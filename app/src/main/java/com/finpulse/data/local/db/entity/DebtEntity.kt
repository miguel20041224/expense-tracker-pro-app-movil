package com.finpulse.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["due_at"]), Index(value = ["remaining_minor"]), Index(value = ["user_id"])],
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    val name: String,
    @ColumnInfo(name = "principal_minor") val principalMinor: Long,
    @ColumnInfo(name = "remaining_minor") val remainingMinor: Long,
    @ColumnInfo(name = "interest_rate") val interestRate: Double,
    @ColumnInfo(name = "minimum_payment_minor") val minimumPaymentMinor: Long,
    @ColumnInfo(name = "due_at") val dueAt: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
