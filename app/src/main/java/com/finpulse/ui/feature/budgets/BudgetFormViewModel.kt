package com.finpulse.ui.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.FinanceCatalogRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import com.finpulse.util.AmountParser
import com.finpulse.util.DateRanges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetFormUiState(
    val amountInput: String = "",
    val categoryId: Long? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class BudgetFormViewModel @Inject constructor(
    private val financeCatalog: FinanceCatalogRepository,
    categoryRepository: CategoryRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    val categories = categoryRepository.observeByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(BudgetFormUiState())
    val state: StateFlow<BudgetFormUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val cats = categories.first { it.isNotEmpty() }
            _state.update { it.copy(categoryId = cats.first().id) }
        }
    }

    fun updateAmount(v: String) = _state.update { it.copy(amountInput = v, error = null) }
    fun updateCategory(id: Long) = _state.update { it.copy(categoryId = id, error = null) }

    fun save() {
        val s = _state.value
        val minor = AmountParser.parseToMinor(s.amountInput)
        val categoryId = s.categoryId
        if (minor == null || minor <= 0) {
            _state.update { it.copy(error = "Monto inválido") }
            return
        }
        if (categoryId == null) {
            _state.update { it.copy(error = "Selecciona categoría") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            runCatching {
                val range = DateRanges.monthRange()
                financeCatalog.addBudget(categoryId, minor, range.startEpochMilli, range.endEpochMilli)
                refreshIntelligence()
            }.onSuccess {
                _state.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
