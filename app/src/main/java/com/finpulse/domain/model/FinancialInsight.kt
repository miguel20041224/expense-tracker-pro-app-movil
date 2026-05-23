package com.finpulse.domain.model

data class FinancialInsight(
    val id: Long,
    val type: String,
    val title: String,
    val body: String,
    val severity: String,
)
