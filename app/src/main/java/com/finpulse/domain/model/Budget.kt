package com.finpulse.domain.model

data class Budget(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val amountMinor: Long,
    val period: String,
    val spentMinor: Long,
) {
    val usagePercent: Int
        get() = if (amountMinor <= 0) 0 else ((spentMinor.toDouble() / amountMinor) * 100).toInt().coerceIn(0, 100)
}
