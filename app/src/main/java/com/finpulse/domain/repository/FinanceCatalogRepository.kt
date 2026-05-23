package com.finpulse.domain.repository

import com.finpulse.domain.model.Budget
import com.finpulse.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface FinanceCatalogRepository {
    fun observeBudgets(): Flow<List<Budget>>
    fun observeGoals(): Flow<List<Goal>>
    suspend fun addBudget(categoryId: Long, amountMinor: Long, startAt: Long, endAt: Long?)
    suspend fun addGoal(name: String, targetMinor: Long, savedMinor: Long, deadlineAt: Long?)
    suspend fun deleteBudget(id: Long)
    suspend fun deleteGoal(id: Long)
}
