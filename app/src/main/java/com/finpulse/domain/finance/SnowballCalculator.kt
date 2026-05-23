package com.finpulse.domain.finance

import com.finpulse.domain.model.Debt
import com.finpulse.domain.model.SnowballPlan
import com.finpulse.domain.model.SnowballStep
import kotlin.math.ceil
import kotlin.math.max

object SnowballCalculator {

    /**
     * Método bola de nieve: pagar primero la deuda con menor saldo pendiente.
     */
    fun buildPlan(debts: List<Debt>, extraMonthlyMinor: Long = 0): SnowballPlan {
        val active = debts.filter { it.remainingMinor > 0 }
            .sortedBy { it.remainingMinor }

        if (active.isEmpty()) {
            return SnowballPlan(
                steps = emptyList(),
                totalRemainingMinor = 0,
                estimatedMonths = 0,
                estimatedInterestSavedMinor = 0,
                recommendedDebtId = null,
                monthlyPaymentNeededMinor = 0,
            )
        }

        var rollingPayment = extraMonthlyMinor
        val steps = active.mapIndexed { index, debt ->
            val payment = debt.minimumPaymentMinor + if (index == 0) rollingPayment else 0L
            val months = monthsToPayoff(debt.remainingMinor, payment, debt.interestRate)
            if (index == 0) {
                rollingPayment += debt.minimumPaymentMinor
            }
            SnowballStep(
                order = index + 1,
                debtId = debt.id,
                debtName = debt.name,
                remainingMinor = debt.remainingMinor,
                minimumPaymentMinor = debt.minimumPaymentMinor,
                estimatedMonthsToPayoff = months,
                isRecommendedNext = index == 0,
            )
        }

        val totalMonths = steps.sumOf { it.estimatedMonthsToPayoff }
        val interestSaved = estimateInterestSaved(active)

        return SnowballPlan(
            steps = steps,
            totalRemainingMinor = active.sumOf { it.remainingMinor },
            estimatedMonths = totalMonths,
            estimatedInterestSavedMinor = interestSaved,
            recommendedDebtId = active.firstOrNull()?.id,
            monthlyPaymentNeededMinor = active.sumOf { it.minimumPaymentMinor } + extraMonthlyMinor,
        )
    }

    private fun monthsToPayoff(remainingMinor: Long, monthlyPaymentMinor: Long, annualRate: Double): Int {
        if (remainingMinor <= 0) return 0
        val payment = max(monthlyPaymentMinor, 1L)
        if (annualRate <= 0.0) {
            return ceil(remainingMinor.toDouble() / payment).toInt().coerceAtLeast(1)
        }
        var balance = remainingMinor.toDouble()
        var months = 0
        val monthlyRate = annualRate / 100.0 / 12.0
        while (balance > 0 && months < 600) {
            balance += balance * monthlyRate
            balance -= payment
            months++
        }
        return months.coerceAtLeast(1)
    }

    private fun estimateInterestSaved(debts: List<Debt>): Long {
        return debts.sumOf { debt ->
            val naive = (debt.remainingMinor * debt.interestRate / 100.0 * 0.5).toLong()
            (naive * 0.15).toLong()
        }
    }
}
