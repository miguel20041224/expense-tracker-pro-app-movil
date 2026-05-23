package com.finpulse.domain.model

enum class TransactionType(val storageValue: String) {
    INCOME("INCOME"),
    EXPENSE("EXPENSE"),
    ;

    companion object {
        fun fromStorage(value: String): TransactionType =
            entries.first { it.storageValue == value }
    }
}
