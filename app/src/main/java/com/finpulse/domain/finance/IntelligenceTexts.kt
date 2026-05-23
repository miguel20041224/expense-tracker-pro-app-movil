package com.finpulse.domain.finance

data class IntelligenceTexts(
    val healthTitle: String,
    val healthBody: (Int) -> String,
    val overspendInsightTitle: String,
    val overspendInsightBody: String,
    val overspendAlertTitle: String,
    val overspendAlertMessage: String,
    val savingsGoodTitle: String,
    val savingsGoodBody: (Int) -> String,
    val savingsLowTitle: String,
    val savingsLowBody: (Int) -> String,
    val categoryDominanceTitle: (String) -> String,
    val categoryDominanceBody: (String, Int) -> String,
    val debtRiskTitle: String,
    val debtRiskBody: String,
    val debtDueTitle: (String) -> String,
    val debtDueMessage: (String) -> String,
    val snowballTitle: String,
    val snowballBody: (String, Int) -> String,
    val categoryOther: String,
)
