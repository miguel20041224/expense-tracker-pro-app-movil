package com.finpulse.data.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.finpulse.data.local.db.entity.TransactionEntity

data class TransactionWithCategoryRow(
    @Embedded val transaction: TransactionEntity,
    @ColumnInfo(name = "category_name") val categoryName: String?,
)
