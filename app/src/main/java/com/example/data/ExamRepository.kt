package com.example.data

import android.content.Context
import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.local.QuestionProgressEntity
import com.example.data.local.SubmissionReason
import com.example.data.local.TestAttemptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ExamRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val dao = database.examDao()
    private val appContext = context.applicationContext

    val allAttempts: Flow<List<TestAttemptEntity>> = dao.observeAllAttempts()
    val allProgress: Flow<List<QuestionProgressEntity>> = dao.observeAllProgress()

    suspend fun loadQuestionBank(): Triple<List<Question>, List<SectionInfo>, Pair<List<MockTestMeta>, List<MockTestMeta>>> =
        withContext(Dispatchers.IO) {
            val questions = QuestionBankLoader.loadAllQuestions(appContext)
            val sections = QuestionBankLoader.buildSections(questions)
            val fullMocks = QuestionBankLoader.buildFullLengthMocks(questions)
            val sectionalTests = QuestionBankLoader.buildSectionalTests(questions, sections)
            Triple(questions, sections, fullMocks to sectionalTests)
        }

    suspend fun saveAttemptAndProgress(
        testMeta: MockTestMeta,
        questions: List<Question>,
        selectedAnswers: Map<Int, Int>,
        durationSeconds: Int,
        submissionReason: SubmissionReason = SubmissionReason.MANUAL
    ): Long = withContext(Dispatchers.IO) {
        var correct = 0
        var incorrect = 0
        var skipped = 0
        var u1Correct = 0
        var u1Wrong = 0
        var u1Total = 0
        var u2Correct = 0
        var u2Wrong = 0
        var u2Total = 0

        val answersJsonObj = JSONObject()
        val now = System.currentTimeMillis()

        database.withTransaction {
            val answeredIds = questions.mapIndexedNotNull { idx, q ->
                if (selectedAnswers[idx] != null) q.id else null
            }
            val existingById = if (answeredIds.isEmpty()) {
                emptyMap()
            } else {
                dao.getProgressByIds(answeredIds).associateBy { it.questionId }
            }

            val progressUpdates = mutableListOf<QuestionProgressEntity>()

            questions.forEachIndexed { idx, q ->
                val selected = selectedAnswers[idx]
                if (q.unit == ExamUnit.UNIT_1_MUSIC) u1Total++ else u2Total++

                if (selected == null) {
                    skipped++
                } else {
                    answersJsonObj.put(q.id, selected)
                    val isCorrect = selected == q.answerIndex
                    if (isCorrect) {
                        correct++
                        if (q.unit == ExamUnit.UNIT_1_MUSIC) u1Correct++ else u2Correct++
                    } else {
                        incorrect++
                        if (q.unit == ExamUnit.UNIT_1_MUSIC) u1Wrong++ else u2Wrong++
                    }

                    val existing = existingById[q.id]
                    progressUpdates.add(
                        QuestionProgressEntity(
                            questionId = q.id,
                            isBookmarked = existing?.isBookmarked ?: false,
                            isMistake = !isCorrect,
                            timesAttempted = (existing?.timesAttempted ?: 0) + 1,
                            timesCorrect = (existing?.timesCorrect ?: 0) + if (isCorrect) 1 else 0,
                            lastSelectedIndex = selected,
                            updatedAt = now
                        )
                    )
                }
            }

            if (progressUpdates.isNotEmpty()) {
                dao.upsertProgressList(progressUpdates)
            }

            val attempt = TestAttemptEntity(
                testId = testMeta.id,
                testTitle = testMeta.title,
                isFullLength = testMeta.isFullLength,
                timestamp = now,
                durationSeconds = durationSeconds,
                totalQuestions = questions.size,
                correctCount = correct,
                incorrectCount = incorrect,
                skippedCount = skipped,
                unit1Correct = u1Correct,
                unit1Wrong = u1Wrong,
                unit1Total = u1Total,
                unit2Correct = u2Correct,
                unit2Wrong = u2Wrong,
                unit2Total = u2Total,
                questionIdsCsv = questions.joinToString(",") { it.id },
                answersJson = answersJsonObj.toString(),
                submissionReason = submissionReason.name
            )
            dao.insertAttempt(attempt)
        }
    }

    suspend fun getAttemptById(attemptId: Long): TestAttemptEntity? = withContext(Dispatchers.IO) {
        dao.getAttemptById(attemptId)
    }

    suspend fun toggleBookmark(questionId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getProgress(questionId)
        val updated = existing?.copy(
            isBookmarked = !existing.isBookmarked,
            updatedAt = System.currentTimeMillis()
        ) ?: QuestionProgressEntity(
            questionId = questionId,
            isBookmarked = true
        )
        dao.upsertProgress(updated)
    }

    suspend fun clearMistake(questionId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getProgress(questionId) ?: return@withContext
        dao.upsertProgress(existing.copy(isMistake = false, updatedAt = System.currentTimeMillis()))
    }
}
