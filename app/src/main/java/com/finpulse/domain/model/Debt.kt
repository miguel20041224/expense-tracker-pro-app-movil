package com.finpulse.domain.model

data class Debt(
    val id: Long,
    val name: String,
    val principalMinor: Long,
    val remainingMinor: Long,
    val interestRate: Double,
    val minimumPaymentMinor: Long,
    val dueAt: Long?,
    val createdAt: Long,
) {
    val progressPercent: Int
        get() = if (principalMinor <= 0) 0
        else ((principalMinor - remainingMinor).toDouble() / principalMinor * 100).toInt().coerceIn(0, 100)
}
