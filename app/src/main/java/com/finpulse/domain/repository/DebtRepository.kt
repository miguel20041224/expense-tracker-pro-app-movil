package com.finpulse.domain.repository

import com.finpulse.domain.model.Debt
import com.finpulse.domain.model.SnowballPlan
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun observeAll(): Flow<List<Debt>>
    fun observeSnowballPlan(extraMonthlyMinor: Long = 0): Flow<SnowballPlan>
    suspend fun getById(id: Long): Debt?
    suspend fun save(debt: Debt): Long
    suspend fun delete(id: Long)
}
