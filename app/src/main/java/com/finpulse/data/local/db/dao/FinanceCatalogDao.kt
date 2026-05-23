package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finpulse.data.local.db.entity.BudgetEntity
import com.finpulse.data.local.db.entity.DebtEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.local.db.entity.ProjectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceCatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(items: List<BudgetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(items: List<GoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(items: List<DebtEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjections(items: List<ProjectionEntity>)

    @Query("SELECT COUNT(*) FROM budgets")
    fun observeBudgetCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals")
    fun observeGoalCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(remaining_minor), 0) FROM debts")
    fun observeTotalDebtMinor(): Flow<Long>

    @Query("SELECT COUNT(*) FROM projections")
    fun observeProjectionCount(): Flow<Int>
}
