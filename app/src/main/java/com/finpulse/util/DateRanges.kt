package com.finpulse.util

import java.time.ZoneId
import java.time.ZonedDateTime

object DateRanges {
    fun monthRange(monthsAgo: Int = 0, zoneId: ZoneId = ZoneId.systemDefault()): MonthRange {
        val now = ZonedDateTime.now(zoneId).minusMonths(monthsAgo.toLong())
        val start = now.withDayOfMonth(1).toLocalDate().atStartOfDay(zoneId)
        val end = start.plusMonths(1)
        return MonthRange(
            startEpochMilli = start.toInstant().toEpochMilli(),
            endEpochMilli = end.toInstant().toEpochMilli(),
        )
    }
}
