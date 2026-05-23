package com.finpulse.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_insights",
    indices = [Index(value = ["type"]), Index(value = ["created_at"])],
)
data class FinancialInsightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val body: String,
    val severity: String,
    @ColumnInfo(name = "metadata_json") val metadataJson: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
