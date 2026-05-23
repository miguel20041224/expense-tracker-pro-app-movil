package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.TransactionDao
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.mapper.toEntity
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.Transaction
import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import com.finpulse.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryRepository: CategoryRepository,
    private val currentUser: CurrentUserProvider,
) : TransactionRepository {

    private fun userId() = currentUser.requireUserId()

    override fun observeByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.observeByType(userId(), type.storageValue).map { rows ->
            rows.map { it.toDomain() }
        }

    override fun observeFiltered(
        type: TransactionType,
        startEpochMilli: Long,
        endEpochMilli: Long,
        categoryId: Long?,
    ): Flow<List<Transaction>> =
        transactionDao.observeFiltered(
            userId(),
            type.storageValue,
            startEpochMilli,
            endEpochMilli,
            categoryId ?: -1L,
        ).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: Long): Transaction? =
        transactionDao.getById(id, userId())?.toDomain()

    override suspend fun save(transaction: Transaction): Long {
        val accountId = categoryRepository.getDefaultAccountId()
        val categoryId = transaction.categoryId?.takeIf { it > 0 }
            ?: error("Categoría requerida")
        val existing = if (transaction.id != 0L) transactionDao.getById(transaction.id, userId()) else null
        val entity = transaction.copy(accountId = accountId, categoryId = categoryId)
            .toEntity(userId())
            .copy(createdAt = existing?.createdAt ?: System.currentTimeMillis())
        return if (transaction.id == 0L) {
            transactionDao.insert(entity)
        } else {
            transactionDao.update(entity)
            transaction.id
        }
    }

    override suspend fun delete(id: Long) {
        transactionDao.getById(id, userId())?.let { transactionDao.delete(it) }
    }
}
