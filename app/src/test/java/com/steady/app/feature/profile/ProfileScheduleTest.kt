package com.steady.app.feature.profile

import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.model.localEpochDay
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileScheduleTest {
    private val dayMillis = TimeUnit.DAYS.toMillis(1)
    private val noon = 20_000 * dayMillis + TimeUnit.HOURS.toMillis(12)

    private fun medication(frequency: MedicationFrequency, updatedAt: Long = noon) = MedicationProfile(
        userId = "u",
        route = MedicationRoute.INJECTION,
        name = "Tirzepatide",
        doseAmount = 5.0,
        doseUnit = "mg",
        frequency = frequency,
        updatedAtEpochMillis = updatedAt,
    )

    private fun dose(takenAt: Long) = DoseLog("d$takenAt", "u", "Tirzepatide", 5.0, "mg", takenAt, null, null)

    @Test
    fun `no medication or no dose yet are distinct states`() {
        assertEquals(DoseSchedule.NoMedication, doseSchedule(null, listOf(dose(noon)), 0))
        assertEquals(DoseSchedule.NoDoseLogged, doseSchedule(medication(MedicationFrequency.WEEKLY), emptyList(), 0))
    }

    @Test
    fun `weekly dose is due seven days after the latest log`() {
        val lastDay = localEpochDay(noon)
        val result = doseSchedule(
            medication(MedicationFrequency.WEEKLY),
            listOf(dose(noon - 7 * dayMillis), dose(noon)),
            today = lastDay + 2,
        )
        assertEquals(DoseSchedule.Next(lastDay + 7, 5, noon), result)
    }

    @Test
    fun `daily dose missed yesterday reports negative days until`() {
        val lastDay = localEpochDay(noon)
        val result = doseSchedule(medication(MedicationFrequency.DAILY), listOf(dose(noon)), today = lastDay + 3)
        assertEquals(DoseSchedule.Next(lastDay + 1, -2, noon), result)
    }

    @Test
    fun `member since is the earliest local activity`() {
        val doseDay = noon - 40 * dayMillis
        val since = memberSinceEpochDay(setOf(localEpochDay(noon)), listOf(dose(doseDay)), medication(MedicationFrequency.WEEKLY))
        assertEquals(localEpochDay(doseDay), since)
        assertNull(memberSinceEpochDay(emptySet(), emptyList(), null))
    }
}
