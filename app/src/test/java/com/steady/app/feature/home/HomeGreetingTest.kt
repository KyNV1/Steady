package com.steady.app.feature.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeGreetingTest {
    @Test
    fun `greeting follows time of day`() {
        assertEquals(Greeting.EVENING, greetingForHour(4))
        assertEquals(Greeting.MORNING, greetingForHour(5))
        assertEquals(Greeting.MORNING, greetingForHour(11))
        assertEquals(Greeting.AFTERNOON, greetingForHour(12))
        assertEquals(Greeting.AFTERNOON, greetingForHour(17))
        assertEquals(Greeting.EVENING, greetingForHour(18))
    }

    @Test
    fun `progress fraction is clamped and safe for a zero goal`() {
        assertEquals(0.5f, MetricProgress(current = 10, goal = 20).fraction)
        assertEquals(1f, MetricProgress(current = 30, goal = 20).fraction)
        assertEquals(0f, MetricProgress(current = 5, goal = 0).fraction)
    }
}
