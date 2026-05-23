package com.finpulse.domain.model

data class Category(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val colorHex: String,
)
