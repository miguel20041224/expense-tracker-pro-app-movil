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
    @Query("DELETE FROM financial_insights")
    suspend fun clearInsights()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsights(insights: List<FinancialInsightEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>)

    @Query("SELECT * FROM financial_insights ORDER BY created_at DESC LIMIT :limit")
    fun observeRecentInsights(limit: Int): Flow<List<FinancialInsightEntity>>

    @Query("SELECT COUNT(*) FROM alerts WHERE is_read = 0")
    fun observeUnreadAlertCount(): Flow<Int>
}
