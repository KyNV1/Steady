package com.steady.app.feature.dose

import com.steady.app.domain.model.InjectionSite
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogDoseLogicTest {
    @Test
    fun `injection site rotates abdomen, thigh, arm`() {
        assertEquals(InjectionSite.ABDOMEN, nextInjectionSite(null))
        assertEquals(InjectionSite.THIGH, nextInjectionSite(InjectionSite.ABDOMEN))
        assertEquals(InjectionSite.ARM, nextInjectionSite(InjectionSite.THIGH))
        assertEquals(InjectionSite.ABDOMEN, nextInjectionSite(InjectionSite.ARM))
    }

    @Test
    fun `picked UTC date keeps its calendar day in the local zone`() {
        val saigon = TimeZone.getTimeZone("Asia/Saigon")
        val sep22UtcMidnight = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.SEPTEMBER, 22)
        }.timeInMillis
        val result = Calendar.getInstance(saigon).apply { timeInMillis = combineDateAndTime(sep22UtcMidnight, 8, 0, saigon) }
        assertEquals(22, result[Calendar.DAY_OF_MONTH])
        assertEquals(8, result[Calendar.HOUR_OF_DAY])
        assertEquals(0, result[Calendar.MINUTE])
    }

    @Test
    fun `dose amount drops a trailing zero`() {
        assertEquals("5", formatDoseAmount(5.0))
        assertEquals("2.5", formatDoseAmount(2.5))
    }

    @Test
    fun `injection needs a site to save, oral does not`() {
        val injection = MedicationProfile("u", MedicationRoute.INJECTION, "T", 5.0, "mg", MedicationFrequency.WEEKLY, 0L)
        assertFalse(LogDoseUiState(isLoading = false, medication = injection).canSave)
        assertTrue(LogDoseUiState(isLoading = false, medication = injection, site = InjectionSite.ARM).canSave)
        assertTrue(LogDoseUiState(isLoading = false, medication = injection.copy(route = MedicationRoute.ORAL)).canSave)
        assertFalse(LogDoseUiState(isLoading = false).canSave)
    }
}
