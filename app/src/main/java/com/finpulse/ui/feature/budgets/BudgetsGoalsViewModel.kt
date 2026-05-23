package com.finpulse.ui.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.repository.FinanceCatalogRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetsGoalsViewModel @Inject constructor(
    private val financeCatalog: FinanceCatalogRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    val budgets = financeCatalog.observeBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goals = financeCatalog.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            financeCatalog.deleteBudget(id)
            refreshIntelligence()
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            financeCatalog.deleteGoal(id)
        }
    }
}
