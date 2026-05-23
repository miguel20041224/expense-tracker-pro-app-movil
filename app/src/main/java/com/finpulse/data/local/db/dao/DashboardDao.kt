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
        WHERE type = 'INCOME' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    fun observeIncomeMinor(start: Long, end: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE type = 'EXPENSE' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    fun observeExpenseMinor(start: Long, end: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE type = 'INCOME' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    suspend fun incomeMinorOnce(start: Long, end: Long): Long

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE type = 'EXPENSE' AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    suspend fun expenseMinorOnce(start: Long, end: Long): Long

    @Query(
        """
        SELECT strftime('%Y-%m', occurred_at / 1000, 'unixepoch') AS monthKey,
               COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_minor ELSE 0 END), 0) AS incomeMinor,
               COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_minor ELSE 0 END), 0) AS expenseMinor
        FROM transactions
        WHERE occurred_at >= :since
        GROUP BY monthKey
        ORDER BY monthKey ASC
        """,
    )
    fun observeMonthlyCashFlow(since: Long): Flow<List<MonthlyCashFlowRow>>
}
