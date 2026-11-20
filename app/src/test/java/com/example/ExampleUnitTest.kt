package com.example

import com.example.data.ExamUnit
import com.example.data.MockTestMeta
import com.example.data.Question
import com.example.data.RevisionNotesData
import com.example.ui.components.formatDurationHms
import com.example.ui.components.formatDurationMs
import com.example.ui.components.formatDurationReadable
import com.example.viewmodel.ActiveCbtState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `duration formatters handle zero, normal, and negative values`() {
        assertEquals("00:00:00", formatDurationHms(0))
        assertEquals("00:00:00", formatDurationHms(-45))
        assertEquals("02:30:00", formatDurationHms(150 * 60))
        assertEquals("01:05:09", formatDurationHms(3600 + 5 * 60 + 9))

        assertEquals("00:00", formatDurationMs(0))
        assertEquals("03:45", formatDurationMs(225))

        assertEquals("00m 00s", formatDurationReadable(0))
        assertEquals("12m 05s", formatDurationReadable(12 * 60 + 5))
    }

    @Test
    fun `active cbt state calculates unit ranges and progress accurately`() {
        val q1 = Question("M1", ExamUnit.UNIT_1_MUSIC, "music_p1", "Music", "Music", "", "Q1", listOf("A", "B", "C", "D"), 0, "Exp")
        val q2 = Question("M2", ExamUnit.UNIT_1_MUSIC, "music_p1", "Music", "Music", "", "Q2", listOf("A", "B", "C", "D"), 1, "Exp")
        val q3 = Question("G1", ExamUnit.UNIT_2_GENERAL, "gk_p1", "GK", "GK", "Q3", "Q3", listOf("A", "B", "C", "D"), 2, "Exp")
        val meta = MockTestMeta("m1", 1, "MOCK 01", "Mock #01", "Sub", 3, 10, listOf("M1", "M2", "G1"), false)

        val state = ActiveCbtState(
            testMeta = meta,
            questions = listOf(q1, q2, q3),
            remainingSeconds = 600,
            selectedAnswers = mapOf(0 to 0, 2 to 2)
        )

        assertEquals(2, state.answeredCount)
        assertEquals(3, state.totalCount)
        assertEquals(66, state.progressPercent)
        assertEquals(0..1, state.unit1Indices)
        assertEquals(2..2, state.unit2Indices)
    }

    @Test
    fun `revision notes contain valid content with no Cyrillic or stray markdown`() {
        val cyrillicRegex = Regex("[\\u0400-\\u04FF]")
        assertEquals(7, RevisionNotesData.capsules.size)
        RevisionNotesData.capsules.forEach { capsule ->
            assertTrue(capsule.title.isNotBlank())
            assertTrue(capsule.highlights.isNotEmpty())
            capsule.highlights.forEach { bullet ->
                assertFalse("Cyrillic found in ${capsule.id}: $bullet", cyrillicRegex.containsMatchIn(bullet))
                assertFalse("Markdown stars found in ${capsule.id}: $bullet", bullet.contains("**"))
            }
        }
    }
}
