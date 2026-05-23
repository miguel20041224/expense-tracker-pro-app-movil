package com.finpulse.domain.usecase

import com.finpulse.domain.repository.DashboardRepository
import javax.inject.Inject

class RefreshFinancialIntelligenceUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) {
    suspend operator fun invoke() = dashboardRepository.refreshIntelligence()
}
