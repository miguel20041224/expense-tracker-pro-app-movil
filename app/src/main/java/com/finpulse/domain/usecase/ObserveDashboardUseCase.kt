package com.finpulse.domain.usecase

import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDashboardUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) {
    operator fun invoke(): Flow<DashboardSnapshot> = dashboardRepository.observeDashboard()
}
