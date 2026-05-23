package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finpulse.data.local.db.entity.BudgetEntity
import com.finpulse.data.local.db.entity.DebtEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.local.db.entity.ProjectionEntity
import com.finpulse.data.local.db.model.BudgetWithCategoryRow
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

    @Query("SELECT COUNT(*) FROM budgets WHERE user_id = :userId")
    fun observeBudgetCount(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals WHERE user_id = :userId")
    fun observeGoalCount(userId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(remaining_minor), 0) FROM debts WHERE user_id = :userId")
    fun observeTotalDebtMinor(userId: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM projections WHERE user_id = :userId")
    fun observeProjectionCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM goals WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeGoals(userId: Long): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id AND user_id = :userId")
    suspend fun getGoalById(id: Long, userId: Long): GoalEntity?

    @Query(
        """
        SELECT b.id, b.category_id, b.amount_minor, b.period, b.start_at, b.end_at,
               c.name AS category_name,
               COALESCE((
                   SELECT SUM(t.amount_minor) FROM transactions t
                   WHERE t.user_id = :userId AND t.category_id = b.category_id AND t.type = 'EXPENSE'
                     AND t.occurred_at >= b.start_at
                     AND (b.end_at IS NULL OR t.occurred_at < b.end_at)
               ), 0) AS spent_minor
        FROM budgets b
        INNER JOIN categories c ON b.category_id = c.id
        WHERE b.user_id = :userId
        ORDER BY c.name ASC
        """,
    )
    fun observeBudgetsWithSpent(userId: Long): Flow<List<BudgetWithCategoryRow>>

    @Query("SELECT * FROM budgets WHERE id = :id AND user_id = :userId")
    suspend fun getBudgetById(id: Long, userId: Long): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
}
