package com.finpulse.ui.feature.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.Debt
import com.finpulse.domain.model.SnowballPlan
import com.finpulse.domain.repository.DebtRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    val debts = debtRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val snowballPlan = debtRepository.observeSnowballPlan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SnowballPlan(emptyList(), 0, 0, 0, null, 0))

    fun delete(debt: Debt) {
        viewModelScope.launch {
            debtRepository.delete(debt.id)
            refreshIntelligence()
        }
    }
}
