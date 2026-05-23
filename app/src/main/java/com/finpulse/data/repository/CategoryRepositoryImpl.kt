package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.CatalogDao
import com.finpulse.data.local.db.entity.AccountEntity
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.Category
import com.finpulse.domain.model.TransactionType
import com.finpulse.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val catalogDao: CatalogDao,
    private val currentUser: CurrentUserProvider,
) : CategoryRepository {

    override fun observeByType(type: TransactionType): Flow<List<Category>> {
        val userId = currentUser.requireUserId()
        return catalogDao.observeCategoriesByType(userId, type.storageValue).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getDefaultAccountId(): Long {
        val userId = currentUser.requireUserId()
        return catalogDao.getDefaultAccountId(userId) ?: run {
            val now = System.currentTimeMillis()
            catalogDao.insertAccounts(
                listOf(
                    AccountEntity(
                        userId = userId,
                        name = "Cuenta principal",
                        currencyCode = "USD",
                        type = "CHECKING",
                        createdAt = now,
                    ),
                ),
            )
            catalogDao.getDefaultAccountId(userId) ?: error("No se pudo crear la cuenta")
        }
    }
}
