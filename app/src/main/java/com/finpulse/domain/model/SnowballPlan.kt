package com.finpulse.domain.model

data class SnowballPlan(
    val steps: List<SnowballStep>,
    val totalRemainingMinor: Long,
    val estimatedMonths: Int,
    val estimatedInterestSavedMinor: Long,
    val recommendedDebtId: Long?,
    val monthlyPaymentNeededMinor: Long,
)

data class SnowballStep(
    val order: Int,
    val debtId: Long,
    val debtName: String,
    val remainingMinor: Long,
    val minimumPaymentMinor: Long,
    val estimatedMonthsToPayoff: Int,
    val isRecommendedNext: Boolean,
)
