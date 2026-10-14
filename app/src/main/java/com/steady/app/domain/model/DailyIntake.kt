package com.steady.app.domain.model

import java.util.TimeZone
import java.util.concurrent.TimeUnit

/** Quick-add totals for one local calendar day, keyed by [epochDay] (days since 1970-01-01 local). */
data class DailyIntake(
    val epochDay: Long,
    val fiberGrams: Int = 0,
    val proteinGrams: Int = 0,
    val waterMilliliters: Int = 0,
)

const val ML_PER_FL_OZ = 29.5735

/** Local-calendar epoch day for [epochMillis], so "today" flips at the user's midnight, not UTC's. */
fun localEpochDay(epochMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): Long =
    Math.floorDiv(epochMillis + timeZone.getOffset(epochMillis), TimeUnit.DAYS.toMillis(1))

/**
 * Consecutive logged days ending today. A day without entries yet doesn't break the streak until
 * it's over, so an unlogged today still counts the run that ended yesterday.
 */
fun consecutiveDayStreak(activeDays: Set<Long>, today: Long): Int {
    var day = if (today in activeDays) today else today - 1
    var streak = 0
    while (day in activeDays) {
        streak++
        day--
    }
    return streak
}
