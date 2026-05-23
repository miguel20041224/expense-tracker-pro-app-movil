package com.finpulse.domain.model

data class Alert(
    val id: Long,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: Long,
)
