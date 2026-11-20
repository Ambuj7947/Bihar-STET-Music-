package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ExamRepository
import com.example.data.ExamUnit
import com.example.data.QuestionBankLoader
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("STET Music", appName)
    }

    @Test
    fun `question bank loads all 460 verified questions with clean formatting and 10 full mocks`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val questions = QuestionBankLoader.loadAllQuestions(context)

        assertEquals(460, questions.size)
        assertEquals(460, questions.map { it.id }.toSet().size)

        val musicCount = questions.count { it.unit == ExamUnit.UNIT_1_MUSIC }
        val gkCount = questions.count { it.sectionId.startsWith("gk_") }
        val reasCount = questions.count { it.sectionId.startsWith("reas_") }

        assertEquals(277, musicCount)
        assertEquals(101, gkCount)
        assertEquals(82, reasCount)

        val cyrillicRegex = Regex("[\\u0400-\\u04FF\\uFFFD]")
        questions.forEach { q ->
            assertEquals("Question ${q.id} should have 4 options", 4, q.options.size)
            assertTrue("Question ${q.id} answerIndex out of range", q.answerIndex in 0..3)
            assertTrue("Question ${q.id} text blank", q.qHi.isNotBlank() || q.qEn.isNotBlank())
            assertTrue("Question ${q.id} explanation blank", q.explanation.isNotBlank())
            q.options.forEach { opt ->
                assertTrue("Question ${q.id} has blank option", opt.isNotBlank())
                assertFalse("Question ${q.id} has stray markdown in option", opt.contains("**"))
                assertFalse("Question ${q.id} has Cyrillic/corrupt char", cyrillicRegex.containsMatchIn(opt))
            }
            assertEquals(
                "Question ${q.id} has duplicate options: ${q.options}",
                4,
                q.options.map { it.trim().lowercase() }.toSet().size
            )
            assertFalse("Question ${q.id} has Cyrillic/corrupt char in prompt", cyrillicRegex.containsMatchIn(q.qHi + q.qEn + q.explanation))
        }

        val sections = QuestionBankLoader.buildSections(questions)
        assertEquals(15, sections.size)

        val fullMocks = QuestionBankLoader.buildFullLengthMocks(questions)
        val sectionals = QuestionBankLoader.buildSectionalTests(questions, sections)
        assertEquals(10, fullMocks.size)
        assertEquals(15, sectionals.size)

        val qById = questions.associateBy { it.id }
        fullMocks.forEach { mock ->
            assertEquals(150, mock.totalQuestions)
            assertEquals(150, mock.durationMinutes)
            assertEquals(150, mock.questionIds.size)
            assertEquals(150, mock.questionIds.toSet().size)
            val mockQuestions = mock.questionIds.map { qById.getValue(it) }
            assertEquals(100, mockQuestions.count { it.unit == ExamUnit.UNIT_1_MUSIC })
            assertEquals(50, mockQuestions.count { it.unit == ExamUnit.UNIT_2_GENERAL })
        }

        // Verify Mocks 1-3 cover all 277 unique Music questions
        val coveredMusic = fullMocks.take(3).flatMap { it.questionIds }
            .filter { qById.getValue(it).unit == ExamUnit.UNIT_1_MUSIC }.toSet()
        assertEquals(277, coveredMusic.size)

        // Verify Mocks 1-4 cover all 183 unique General (GK + Reasoning) questions
        val coveredGeneral = fullMocks.take(4).flatMap { it.questionIds }
            .filter { qById.getValue(it).unit == ExamUnit.UNIT_2_GENERAL }.toSet()
        assertEquals(183, coveredGeneral.size)
    }

    @Test
    fun `repository saves attempt atomically and tracks mistakes and bookmarks`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = ExamRepository(context)
        val (questions, _, testsPair) = repo.loadQuestionBank()
        val firstSection = testsPair.second.first()
        val sectionQs = firstSection.questionIds.map { id -> questions.first { it.id == id } }

        // Answer Q0 right, Q1 wrong, leave rest skipped
        val wrongChoice = (sectionQs[1].answerIndex + 1) % 4
        val selected = mapOf(
            0 to sectionQs[0].answerIndex,
            1 to wrongChoice
        )

        val attemptId = repo.saveAttemptAndProgress(
            testMeta = firstSection,
            questions = sectionQs,
            selectedAnswers = selected,
            durationSeconds = 95
        )

        val savedAttempt = repo.getAttemptById(attemptId)
        assertNotNull(savedAttempt)
        assertEquals(1, savedAttempt!!.correctCount)
        assertEquals(1, savedAttempt.incorrectCount)
        assertEquals(sectionQs.size - 2, savedAttempt.skippedCount)

        val progress = repo.allProgress.first().associateBy { it.questionId }
        assertFalse(progress.getValue(sectionQs[0].id).isMistake)
        assertTrue(progress.getValue(sectionQs[1].id).isMistake)

        repo.toggleBookmark(sectionQs[0].id)
        repo.clearMistake(sectionQs[1].id)

        val updated = repo.allProgress.first().associateBy { it.questionId }
        assertTrue(updated.getValue(sectionQs[0].id).isBookmarked)
        assertFalse(updated.getValue(sectionQs[1].id).isMistake)
    }
}
