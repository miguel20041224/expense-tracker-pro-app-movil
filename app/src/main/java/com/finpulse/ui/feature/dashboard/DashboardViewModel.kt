package com.finpulse.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.usecase.ObserveDashboardUseCase
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val snapshot: DashboardSnapshot? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeDashboard: ObserveDashboardUseCase,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeDashboard()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "error")
                    }
                }
                .collect { snapshot ->
                    _uiState.update {
                        it.copy(isLoading = false, snapshot = snapshot, errorMessage = null)
                    }
                }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { refreshIntelligence() }
        }
    }
}
