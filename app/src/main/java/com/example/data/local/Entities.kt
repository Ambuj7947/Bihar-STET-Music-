package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val attemptId: Long = 0L,
    val testId: String,
    val testTitle: String,
    val isFullLength: Boolean,
    val timestamp: Long,
    val durationSeconds: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val unit1Correct: Int,
    val unit1Wrong: Int,
    val unit1Total: Int,
    val unit2Correct: Int,
    val unit2Wrong: Int,
    val unit2Total: Int,
    val questionIdsCsv: String,
    val answersJson: String
)

@Entity(tableName = "question_progress")
data class QuestionProgressEntity(
    @PrimaryKey val questionId: String,
    val isBookmarked: Boolean = false,
    val isMistake: Boolean = false,
    val timesAttempted: Int = 0,
    val timesCorrect: Int = 0,
    val lastSelectedIndex: Int = -1,
    val updatedAt: Long = System.currentTimeMillis()
)
