package com.finpulse.data.mapper

import com.finpulse.data.local.db.entity.FinancialInsightEntity
import com.finpulse.domain.model.FinancialInsight

fun FinancialInsightEntity.toDomain(): FinancialInsight = FinancialInsight(
    id = id,
    type = type,
    title = title,
    body = body,
    severity = severity,
)
