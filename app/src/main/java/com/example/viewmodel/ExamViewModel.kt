package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExamRepository
import com.example.data.ExamUnit
import com.example.data.MockTestMeta
import com.example.data.Question
import com.example.data.SectionInfo
import com.example.data.local.QuestionProgressEntity
import com.example.data.local.SubmissionReason
import com.example.data.local.TestAttemptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class MainTab {
    HOME, TESTS, ANALYTICS, MISTAKES, NOTES
}

sealed interface AppScreen {
    data class MainTabs(
        val tab: MainTab = MainTab.HOME,
        val testsInitialSubTab: Int = 0,
        val mistakesInitialSubTab: Int = 0
    ) : AppScreen

    data object CbtTest : AppScreen
    data class Scorecard(val attempt: TestAttemptEntity) : AppScreen
    data class QuestionBank(val initialSectionId: String? = null) : AppScreen
}

data class ActiveCbtState(
    val testMeta: MockTestMeta,
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val remainingSeconds: Int,
    val elapsedSeconds: Int = 0,
    val currentQuestionSeconds: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val visitedIndices: Set<Int> = setOf(0),
    val flaggedIndices: Set<Int> = emptySet(),
    val fontScalePercent: Int = 100
) {
    val answeredCount: Int get() = selectedAnswers.size
    val totalCount: Int get() = questions.size
    val progressPercent: Int get() = if (totalCount == 0) 0 else (answeredCount * 100) / totalCount
    val currentQuestion: Question get() = questions[currentIndex]

    val unit1Indices: IntRange?
        get() {
            val first = questions.indexOfFirst { it.unit == ExamUnit.UNIT_1_MUSIC }
            val last = questions.indexOfLast { it.unit == ExamUnit.UNIT_1_MUSIC }
            return if (first >= 0 && last >= first) first..last else null
        }

    val unit2Indices: IntRange?
        get() {
            val first = questions.indexOfFirst { it.unit == ExamUnit.UNIT_2_GENERAL }
            val last = questions.indexOfLast { it.unit == ExamUnit.UNIT_2_GENERAL }
            return if (first >= 0 && last >= first) first..last else null
        }
}

