package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finpulse.data.local.db.entity.AlertEntity
import com.finpulse.data.local.db.entity.FinancialInsightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    @Query("DELETE FROM financial_insights WHERE user_id = :userId")
    suspend fun clearInsights(userId: Long)

    @Query("DELETE FROM alerts WHERE user_id = :userId")
    suspend fun clearAlerts(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsights(insights: List<FinancialInsightEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>)

    @Query("SELECT * FROM financial_insights WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    fun observeRecentInsights(userId: Long, limit: Int): Flow<List<FinancialInsightEntity>>

    @Query("SELECT COUNT(*) FROM alerts WHERE user_id = :userId AND is_read = 0")
    fun observeUnreadAlertCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM alerts WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeAlerts(userId: Long): Flow<List<AlertEntity>>

    @Query("UPDATE alerts SET is_read = 1 WHERE id = :id AND user_id = :userId")
    suspend fun markRead(id: Long, userId: Long)

    @Query("UPDATE alerts SET is_read = 1 WHERE user_id = :userId")
    suspend fun markAllRead(userId: Long)
}
