package com.finpulse.domain.repository

interface FinancialIntelligenceRepository {
    suspend fun refresh()
}
