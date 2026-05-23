package com.finpulse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finpulse.data.local.db.entity.AccountEntity
import com.finpulse.data.local.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("SELECT id FROM accounts WHERE user_id = :userId AND is_active = 1 ORDER BY id LIMIT 1")
    suspend fun getDefaultAccountId(userId: Long): Long?

    @Query("SELECT COUNT(*) FROM categories WHERE user_id = :userId")
    suspend fun getCategoryCount(userId: Long): Int

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type ORDER BY name ASC")
    fun observeCategoriesByType(userId: Long, type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE user_id = :userId ORDER BY type, name")
    fun observeAllCategories(userId: Long): Flow<List<CategoryEntity>>
}
