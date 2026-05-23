package com.finpulse.data.local.db.model

import androidx.room.ColumnInfo

data class BudgetWithCategoryRow(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    val period: String,
    @ColumnInfo(name = "start_at") val startAt: Long,
    @ColumnInfo(name = "end_at") val endAt: Long?,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "spent_minor") val spentMinor: Long,
)
