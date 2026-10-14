package com.steady.app.feature.subscription

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyEquivalentTest {
    @Test
    fun `yearly usd price is spread over 52 weeks`() {
        val yearly = PlanOffer(39_990_000, "USD", "$39.99")
        assertEquals("$0.77", weeklyEquivalent(yearly, Locale.US))
    }

    @Test
    fun `zero-decimal currencies keep whole units`() {
        val yearly = PlanOffer(999_000_000_000, "VND", "999.000 ₫")
        assertEquals("19.212 ₫", weeklyEquivalent(yearly, Locale.forLanguageTag("vi-VN")).replace(' ', ' '))
    }
}
