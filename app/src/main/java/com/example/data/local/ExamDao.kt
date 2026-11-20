package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
    fun observeAllAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE attemptId = :attemptId LIMIT 1")
    suspend fun getAttemptById(attemptId: Long): TestAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: TestAttemptEntity): Long

    @Query("SELECT * FROM question_progress")
    fun observeAllProgress(): Flow<List<QuestionProgressEntity>>

    @Query("SELECT * FROM question_progress WHERE questionId = :questionId LIMIT 1")
    suspend fun getProgress(questionId: String): QuestionProgressEntity?

    @Query("SELECT * FROM question_progress WHERE questionId IN (:questionIds)")
    suspend fun getProgressByIds(questionIds: List<String>): List<QuestionProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: QuestionProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgressList(list: List<QuestionProgressEntity>)
}
