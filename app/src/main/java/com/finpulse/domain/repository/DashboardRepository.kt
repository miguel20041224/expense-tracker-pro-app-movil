package com.finpulse.domain.repository

import com.finpulse.domain.model.DashboardSnapshot
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboard(): Flow<DashboardSnapshot>
    suspend fun refreshIntelligence()
}
