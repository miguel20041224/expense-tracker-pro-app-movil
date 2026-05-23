package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.DashboardDao
import com.finpulse.data.local.db.dao.DebtDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.dao.TransactionDao
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.finance.FinancialIntelligenceEngine
import com.finpulse.domain.repository.FinancialIntelligenceRepository
import com.finpulse.util.AppStrings
import com.finpulse.util.MonthRange
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialIntelligenceRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
    private val transactionDao: TransactionDao,
    private val debtDao: DebtDao,
    private val insightDao: InsightDao,
    private val currentUser: CurrentUserProvider,
    private val appStrings: AppStrings,
) : FinancialIntelligenceRepository {

    override suspend fun refresh() {
        val userId = currentUser.requireUserId()
        val range = MonthRange.current()
        val income = dashboardDao.incomeMinorOnce(userId, range.startEpochMilli, range.endEpochMilli)
        val expense = dashboardDao.expenseMinorOnce(userId, range.startEpochMilli, range.endEpochMilli)
        val debts = debtDao.observeAllOrderedForSnowball(userId).first().map { it.toDomain() }
        val texts = appStrings.intelligenceTexts()
        val expenseRows = transactionDao.observeByType(userId, "EXPENSE").first()
        val expenseByCategory = expenseRows.groupBy { it.categoryName ?: texts.categoryOther }
            .mapValues { (_, rows) -> rows.sumOf { it.transaction.amountMinor } }

        val output = FinancialIntelligenceEngine.analyze(
            userId = userId,
            texts = texts,
            incomeMinor = income,
            expenseMinor = expense,
            debts = debts,
            expenseByCategory = expenseByCategory,
        )

        insightDao.clearInsights(userId)
        insightDao.clearAlerts(userId)
        insightDao.insertInsights(output.insights)
        if (output.alerts.isNotEmpty()) {
            insightDao.insertAlerts(output.alerts)
        }
    }
}
