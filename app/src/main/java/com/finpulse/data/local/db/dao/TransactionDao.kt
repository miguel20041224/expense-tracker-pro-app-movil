package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finpulse.data.local.db.entity.TransactionEntity
import com.finpulse.data.local.db.model.TransactionWithCategoryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id AND user_id = :userId")
    suspend fun getById(id: Long, userId: Long): TransactionEntity?

    @Query(
        """
        SELECT t.*, c.name AS category_name
        FROM transactions t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.user_id = :userId AND t.type = :type
        ORDER BY t.occurred_at DESC
        """,
    )
    fun observeByType(userId: Long, type: String): Flow<List<TransactionWithCategoryRow>>

    @Query(
        """
        SELECT t.*, c.name AS category_name
        FROM transactions t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.user_id = :userId AND t.type = :type
          AND t.occurred_at >= :start AND t.occurred_at < :end
          AND (:categoryId < 0 OR t.category_id = :categoryId)
        ORDER BY t.occurred_at DESC
        """,
    )
    fun observeFiltered(
        userId: Long,
        type: String,
        start: Long,
        end: Long,
        categoryId: Long,
    ): Flow<List<TransactionWithCategoryRow>>

    @Query(
        """
        SELECT COALESCE(SUM(amount_minor), 0)
        FROM transactions
        WHERE user_id = :userId AND type = :type AND occurred_at >= :start AND occurred_at < :end
        """,
    )
    fun observeSumByTypeInRange(userId: Long, type: String, start: Long, end: Long): Flow<Long>
}
