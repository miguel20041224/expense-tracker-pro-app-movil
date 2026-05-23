package com.finpulse.domain.usecase

import com.finpulse.domain.repository.FinancialIntelligenceRepository
import javax.inject.Inject

class RefreshFinancialIntelligenceUseCase @Inject constructor(
    private val intelligenceRepository: FinancialIntelligenceRepository,
) {
    suspend operator fun invoke() = intelligenceRepository.refresh()
}
