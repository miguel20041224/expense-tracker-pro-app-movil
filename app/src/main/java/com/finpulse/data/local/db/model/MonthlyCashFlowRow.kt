package com.finpulse.data.local.db.model

data class MonthlyCashFlowRow(
    val monthKey: String,
    val incomeMinor: Long,
    val expenseMinor: Long,
)
