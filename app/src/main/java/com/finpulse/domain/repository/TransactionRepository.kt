package com.finpulse.domain.repository

import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeByType(type: TransactionType): Flow<List<Transaction>>
    fun observeFiltered(
        type: TransactionType,
        startEpochMilli: Long,
        endEpochMilli: Long,
        categoryId: Long?,
    ): Flow<List<Transaction>>
    suspend fun getById(id: Long): Transaction?
    suspend fun save(transaction: Transaction): Long
    suspend fun delete(id: Long)
}
