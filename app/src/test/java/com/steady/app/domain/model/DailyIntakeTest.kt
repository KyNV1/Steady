package com.steady.app.domain.model

import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyIntakeTest {
    @Test
    fun `streak counts consecutive days ending today`() {
        assertEquals(3, consecutiveDayStreak(setOf(8L, 9L, 10L), today = 10L))
    }

    @Test
    fun `unlogged today keeps yesterday's run alive`() {
        assertEquals(2, consecutiveDayStreak(setOf(8L, 9L), today = 10L))
    }

    @Test
    fun `a missed day breaks the streak`() {
        assertEquals(1, consecutiveDayStreak(setOf(6L, 7L, 9L), today = 9L))
        assertEquals(0, consecutiveDayStreak(setOf(6L, 7L), today = 9L))
    }

    @Test
    fun `local epoch day flips at local midnight, not UTC`() {
        val saigon = TimeZone.getTimeZone("Asia/Saigon") // UTC+7
        val utc2300 = 86_400_000L * 100 - 3_600_000L // day 99, 23:00 UTC = day 100, 06:00 local
        assertEquals(99L, localEpochDay(utc2300, TimeZone.getTimeZone("UTC")))
        assertEquals(100L, localEpochDay(utc2300, saigon))
    }
}
