package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.finpulse.data.local.db.model.MonthlyCashFlowRow
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {
    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE user_id = :userId AND type = 'INCOME' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    fun observeIncomeMinor(userId: Long, start: Long, end: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE user_id = :userId AND type = 'EXPENSE' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    fun observeExpenseMinor(userId: Long, start: Long, end: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE user_id = :userId AND type = 'INCOME' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    suspend fun incomeMinorOnce(userId: Long, start: Long, end: Long): Long

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE user_id = :userId AND type = 'EXPENSE' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    suspend fun expenseMinorOnce(userId: Long, start: Long, end: Long): Long

    @Query(
        """
        SELECT strftime('%Y-%m', occurred_at / 1000, 'unixepoch') AS monthKey,
               COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_minor ELSE 0 END), 0) AS incomeMinor,
               COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_minor ELSE 0 END), 0) AS expenseMinor
        FROM transactions
        WHERE user_id = :userId AND occurred_at >= :since
        GROUP BY monthKey
        ORDER BY monthKey ASC
        """,
    )
    fun observeMonthlyCashFlow(userId: Long, since: Long): Flow<List<MonthlyCashFlowRow>>
}
