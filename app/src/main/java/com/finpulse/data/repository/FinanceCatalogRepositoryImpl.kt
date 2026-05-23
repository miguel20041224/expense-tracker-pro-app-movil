package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.entity.BudgetEntity
import com.finpulse.data.local.db.entity.GoalEntity
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.Budget
import com.finpulse.domain.model.Goal
import com.finpulse.domain.repository.FinanceCatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceCatalogRepositoryImpl @Inject constructor(
    private val financeCatalogDao: FinanceCatalogDao,
    private val currentUser: CurrentUserProvider,
) : FinanceCatalogRepository {

    private fun userId() = currentUser.requireUserId()

    override fun observeBudgets(): Flow<List<Budget>> =
        financeCatalogDao.observeBudgetsWithSpent(userId()).map { rows ->
            rows.map {
                Budget(
                    id = it.id,
                    categoryId = it.categoryId,
                    categoryName = it.categoryName,
                    amountMinor = it.amountMinor,
                    period = it.period,
                    spentMinor = it.spentMinor,
                )
            }
        }

    override fun observeGoals(): Flow<List<Goal>> =
        financeCatalogDao.observeGoals(userId()).map { list -> list.map { it.toDomain() } }

    override suspend fun addBudget(categoryId: Long, amountMinor: Long, startAt: Long, endAt: Long?) {
        financeCatalogDao.insertBudget(
            BudgetEntity(
                userId = userId(),
                categoryId = categoryId,
                amountMinor = amountMinor,
                period = "MONTHLY",
                startAt = startAt,
                endAt = endAt,
            ),
        )
    }

    override suspend fun addGoal(name: String, targetMinor: Long, savedMinor: Long, deadlineAt: Long?) {
        financeCatalogDao.insertGoal(
            GoalEntity(
                userId = userId(),
                name = name,
                targetMinor = targetMinor,
                savedMinor = savedMinor,
                deadlineAt = deadlineAt,
                type = "SAVINGS",
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun deleteBudget(id: Long) {
        financeCatalogDao.getBudgetById(id, userId())?.let { financeCatalogDao.deleteBudget(it) }
    }

    override suspend fun deleteGoal(id: Long) {
        financeCatalogDao.getGoalById(id, userId())?.let { financeCatalogDao.deleteGoal(it) }
    }
}
