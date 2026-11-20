package com.example.data

import android.content.Context
import org.json.JSONArray
import kotlin.random.Random

object QuestionBankLoader {

    private val assetFiles = listOf(
        "questions_music_1.json",
        "questions_music_2.json",
        "questions_gk.json",
        "questions_reasoning.json"
    )

    private val sectionAccents = mapOf(
        "music_p1" to 0xFF2563EB,
        "music_p2" to 0xFF0D9488,
        "music_p3" to 0xFF7C3AED,
        "music_p4" to 0xFFD97706,
        "music_p5" to 0xFFE11D48,
        "gk_p1" to 0xFF0284C7,
        "gk_p2" to 0xFF059669,
        "gk_p3" to 0xFF4F46E5,
        "gk_p4" to 0xFFEA580C,
        "reas_p1" to 0xFF9333EA,
        "reas_p2" to 0xFF0891B2,
        "reas_p3" to 0xFF2563EB,
        "reas_p4" to 0xFF16A34A,
        "reas_p5" to 0xFFCA8A04,
        "reas_p6" to 0xFFDC2626
    )

    fun loadAllQuestions(context: Context): List<Question> {
        val result = mutableListOf<Question>()
        for (fileName in assetFiles) {
            val jsonText = context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { it.readText() }
            val array = JSONArray(jsonText)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val optsJson = obj.getJSONArray("options")
                val options = List(optsJson.length()) { idx -> optsJson.getString(idx) }
                val unit = if (obj.getString("unit") == "UNIT_1_MUSIC") {
                    ExamUnit.UNIT_1_MUSIC
                } else {
                    ExamUnit.UNIT_2_GENERAL
                }
                result.add(
                    Question(
                        id = obj.getString("id"),
                        unit = unit,
                        sectionId = obj.getString("sectionId"),
                        sectionTitle = obj.getString("sectionTitle"),
                        sectionShort = obj.getString("sectionShort"),
                        qEn = obj.optString("qEn", ""),
                        qHi = obj.optString("qHi", ""),
                        options = options,
                        answerIndex = obj.getInt("answer"),
                        explanation = obj.optString("exp", "")
                    )
                )
            }
        }
        return result
    }

    fun buildSections(questions: List<Question>): List<SectionInfo> {
        val grouped = questions.groupBy { it.sectionId }
        return grouped.entries.map { (sectionId, list) ->
            val first = list.first()
            SectionInfo(
                id = sectionId,
                title = first.sectionTitle,
                shortTitle = first.sectionShort,
                unit = first.unit,
                accentHex = sectionAccents[sectionId] ?: 0xFF0D3B7A,
                questionCount = list.size
            )
        }
    }

    /**
     * Generates 10 Full-Length Final CBT Mock Tests strictly using ONLY the 460 attached questions.
     * Official Bihar STET Paper 1 Music pattern:
     * - 150 Questions | 150 Marks | 150 Minutes | No Negative Marking
     * - Q1..100: Unit I — Music (Specific Subject) from the 277 Music questions
     * - Q101..150: Unit II — General Skills (50 Qs balanced from 101 GK/Bihar GK/Science + 82 Reasoning)
     */
    fun buildFullLengthMocks(questions: List<Question>): List<MockTestMeta> {
        val musicQs = questions.filter { it.unit == ExamUnit.UNIT_1_MUSIC }
        val gkQs = questions.filter { it.sectionId.startsWith("gk_") }
        val reasQs = questions.filter { it.sectionId.startsWith("reas_") }

        val mockThemes = listOf(
            "Official Bihar STET CBT Pattern • Shruti, Swar, Raga & Bihar GK Focus",
            "Samgaan, Granths, History, Polity & Logical Reasoning Paper",
            "Gayaki, Instruments, Taal Notation & General Science Paper",
            "Complete Coverage Sprint • Balanced All-Units Mixed CBT Paper",
            "High-Yield Shruti, Murchhana, Gharana & Bihar Heritage Mock",
            "Raga-Thaat, Vadi-Samvadi, Constitution & Coding-Decoding Mock",
            "Taal-Lipi, Bhatkhande-Paluskar, Geography & Direction Test Mock",
            "Classical Granthkar, Folk Music, Science & Series-Analogy Mock",
            "Pre-Exam Speed & Accuracy Booster • Full Syllabus CBT Mock",
            "Grand Finale Bihar STET Music Paper 1 • Ultimate 150-Q CBT"
        )

        val mocks = mutableListOf<MockTestMeta>()
        for (idx in 0 until 10) {
            val number = idx + 1
            val selectedMusic: List<Question> = when (idx) {
                // Mocks 1, 2, 3 guarantee 100% coverage of all 277 unique Music questions!
                0 -> musicQs.subList(0, 100)
                1 -> musicQs.subList(100, 200)
                2 -> musicQs.subList(200, musicQs.size) + musicQs.subList(0, 23)
                else -> musicQs.shuffled(Random(1000 + number * 37)).take(100)
            }

            // 50 Unit II questions: 28 GK/Bihar GK/Polity/Science/EVS + 22 Reasoning/Math
            // Mocks 1..4 systematically cover 100% of all 101 GK + 82 Reasoning questions!
            val selectedGk: List<Question> = when (idx) {
                0 -> gkQs.subList(0, 28)
                1 -> gkQs.subList(28, 56)
                2 -> gkQs.subList(56, 84)
                3 -> gkQs.subList(84, gkQs.size) + gkQs.subList(0, 11)
                else -> gkQs.shuffled(Random(2000 + number * 53)).take(28)
            }

            val selectedReas: List<Question> = when (idx) {
                0 -> reasQs.subList(0, 22)
                1 -> reasQs.subList(22, 44)
                2 -> reasQs.subList(44, 66)
                3 -> reasQs.subList(66, reasQs.size) + reasQs.subList(0, 6)
                else -> reasQs.shuffled(Random(3000 + number * 71)).take(22)
            }

            val combinedIds = (selectedMusic + selectedGk + selectedReas).map { it.id }
            val numFormatted = number.toString().padStart(2, '0')
            mocks.add(
                MockTestMeta(
                    id = "full_mock_$numFormatted",
                    number = number,
                    badge = "MOCK TEST $numFormatted",
                    title = "Final Mock Test #$numFormatted",
                    subtitle = mockThemes[idx],
                    totalQuestions = combinedIds.size,
                    durationMinutes = 150,
                    questionIds = combinedIds,
                    isFullLength = true
                )
            )
        }
        return mocks
    }

    fun buildSectionalTests(questions: List<Question>, sections: List<SectionInfo>): List<MockTestMeta> {
        return sections.mapIndexed { index, sec ->
            val secQs = questions.filter { it.sectionId == sec.id }
            val mins = maxOf(10, secQs.size)
            val num = index + 1
            MockTestMeta(
                id = "sec_${sec.id}",
                number = num,
                badge = if (sec.unit == ExamUnit.UNIT_1_MUSIC) "UNIT I • MUSIC" else "UNIT II • GENERAL",
                title = sec.shortTitle,
                subtitle = sec.title,
                totalQuestions = secQs.size,
                durationMinutes = mins,
                questionIds = secQs.map { it.id },
                isFullLength = false
            )
        }
    }
}
