package com.example.data

enum class ExamUnit(val displayName: String, val shortLabel: String) {
    UNIT_1_MUSIC("Unit I: Music (Specific Subject)", "Unit I: Music"),
    UNIT_2_GENERAL("Unit II: Teaching & General Skills", "Unit II: General Skills")
}

data class Question(
    val id: String,
    val unit: ExamUnit,
    val sectionId: String,
    val sectionTitle: String,
    val sectionShort: String,
    val qEn: String,
    val qHi: String,
    val options: List<String>,
    val answerIndex: Int,
    val explanation: String
)

data class SectionInfo(
    val id: String,
    val title: String,
    val shortTitle: String,
    val unit: ExamUnit,
    val accentHex: Long,
    val questionCount: Int
)

data class MockTestMeta(
    val id: String,
    val number: Int,
    val badge: String,
    val title: String,
    val subtitle: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val questionIds: List<String>,
    val isFullLength: Boolean
)

data class RevisionNoteCapsule(
    val id: String,
    val category: String,
    val badge: String,
    val title: String,
    val subtitle: String,
    val highlights: List<String>,
    val tableHeaders: List<String> = emptyList(),
    val tableRows: List<List<String>> = emptyList()
)
