package com.finpulse.ui.feature.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
) : ViewModel() {
    val alerts = alertRepository.observeAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markRead(id: Long) {
        viewModelScope.launch { alertRepository.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { alertRepository.markAllRead() }
    }
}
