package com.steady.app.feature.medication

import com.steady.app.domain.model.MedicationRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MedicationCatalogTest {
    @Test
    fun `dose parsing accepts comma decimals and rejects out-of-range values`() {
        assertEquals(2.5, parseDoseAmount(" 2,5 ")!!, 0.0)
        assertEquals(0.25, parseDoseAmount("0.25")!!, 0.0)
        assertNull(parseDoseAmount("0"))
        assertNull(parseDoseAmount("-1"))
        assertNull(parseDoseAmount("5000"))
        assertNull(parseDoseAmount("abc"))
    }

    @Test
    fun `strength shortcuts depend on medication and route`() {
        assertEquals(listOf(3.0, 7.0, 14.0), commonStrengths("semaglutide", MedicationRoute.ORAL))
        assertEquals(2.4, commonStrengths("Semaglutide", MedicationRoute.INJECTION).last(), 0.0)
        assertTrue(commonStrengths("Something else", MedicationRoute.INJECTION).isEmpty())
    }
}
