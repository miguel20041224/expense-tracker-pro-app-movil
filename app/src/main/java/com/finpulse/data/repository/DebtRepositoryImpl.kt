package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.DebtDao
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.mapper.toEntity
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.finance.SnowballCalculator
import com.finpulse.domain.model.Debt
import com.finpulse.domain.model.SnowballPlan
import com.finpulse.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepositoryImpl @Inject constructor(
    private val debtDao: DebtDao,
    private val currentUser: CurrentUserProvider,
) : DebtRepository {

    private fun userId() = currentUser.requireUserId()

    override fun observeAll(): Flow<List<Debt>> =
        debtDao.observeAllOrderedForSnowball(userId()).map { list -> list.map { it.toDomain() } }

    override fun observeSnowballPlan(extraMonthlyMinor: Long): Flow<SnowballPlan> =
        observeAll().map { debts -> SnowballCalculator.buildPlan(debts, extraMonthlyMinor) }

    override suspend fun getById(id: Long): Debt? =
        debtDao.getById(id, userId())?.toDomain()

    override suspend fun save(debt: Debt): Long {
        val entity = debt.toEntity(userId())
        return if (debt.id == 0L) {
            debtDao.insert(entity)
        } else {
            debtDao.update(entity)
            debt.id
        }
    }

    override suspend fun delete(id: Long) {
        debtDao.getById(id, userId())?.let { debtDao.delete(it) }
    }
}
