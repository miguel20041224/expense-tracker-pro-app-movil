package com.finpulse.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debts",
    indices = [Index(value = ["due_at"])],
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "principal_minor") val principalMinor: Long,
    @ColumnInfo(name = "remaining_minor") val remainingMinor: Long,
    @ColumnInfo(name = "interest_rate") val interestRate: Double,
    @ColumnInfo(name = "due_at") val dueAt: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