data class BankUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val allQuestions: List<Question> = emptyList(),
    val questionById: Map<String, Question> = emptyMap(),
    val sections: List<SectionInfo> = emptyList(),
    val fullMocks: List<MockTestMeta> = emptyList(),
    val sectionalTests: List<MockTestMeta> = emptyList()
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExamRepository(application)

    private val _bankState = MutableStateFlow(BankUiState())
    val bankState: StateFlow<BankUiState> = _bankState.asStateFlow()

    private val _screen = MutableStateFlow<AppScreen>(AppScreen.MainTabs(MainTab.HOME))
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _activeCbt = MutableStateFlow<ActiveCbtState?>(null)
    val activeCbt: StateFlow<ActiveCbtState?> = _activeCbt.asStateFlow()

    val attempts: StateFlow<List<TestAttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val progressMap: StateFlow<Map<String, QuestionProgressEntity>> = repository.allProgress
        .map { list -> list.associateBy { it.questionId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timerJob: Job? = null
    @Volatile
    private var isSubmitting: Boolean = false

    init {
        loadBank()
    }

    fun retryLoadBank() {
        loadBank()
    }

    private fun loadBank() {
        _bankState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val (questions, sections, testsPair) = repository.loadQuestionBank()
                _bankState.value = BankUiState(
                    isLoading = false,
                    errorMessage = null,
                    allQuestions = questions,
                    questionById = questions.associateBy { it.id },
                    sections = sections,
                    fullMocks = testsPair.first,
                    sectionalTests = testsPair.second
                )
            } catch (e: Exception) {
                _bankState.value = BankUiState(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load question bank."
                )
            }
        }
    }

    fun selectTab(tab: MainTab, testsSubTab: Int = 0, mistakesSubTab: Int = 0) {
        _screen.value = AppScreen.MainTabs(tab, testsSubTab, mistakesSubTab)
    }

    fun openQuestionBank(sectionId: String? = null) {
        _screen.value = AppScreen.QuestionBank(sectionId)
    }

    fun navigateHome() {
        stopTimer()
        isSubmitting = false
        _activeCbt.value = null
        _screen.value = AppScreen.MainTabs(MainTab.HOME)
    }

    fun startTest(meta: MockTestMeta) {
        val qMap = _bankState.value.questionById
        val testQuestions = meta.questionIds.mapNotNull { qMap[it] }
        if (testQuestions.isEmpty()) return

        stopTimer()
        isSubmitting = false
        _activeCbt.value = ActiveCbtState(
            testMeta = meta,
            questions = testQuestions,
            currentIndex = 0,
            remainingSeconds = meta.durationMinutes * 60,
            elapsedSeconds = 0,
            currentQuestionSeconds = 0,
            selectedAnswers = emptyMap(),
            visitedIndices = setOf(0),
            flaggedIndices = emptySet(),
            fontScalePercent = 100
        )
        _screen.value = AppScreen.CbtTest
        startTimer()
    }

    fun startNextPendingFullMock() {
        val completedIds = attempts.value.filter { it.isFullLength }.map { it.testId }.toSet()
        val nextMock = _bankState.value.fullMocks.firstOrNull { it.id !in completedIds }
            ?: _bankState.value.fullMocks.firstOrNull()
        if (nextMock != null) {
            startTest(nextMock)
        }
    }

    fun startCustomRevisionTest(title: String, questionIds: List<String>) {
        if (questionIds.isEmpty()) return
        val meta = MockTestMeta(
            id = "custom_${System.currentTimeMillis()}",
            number = 0,
            badge = "TARGETED REVISION",
            title = title,
            subtitle = "Custom Practice Drill • ${questionIds.size} Selected Qs",
            totalQuestions = questionIds.size,
            durationMinutes = maxOf(10, questionIds.size),
            questionIds = questionIds,
            isFullLength = false
        )
        startTest(meta)
    }

    fun reattemptFromScorecard(attempt: TestAttemptEntity) {
        val existingMeta = (_bankState.value.fullMocks + _bankState.value.sectionalTests)
            .firstOrNull { it.id == attempt.testId }
        if (existingMeta != null) {
            startTest(existingMeta)
        } else {
            val ids = attempt.questionIdsCsv.split(",").filter { it.isNotBlank() }
            startCustomRevisionTest(attempt.testTitle, ids)
        }
    }

    fun openAttemptScorecard(attempt: TestAttemptEntity) {
        _screen.value = AppScreen.Scorecard(attempt)
    }

    fun selectOption(optionIndex: Int) {
        _activeCbt.update { state ->
            state?.copy(selectedAnswers = state.selectedAnswers + (state.currentIndex to optionIndex))
        }
    }

    fun clearCurrentAnswer() {
        _activeCbt.update { state ->
            state?.copy(selectedAnswers = state.selectedAnswers - state.currentIndex)
        }
    }

    fun toggleFlagCurrent() {
        _activeCbt.update { state ->
            if (state == null) return@update null
            val idx = state.currentIndex
            val updated = if (idx in state.flaggedIndices) {
                state.flaggedIndices - idx
            } else {
                state.flaggedIndices + idx
            }
            state.copy(flaggedIndices = updated)
        }
    }

    fun goToQuestion(index: Int) {
        _activeCbt.update { state ->
            if (state == null || index !in state.questions.indices) return@update state
            state.copy(
                currentIndex = index,
                currentQuestionSeconds = 0,
                visitedIndices = state.visitedIndices + index
            )
        }
    }

    fun nextQuestion() {
        val state = _activeCbt.value ?: return
        if (state.currentIndex + 1 < state.questions.size) {
            goToQuestion(state.currentIndex + 1)
        }
    }

    fun previousQuestion() {
        val state = _activeCbt.value ?: return
        if (state.currentIndex > 0) {
            goToQuestion(state.currentIndex - 1)
        }
    }

    fun skipAndNext() {
        _activeCbt.update { state ->
            if (state == null) return@update null
            val cleared = state.selectedAnswers - state.currentIndex
            val nextIdx = if (state.currentIndex < state.questions.lastIndex) {
                state.currentIndex + 1
            } else {
                // On final question, jump to first remaining unanswered question if any
                state.questions.indices.firstOrNull { it != state.currentIndex && it !in cleared }
                    ?: state.currentIndex
            }
            state.copy(
                selectedAnswers = cleared,
                currentIndex = nextIdx,
                currentQuestionSeconds = 0,
                visitedIndices = state.visitedIndices + state.currentIndex + nextIdx
            )
        }
    }

    fun adjustFontScale(delta: Int) {
        _activeCbt.update { state ->
            state?.copy(fontScalePercent = (state.fontScalePercent + delta).coerceIn(75, 130))
        }
    }

    fun resetFontScale() {
        _activeCbt.update { state -> state?.copy(fontScalePercent = 100) }
    }

    fun toggleBookmark(questionId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId)
        }
    }

    fun clearMistake(questionId: String) {
        viewModelScope.launch {
            repository.clearMistake(questionId)
        }
    }

    fun onAppBackgroundedDuringTest(
        reason: SubmissionReason = SubmissionReason.APP_SWITCH_OR_EXIT
    ) {
        if (_screen.value is AppScreen.CbtTest && _activeCbt.value != null) {
            submitActiveTest(reason)
        }
    }

    fun submitActiveTest(reason: SubmissionReason = SubmissionReason.MANUAL) {
        if (isSubmitting) return
        val state = _activeCbt.value ?: return
        isSubmitting = true
        stopTimer()
        persistenceScope.launch {
            try {
                val attemptId = repository.saveAttemptAndProgress(
                    testMeta = state.testMeta,
                    questions = state.questions,
                    selectedAnswers = state.selectedAnswers,
                    durationSeconds = state.elapsedSeconds,
                    submissionReason = reason
                )
                val savedAttempt = repository.getAttemptById(attemptId)
                _activeCbt.value = null
                if (savedAttempt != null) {
                    _screen.value = AppScreen.Scorecard(savedAttempt)
                } else {
                    _screen.value = AppScreen.MainTabs(MainTab.HOME)
                }
            } finally {
                isSubmitting = false
            }
        }
    }

    fun parseAnswersMap(answersJson: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        try {
            val obj = JSONObject(answersJson)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k] = obj.getInt(k)
            }
        } catch (_: Exception) {
        }
        return map
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _activeCbt.value ?: break
                val nextRemaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                _activeCbt.value = current.copy(
                    remainingSeconds = nextRemaining,
                    elapsedSeconds = current.elapsedSeconds + 1,
                    currentQuestionSeconds = current.currentQuestionSeconds + 1
                )
                if (nextRemaining == 0) {
                    submitActiveTest(SubmissionReason.TIME_UP)
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
