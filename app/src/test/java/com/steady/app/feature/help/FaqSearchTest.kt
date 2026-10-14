package com.steady.app.feature.help

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FaqSearchTest {
    private val question = "How do I cancel my subscription?"
    private val answer = "Subscriptions are managed by Google Play."

    @Test
    fun `blank query matches everything`() {
        assertTrue(faqMatches(question, answer, "  "))
    }

    @Test
    fun `every term must appear in question or answer, ignoring case`() {
        assertTrue(faqMatches(question, answer, "CANCEL play"))
        assertFalse(faqMatches(question, answer, "cancel dose"))
    }
}
