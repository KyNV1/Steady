package com.steady.app.feature.insights

import com.steady.app.domain.model.DailyIntake
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightsSummaryTest {
    private val today = 100L

    private fun day(offset: Int, protein: Int) = DailyIntake(epochDay = today + offset, proteinGrams = protein)

    @Test
    fun `week is the last seven days ending today, zero-filled`() {
        val summary = summarizeProtein(listOf(day(0, 72), day(-3, 60)), today, goalGrams = 80, activeDays = setOf(today, today - 3))
        assertEquals((94L..100L).toList(), summary.week.map { it.epochDay })
        assertEquals(listOf(0, 0, 0, 60, 0, 0, 72), summary.week.map { it.grams })
        assertTrue(summary.week.last().isToday)
        assertEquals(72, summary.todayGrams)
        assertEquals(8, summary.gramsToGoal)
    }

    @Test
    fun `average ignores unlogged days and compares with last week`() {
        val intakes = listOf(day(0, 70), day(-1, 60), day(-8, 50), day(-9, 50))
        val summary = summarizeProtein(intakes, today, goalGrams = 80, activeDays = intakes.map { it.epochDay }.toSet())
        assertEquals(65, summary.averageGrams)
        assertEquals(15, summary.averageDeltaGrams)
    }

    @Test
    fun `no logs means no average, no delta and not on track`() {
        val summary = summarizeProtein(emptyList(), today, goalGrams = 80, activeDays = emptySet())
        assertNull(summary.averageGrams)
        assertNull(summary.averageDeltaGrams)
        assertFalse(summary.isOnTrack)
        assertEquals(0, summary.streakDays)
    }

    @Test
    fun `on track when the weekly average reaches ninety percent of goal`() {
        val intakes = listOf(day(0, 72), day(-1, 72))
        val active = intakes.map { it.epochDay }.toSet()
        assertTrue(summarizeProtein(intakes, today, goalGrams = 80, activeDays = active).isOnTrack)
        assertFalse(summarizeProtein(intakes, today, goalGrams = 90, activeDays = active).isOnTrack)
    }
}
