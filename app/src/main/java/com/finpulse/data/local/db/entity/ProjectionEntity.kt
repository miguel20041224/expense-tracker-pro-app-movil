package com.finpulse.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "projections",
    indices = [Index(value = ["type"]), Index(value = ["projected_at"])],
)
data class ProjectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    @ColumnInfo(name = "metadata_json") val metadataJson: String?,
    @ColumnInfo(name = "projected_at") val projectedAt: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
