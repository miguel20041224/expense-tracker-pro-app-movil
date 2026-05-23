package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.entity.AlertEntity
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.Alert
import com.finpulse.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val insightDao: InsightDao,
    private val currentUser: CurrentUserProvider,
) : AlertRepository {
    private fun userId() = currentUser.requireUserId()

    override fun observeAlerts(): Flow<List<Alert>> =
        insightDao.observeAlerts(userId()).map { list -> list.map { it.toDomain() } }

    override suspend fun markRead(id: Long) = insightDao.markRead(id, userId())

    override suspend fun markAllRead() = insightDao.markAllRead(userId())
}

private fun AlertEntity.toDomain() = Alert(
    id = id,
    type = type,
    title = title,
    message = message,
    isRead = isRead,
    createdAt = createdAt,
)
