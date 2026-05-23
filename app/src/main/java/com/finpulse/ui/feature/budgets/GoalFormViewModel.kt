package com.finpulse.ui.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.repository.FinanceCatalogRepository
import com.finpulse.util.AmountParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalFormUiState(
    val name: String = "",
    val targetInput: String = "",
    val savedInput: String = "0",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class GoalFormViewModel @Inject constructor(
    private val financeCatalog: FinanceCatalogRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(GoalFormUiState())
    val state: StateFlow<GoalFormUiState> = _state.asStateFlow()

    fun updateName(v: String) = _state.update { it.copy(name = v, error = null) }
    fun updateTarget(v: String) = _state.update { it.copy(targetInput = v, error = null) }
    fun updateSaved(v: String) = _state.update { it.copy(savedInput = v, error = null) }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Nombre requerido") }
            return
        }
        val target = AmountParser.parseToMinor(s.targetInput)
        val saved = AmountParser.parseToMinor(s.savedInput) ?: 0L
        if (target == null || target <= 0) {
            _state.update { it.copy(error = "Meta inválida") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            runCatching {
                financeCatalog.addGoal(s.name.trim(), target, saved, null)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
