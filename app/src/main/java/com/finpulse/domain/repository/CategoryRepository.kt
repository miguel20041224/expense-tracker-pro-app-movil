package com.finpulse.domain.repository

import com.finpulse.domain.model.Category
import com.finpulse.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeByType(type: TransactionType): Flow<List<Category>>
    suspend fun getDefaultAccountId(): Long
}
