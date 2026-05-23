package com.finpulse.domain.model

data class DashboardSnapshot(
    val incomeMinor: Long,
    val expenseMinor: Long,
    val balanceMinor: Long,
    val healthScore: Int,
    val insights: List<FinancialInsight>,
    val unreadAlerts: Int,
    val monthlyTrend: List<MonthlyTrendPoint>,
    val budgetCount: Int,
    val goalCount: Int,
    val totalDebtMinor: Long,
    val projectionCount: Int,
)

data class MonthlyTrendPoint(
    val monthKey: String,
    val incomeMinor: Long,
    val expenseMinor: Long,
)
