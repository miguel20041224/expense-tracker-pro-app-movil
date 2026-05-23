package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finpulse.data.local.db.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: DebtEntity): Long

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(debt: DebtEntity)

    @Query("SELECT * FROM debts WHERE id = :id AND user_id = :userId")
    suspend fun getById(id: Long, userId: Long): DebtEntity?

    @Query("SELECT * FROM debts WHERE user_id = :userId ORDER BY remaining_minor ASC, interest_rate DESC")
    fun observeAllOrderedForSnowball(userId: Long): Flow<List<DebtEntity>>

    @Query("SELECT COALESCE(SUM(remaining_minor), 0) FROM debts WHERE user_id = :userId")
    fun observeTotalRemainingMinor(userId: Long): Flow<Long>
}
