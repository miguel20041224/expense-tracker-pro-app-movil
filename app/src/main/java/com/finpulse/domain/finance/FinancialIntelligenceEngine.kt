package com.finpulse.domain.finance

import com.finpulse.data.local.db.entity.AlertEntity
import com.finpulse.domain.model.Debt
import com.finpulse.data.local.db.entity.FinancialInsightEntity

data class IntelligenceOutput(
    val insights: List<FinancialInsightEntity>,
    val alerts: List<AlertEntity>,
)

object FinancialIntelligenceEngine {

    fun analyze(
        userId: Long,
        texts: IntelligenceTexts,
        incomeMinor: Long,
        expenseMinor: Long,
        debts: List<Debt>,
        expenseByCategory: Map<String, Long>,
        now: Long = System.currentTimeMillis(),
    ): IntelligenceOutput {
        val score = computeHealthScore(incomeMinor, expenseMinor, debts)
        val insights = mutableListOf<FinancialInsightEntity>()
        val alerts = mutableListOf<AlertEntity>()

        insights += FinancialInsightEntity(
            userId = userId,
            type = "HEALTH_SCORE",
            title = texts.healthTitle,
            body = texts.healthBody(score),
            severity = scoreSeverity(score),
            metadataJson = """{"score":$score}""",
            createdAt = now,
        )

        if (expenseMinor > incomeMinor && incomeMinor > 0) {
            insights += FinancialInsightEntity(
                userId = userId,
                type = "OVERSPEND",
                title = texts.overspendInsightTitle,
                body = texts.overspendInsightBody,
                severity = "HIGH",
                metadataJson = null,
                createdAt = now,
            )
            alerts += AlertEntity(
                userId = userId,
                type = "OVERSPEND",
                title = texts.overspendAlertTitle,
                message = texts.overspendAlertMessage,
                createdAt = now,
            )
        } else if (incomeMinor > 0) {
            val savingsRate = ((incomeMinor - expenseMinor).toDouble() / incomeMinor * 100).toInt()
            if (savingsRate >= 20) {
                insights += FinancialInsightEntity(
                    userId = userId,
                    type = "SAVINGS",
                    title = texts.savingsGoodTitle,
                    body = texts.savingsGoodBody(savingsRate),
                    severity = "LOW",
                    metadataJson = null,
                    createdAt = now,
                )
            } else if (savingsRate < 5) {
                insights += FinancialInsightEntity(
                    userId = userId,
                    type = "LOW_SAVINGS",
                    title = texts.savingsLowTitle,
                    body = texts.savingsLowBody(savingsRate),
                    severity = "MEDIUM",
                    metadataJson = null,
                    createdAt = now,
                )
            }
        }

        val topExpense = expenseByCategory.maxByOrNull { it.value }
        if (topExpense != null && expenseMinor > 0) {
            val share = (topExpense.value.toDouble() / expenseMinor * 100).toInt()
            if (share >= 40) {
                insights += FinancialInsightEntity(
                    userId = userId,
                    type = "CATEGORY_DOMINANCE",
                    title = texts.categoryDominanceTitle(topExpense.key),
                    body = texts.categoryDominanceBody(topExpense.key, share),
                    severity = "MEDIUM",
                    metadataJson = null,
                    createdAt = now,
                )
            }
        }

        val totalDebt = debts.sumOf { it.remainingMinor }
        if (totalDebt > 0 && incomeMinor > 0 && totalDebt > incomeMinor * 3) {
            insights += FinancialInsightEntity(
                userId = userId,
                type = "DEBT_RISK",
                title = texts.debtRiskTitle,
                body = texts.debtRiskBody,
                severity = "HIGH",
                metadataJson = null,
                createdAt = now,
            )
        }

        debts.filter { it.dueAt != null && it.dueAt < now + 14 * 86_400_000L }.forEach { debt ->
            alerts += AlertEntity(
                userId = userId,
                type = "DEBT_DUE",
                title = texts.debtDueTitle(debt.name),
                message = texts.debtDueMessage(debt.name),
                createdAt = now,
            )
        }

        if (debts.any { it.remainingMinor > 0 }) {
            val plan = SnowballCalculator.buildPlan(debts)
            plan.recommendedDebtId?.let { id ->
                val step = plan.steps.firstOrNull { it.debtId == id }
                if (step != null) {
                    insights += FinancialInsightEntity(
                        userId = userId,
                        type = "SNOWBALL",
                        title = texts.snowballTitle,
                        body = texts.snowballBody(step.debtName, step.estimatedMonthsToPayoff),
                        severity = "LOW",
                        metadataJson = """{"debtId":$id}""",
                        createdAt = now,
                    )
                }
            }
        }

        return IntelligenceOutput(insights, alerts)
    }

    private fun computeHealthScore(income: Long, expense: Long, debts: List<Debt>): Int {
        var score = if (income <= 0) 40 else {
            val ratio = expense.toDouble() / income
            when {
                ratio <= 0.5 -> 90
                ratio <= 0.7 -> 75
                ratio <= 0.9 -> 60
                ratio <= 1.0 -> 45
                else -> 25
            }
        }
        val debtBurden = debts.sumOf { it.remainingMinor }
        if (income > 0 && debtBurden > income * 2) score = (score - 15).coerceAtLeast(10)
        return score.coerceIn(0, 100)
    }

    private fun scoreSeverity(score: Int): String = when {
        score >= 75 -> "LOW"
        score >= 50 -> "MEDIUM"
        else -> "HIGH"
    }
}
