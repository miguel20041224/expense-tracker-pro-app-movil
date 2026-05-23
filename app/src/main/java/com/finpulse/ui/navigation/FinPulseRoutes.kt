package com.finpulse.ui.navigation

object FinPulseRoutes {
    const val DASHBOARD = "dashboard"
    const val EXPENSES = "expenses"
    const val INCOME = "income"
    const val DEBTS = "debts"
    const val MORE = "more"
    const val TRANSACTION_FORM = "transaction_form/{type}/{id}"
    const val DEBT_FORM = "debt_form/{id}"
    const val SNOWBALL = "snowball"
    const val REPORTS = "reports"
    const val PROJECTIONS = "projections"
    const val ALERTS = "alerts"
    const val BUDGETS_GOALS = "budgets_goals"
    const val BUDGET_FORM = "budget_form"
    const val GOAL_FORM = "goal_form"
    const val SETTINGS = "settings"
    const val INTELLIGENCE = "intelligence"

    fun transactionForm(type: String, id: Long = -1L) = "transaction_form/$type/$id"
    fun debtForm(id: Long = -1L) = "debt_form/$id"

    val bottomBarRoutes = setOf(DASHBOARD, EXPENSES, INCOME, DEBTS, MORE)
}
