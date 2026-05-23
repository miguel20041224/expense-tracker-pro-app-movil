package com.finpulse.domain.model

data class Transaction(
    val id: Long,
    val accountId: Long,
    val categoryId: Long?,
    val categoryName: String?,
    val type: TransactionType,
    val amountMinor: Long,
    val note: String?,
    val occurredAt: Long,
    val isRecurring: Boolean,
    val recurrenceRule: String?,
)
