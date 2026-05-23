package com.finpulse.ui.feature.debts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.Debt
import com.finpulse.domain.repository.DebtRepository
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import com.finpulse.util.AmountParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebtFormUiState(
    val name: String = "",
    val principalInput: String = "",
    val remainingInput: String = "",
    val minimumPaymentInput: String = "",
    val interestInput: String = "0",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class DebtFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val debtRepository: DebtRepository,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    private val debtId: Long = savedStateHandle.get<Long>("id") ?: -1L

    private val _uiState = MutableStateFlow(DebtFormUiState())
    val uiState: StateFlow<DebtFormUiState> = _uiState.asStateFlow()

    init {
        if (debtId > 0) {
            viewModelScope.launch {
                val debt = debtRepository.getById(debtId)
                if (debt != null) {
                    _uiState.update {
                        it.copy(
                            name = debt.name,
                            principalInput = AmountParser.formatInputFromMinor(debt.principalMinor),
                            remainingInput = AmountParser.formatInputFromMinor(debt.remainingMinor),
                            minimumPaymentInput = AmountParser.formatInputFromMinor(debt.minimumPaymentMinor),
                            interestInput = debt.interestRate.toString(),
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No encontrado") }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateName(v: String) = _uiState.update { it.copy(name = v) }
    fun updatePrincipal(v: String) = _uiState.update { it.copy(principalInput = v) }
    fun updateRemaining(v: String) = _uiState.update { it.copy(remainingInput = v) }
    fun updateMinimum(v: String) = _uiState.update { it.copy(minimumPaymentInput = v) }
    fun updateInterest(v: String) = _uiState.update { it.copy(interestInput = v) }

    fun save() {
        val s = _uiState.value
        val principal = AmountParser.parseToMinor(s.principalInput)
        val remaining = AmountParser.parseToMinor(s.remainingInput)
        val minimum = AmountParser.parseToMinor(s.minimumPaymentInput)
        val rate = s.interestInput.replace(",", ".").toDoubleOrNull()
        if (s.name.isBlank() || principal == null || remaining == null || minimum == null || rate == null) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                debtRepository.save(
                    Debt(
                        id = if (debtId > 0) debtId else 0L,
                        name = s.name.trim(),
                        principalMinor = principal,
                        remainingMinor = remaining,
                        interestRate = rate,
                        minimumPaymentMinor = minimum,
                        dueAt = null,
                        createdAt = System.currentTimeMillis(),
                    ),
                )
                refreshIntelligence()
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
