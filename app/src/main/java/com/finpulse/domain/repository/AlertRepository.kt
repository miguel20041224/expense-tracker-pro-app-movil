package com.finpulse.domain.repository

import com.finpulse.domain.model.Alert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun observeAlerts(): Flow<List<Alert>>
    suspend fun markRead(id: Long)
    suspend fun markAllRead()
}
