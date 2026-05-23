package com.finpulse.domain.model

data class Goal(
    val id: Long,
    val name: String,
    val targetMinor: Long,
    val savedMinor: Long,
    val deadlineAt: Long?,
    val type: String,
) {
    val progressPercent: Int
        get() = if (targetMinor <= 0) 0 else ((savedMinor.toDouble() / targetMinor) * 100).toInt().coerceIn(0, 100)
}
