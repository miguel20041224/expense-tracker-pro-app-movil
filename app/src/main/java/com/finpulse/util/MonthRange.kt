package com.finpulse.util

import java.time.ZoneId
import java.time.ZonedDateTime

data class MonthRange(
    val startEpochMilli: Long,
    val endEpochMilli: Long,
) {
    companion object {
        fun current(zoneId: ZoneId = ZoneId.systemDefault()): MonthRange {
            val now = ZonedDateTime.now(zoneId)
            val start = now.withDayOfMonth(1).toLocalDate().atStartOfDay(zoneId)
            val end = start.plusMonths(1)
            return MonthRange(
                startEpochMilli = start.toInstant().toEpochMilli(),
                endEpochMilli = end.toInstant().toEpochMilli(),
            )
        }
    }
}
