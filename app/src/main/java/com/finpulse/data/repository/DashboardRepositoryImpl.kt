package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.DashboardDao
import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.entity.FinancialInsightEntity
import com.finpulse.data.mapper.toDomain
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.model.MonthlyTrendPoint
import com.finpulse.domain.repository.DashboardRepository
import com.finpulse.domain.repository.FinancialIntelligenceRepository
import com.finpulse.util.MonthRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
    private val insightDao: InsightDao,
    private val financeCatalogDao: FinanceCatalogDao,
    private val intelligenceRepository: FinancialIntelligenceRepository,
    private val currentUser: CurrentUserProvider,
) : DashboardRepository {

    override fun observeDashboard(): Flow<DashboardSnapshot> {
        val userId = currentUser.requireUserId()
        val range = MonthRange.current()
        val trendSince = ZonedDateTime.now().minusMonths(6).toInstant().toEpochMilli()

        val cashFlow = combine(
            dashboardDao.observeIncomeMinor(userId, range.startEpochMilli, range.endEpochMilli),
            dashboardDao.observeExpenseMinor(userId, range.startEpochMilli, range.endEpochMilli),
            insightDao.observeRecentInsights(userId, 5),
            insightDao.observeUnreadAlertCount(userId),
            dashboardDao.observeMonthlyCashFlow(userId, trendSince),
        ) { income, expense, insights, unread, trend ->
            CashFlowData(income, expense, insights, unread, trend)
        }

        val catalog = combine(
            financeCatalogDao.observeBudgetCount(userId),
            financeCatalogDao.observeGoalCount(userId),
            financeCatalogDao.observeTotalDebtMinor(userId),
            financeCatalogDao.observeProjectionCount(userId),
        ) { budgets, goals, debt, projections ->
            CatalogData(budgets, goals, debt, projections)
        }

        return combine(cashFlow, catalog) { flow, cat ->
            val healthFromDb = flow.insights.firstOrNull { it.type == "HEALTH_SCORE" }
                ?.metadataJson
                ?.let { json -> HEALTH_SCORE_REGEX.find(json)?.groupValues?.get(1)?.toIntOrNull() }
                ?: computeHealthScore(flow.income, flow.expense)

            DashboardSnapshot(
                incomeMinor = flow.income,
                expenseMinor = flow.expense,
                balanceMinor = flow.income - flow.expense,
                healthScore = healthFromDb.coerceIn(0, 100),
                insights = flow.insights.filter { it.type != "HEALTH_SCORE" }.map { it.toDomain() },
                unreadAlerts = flow.unread,
                monthlyTrend = flow.trend.map {
                    MonthlyTrendPoint(it.monthKey, it.incomeMinor, it.expenseMinor)
                },
                budgetCount = cat.budgetCount,
                goalCount = cat.goalCount,
                totalDebtMinor = cat.totalDebtMinor,
                projectionCount = cat.projectionCount,
            )
        }
    }

    override suspend fun refreshIntelligence() = intelligenceRepository.refresh()

    private fun computeHealthScore(income: Long, expense: Long): Int {
        if (income <= 0L) return 40
        val ratio = expense.toDouble() / income
        return when {
            ratio <= 0.5 -> 90
            ratio <= 0.7 -> 75
            ratio <= 0.9 -> 60
            ratio <= 1.0 -> 45
            else -> 25
        }
    }

    private data class CashFlowData(
        val income: Long,
        val expense: Long,
        val insights: List<FinancialInsightEntity>,
        val unread: Int,
        val trend: List<com.finpulse.data.local.db.model.MonthlyCashFlowRow>,
    )

    private data class CatalogData(
        val budgetCount: Int,
        val goalCount: Int,
        val totalDebtMinor: Long,
        val projectionCount: Int,
    )

    companion object {
        private val HEALTH_SCORE_REGEX = Regex(""""score"\s*:\s*(\d+)""")
    }
}
