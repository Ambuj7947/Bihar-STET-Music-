package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExamUnit
import com.example.data.local.SubmissionReason
import com.example.ui.components.ExamBottomNavBar
import com.example.ui.components.ExamTopHeaderBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CbtTestScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MistakesScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.QuestionBankScreen
import com.example.ui.screens.ScorecardScreen
import com.example.ui.screens.TestsScreen
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StetMusicTheme
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.MainTab

class MainActivity : ComponentActivity() {

    private val examViewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        addOnMultiWindowModeChangedListener { info ->
            if (info.isInMultiWindowMode) {
                examViewModel.onAppBackgroundedDuringTest(SubmissionReason.MULTI_WINDOW_VIOLATION)
            }
        }
        setContent {
            StetMusicTheme {
                StetMusicAppRoot(viewModel = examViewModel)
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        examViewModel.onAppBackgroundedDuringTest(SubmissionReason.APP_SWITCH_OR_EXIT)
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            examViewModel.onAppBackgroundedDuringTest(SubmissionReason.APP_SWITCH_OR_EXIT)
        }
    }
}

@Composable
private fun StetMusicAppRoot(viewModel: ExamViewModel) {
    val bankState by viewModel.bankState.collectAsStateWithLifecycle()
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    val activeCbt by viewModel.activeCbt.collectAsStateWithLifecycle()
    val attempts by viewModel.attempts.collectAsStateWithLifecycle()
    val progressMap by viewModel.progressMap.collectAsStateWithLifecycle()

    if (bankState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = NavyPrimary)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Loading 460 Bihar STET Music Questions...",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        return
    }

    if (bankState.errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Unable to Load Question Bank",
                    color = CrimsonRed,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bankState.errorMessage.orEmpty(),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = viewModel::retryLoadBank,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Retry", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val musicCount = remember(bankState.allQuestions) {
        bankState.allQuestions.count { it.unit == ExamUnit.UNIT_1_MUSIC }
    }
    val gkCount = remember(bankState.allQuestions) {
        bankState.allQuestions.count { it.sectionId.startsWith("gk_") }
    }
    val reasoningCount = remember(bankState.allQuestions) {
        bankState.allQuestions.count { it.sectionId.startsWith("reas_") }
    }

    when (val current = screen) {
        is AppScreen.CbtTest -> {
            val cbt = activeCbt
            if (cbt != null) {
                val isBookmarked = progressMap[cbt.currentQuestion.id]?.isBookmarked == true
                CbtTestScreen(
                    state = cbt,
                    isCurrentBookmarked = isBookmarked,
                    onSelectOption = viewModel::selectOption,
                    onSaveAndNext = viewModel::nextQuestion,
                    onPrevious = viewModel::previousQuestion,
                    onSkip = viewModel::skipAndNext,
                    onClear = viewModel::clearCurrentAnswer,
                    onToggleFlag = viewModel::toggleFlagCurrent,
                    onToggleBookmark = { viewModel.toggleBookmark(cbt.currentQuestion.id) },
                    onJumpToQuestion = viewModel::goToQuestion,
                    onZoomIn = { viewModel.adjustFontScale(10) },
                    onZoomOut = { viewModel.adjustFontScale(-10) },
                    onResetZoom = viewModel::resetFontScale,
                    onSubmitTest = { viewModel.submitActiveTest(SubmissionReason.MANUAL) },
                    onExitAndSubmit = { viewModel.submitActiveTest(SubmissionReason.APP_SWITCH_OR_EXIT) }
                )
            } else {
                LaunchedEffect(Unit) {
                    viewModel.navigateHome()
                }
            }
        }

        is AppScreen.Scorecard -> {
            val attempt = current.attempt
            val questions = remember(attempt.attemptId, bankState.questionById) {
                attempt.questionIdsCsv
                    .split(",")
                    .filter { it.isNotBlank() }
                    .mapNotNull { bankState.questionById[it] }
            }
            val answersMap = remember(attempt.attemptId, attempt.answersJson) {
                viewModel.parseAnswersMap(attempt.answersJson)
            }
            ScorecardScreen(
                attempt = attempt,
                questions = questions,
                answersMap = answersMap,
                progressMap = progressMap,
                onToggleBookmark = viewModel::toggleBookmark,
                onGoHome = viewModel::navigateHome,
                onReattempt = { viewModel.reattemptFromScorecard(attempt) }
            )
        }

        is AppScreen.QuestionBank -> {
            QuestionBankScreen(
                allQuestions = bankState.allQuestions,
                sections = bankState.sections,
                initialSectionId = current.initialSectionId,
                progressMap = progressMap,
                onToggleBookmark = viewModel::toggleBookmark,
                onPracticeFiltered = viewModel::startCustomRevisionTest,
                onBack = viewModel::navigateHome
            )
        }

        is AppScreen.MainTabs -> {
            BackHandler(enabled = current.tab != MainTab.HOME) {
                viewModel.selectTab(MainTab.HOME)
            }

            val (headerTitle, headerSubtitle) = when (current.tab) {
                MainTab.HOME -> stringResource(R.string.header_title_home) to stringResource(R.string.header_subtitle_home)
                MainTab.TESTS -> stringResource(R.string.tests_title) to stringResource(R.string.tests_subtitle)
                MainTab.ANALYTICS -> stringResource(R.string.analytics_title) to stringResource(R.string.analytics_subtitle)
                MainTab.MISTAKES -> stringResource(R.string.mistakes_title) to stringResource(R.string.mistakes_subtitle)
                MainTab.NOTES -> stringResource(R.string.notes_title) to stringResource(R.string.notes_subtitle)
            }

            Scaffold(
                containerColor = AppBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    ExamTopHeaderBar(
                        title = headerTitle,
                        subtitle = headerSubtitle,
                        totalQuestionCount = bankState.allQuestions.size,
                        onQuestionBankClick = { viewModel.openQuestionBank() }
                    )
                },
                bottomBar = {
                    ExamBottomNavBar(
                        selectedTab = current.tab,
                        onSelectTab = { tab -> viewModel.selectTab(tab) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (current.tab) {
                        MainTab.HOME -> HomeScreen(
                            totalQuestions = bankState.allQuestions.size,
                            musicCount = musicCount,
                            gkCount = gkCount,
                            reasoningCount = reasoningCount,
                            fullMocks = bankState.fullMocks,
                            attempts = attempts,
                            progressMap = progressMap,
                            onStartNextMock = viewModel::startNextPendingFullMock,
                            onStartMock = viewModel::startTest,
                            onOpenQuestionBank = { viewModel.openQuestionBank() },
                            onOpenMistakes = { viewModel.selectTab(MainTab.MISTAKES, mistakesSubTab = 0) },
                            onOpenBookmarks = { viewModel.selectTab(MainTab.MISTAKES, mistakesSubTab = 1) },
                            onOpenSectionalTests = { viewModel.selectTab(MainTab.TESTS, testsSubTab = 1) },
                            onOpenNotes = { viewModel.selectTab(MainTab.NOTES) }
                        )

                        MainTab.TESTS -> TestsScreen(
                            initialSubTab = current.testsInitialSubTab,
                            fullMocks = bankState.fullMocks,
                            sectionalTests = bankState.sectionalTests,
                            attempts = attempts,
                            onStartMock = viewModel::startTest
                        )

                        MainTab.ANALYTICS -> AnalyticsScreen(
                            allQuestions = bankState.allQuestions,
                            sections = bankState.sections,
                            attempts = attempts,
                            progressMap = progressMap,
                            onTakeFullMock = viewModel::startNextPendingFullMock,
                            onOpenAttemptScorecard = viewModel::openAttemptScorecard,
                            onPracticeSection = { sectionId -> viewModel.openQuestionBank(sectionId) }
                        )

                        MainTab.MISTAKES -> MistakesScreen(
                            initialSubTab = current.mistakesInitialSubTab,
                            allQuestions = bankState.allQuestions,
                            progressMap = progressMap,
                            onToggleBookmark = viewModel::toggleBookmark,
                            onClearMistake = viewModel::clearMistake,
                            onPracticeQuestions = viewModel::startCustomRevisionTest
                        )

                        MainTab.NOTES -> NotesScreen()
                    }
                }
            }
        }
    }
}
