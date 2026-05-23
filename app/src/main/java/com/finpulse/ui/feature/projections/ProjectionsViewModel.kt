package com.finpulse.ui.feature.projections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.model.SnowballPlan
import com.finpulse.domain.repository.DebtRepository
import com.finpulse.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ProjectionsUiState(
    val snapshot: DashboardSnapshot? = null,
    val snowball: SnowballPlan = SnowballPlan(emptyList(), 0, 0, 0, null, 0),
)

@dagger.hilt.android.lifecycle.HiltViewModel
class ProjectionsViewModel @Inject constructor(
    observeDashboard: ObserveDashboardUseCase,
    debtRepository: DebtRepository,
) : ViewModel() {
    val uiState: StateFlow<ProjectionsUiState> = combine(
        observeDashboard(),
        debtRepository.observeSnowballPlan(),
    ) { snapshot, snowball ->
        ProjectionsUiState(snapshot, snowball)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProjectionsUiState())
}
