package com.finpulse.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    indices = [Index(value = ["type"])],
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "target_minor") val targetMinor: Long,
    @ColumnInfo(name = "saved_minor") val savedMinor: Long,
    @ColumnInfo(name = "deadline_at") val deadlineAt: Long?,
    val type: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
