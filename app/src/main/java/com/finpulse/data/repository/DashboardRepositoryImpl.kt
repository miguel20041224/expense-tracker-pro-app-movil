package com.finpulse.data.repository

import com.finpulse.data.local.db.dao.DashboardDao
import com.finpulse.data.local.db.dao.FinanceCatalogDao
import com.finpulse.data.local.db.dao.InsightDao
import com.finpulse.data.local.db.entity.FinancialInsightEntity
import com.finpulse.data.mapper.toDomain
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.model.MonthlyTrendPoint
import com.finpulse.domain.repository.DashboardRepository
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
) : DashboardRepository {

    override fun observeDashboard(): Flow<DashboardSnapshot> {
        val range = MonthRange.current()
        val trendSince = ZonedDateTime.now().minusMonths(6).toInstant().toEpochMilli()

        val cashFlow = combine(
            dashboardDao.observeIncomeMinor(range.startEpochMilli, range.endEpochMilli),
            dashboardDao.observeExpenseMinor(range.startEpochMilli, range.endEpochMilli),
            insightDao.observeRecentInsights(5),
            insightDao.observeUnreadAlertCount(),
            dashboardDao.observeMonthlyCashFlow(trendSince),
        ) { income, expense, insights, unread, trend ->
            CashFlowData(income, expense, insights, unread, trend)
        }

        val catalog = combine(
            financeCatalogDao.observeBudgetCount(),
            financeCatalogDao.observeGoalCount(),
            financeCatalogDao.observeTotalDebtMinor(),
            financeCatalogDao.observeProjectionCount(),
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

    override suspend fun refreshIntelligence() {
        val range = MonthRange.current()
        val income = dashboardDao.incomeMinorOnce(range.startEpochMilli, range.endEpochMilli)
        val expense = dashboardDao.expenseMinorOnce(range.startEpochMilli, range.endEpochMilli)
        val score = computeHealthScore(income, expense)
        val savingsRate = if (income > 0) ((income - expense).toDouble() / income * 100).toInt() else 0

        val insights = buildList {
            add(
                FinancialInsightEntity(
                    type = "HEALTH_SCORE",
                    title = "Health Score",
                    body = "Score: $score/100",
                    severity = when {
                        score >= 75 -> "LOW"
                        score >= 50 -> "MEDIUM"
                        else -> "HIGH"
                    },
                    metadataJson = """{"score":$score}""",
                    createdAt = System.currentTimeMillis(),
                ),
            )
            if (expense > income) {
                add(
                    FinancialInsightEntity(
                        type = "OVERSPEND",
                        title = "Gastos superan ingresos",
                        body = "Este mes tus gastos superan los ingresos. Revisa categorías principales.",
                        severity = "HIGH",
                        metadataJson = null,
                        createdAt = System.currentTimeMillis(),
                    ),
                )
            } else if (savingsRate >= 20) {
                add(
                    FinancialInsightEntity(
                        type = "SAVINGS",
                        title = "Buen ritmo de ahorro",
                        body = "Ahorras aproximadamente $savingsRate% de tus ingresos este mes.",
                        severity = "LOW",
                        metadataJson = null,
                        createdAt = System.currentTimeMillis(),
                    ),
                )
            }
        }
        insightDao.clearInsights()
        insightDao.insertInsights(insights)
    }

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
