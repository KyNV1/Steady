package com.steady.app.feature.medication

import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.AppPreferences
import com.steady.app.core.testing.MainDispatcherRule
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.repository.TrackingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MedicationDoseViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val trackingRepository = mockk<TrackingRepository>()
    private val appDataStore = mockk<AppDataStore>()

    private fun viewModel(): MedicationDoseViewModel {
        every { appDataStore.preferences } returns flowOf(AppPreferences(userId = "42"))
        return MedicationDoseViewModel(trackingRepository, appDataStore)
    }

    @Test
    fun `no saved profile leaves editable defaults`() = runTest {
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(null)

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertEquals(MedicationRoute.INJECTION, state.route)
        assertEquals("", state.name)
    }

    @Test
    fun `existing profile is loaded into state`() = runTest {
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(
            MedicationProfile("42", MedicationRoute.ORAL, "Semaglutide", 7.0, "mg", MedicationFrequency.DAILY, 0L),
        )

        val state = viewModel().state.value

        assertEquals(MedicationRoute.ORAL, state.route)
        assertEquals("Semaglutide", state.name)
        assertEquals("7", state.doseAmount)
        assertEquals(MedicationFrequency.DAILY, state.frequency)
    }

    @Test
    fun `save is a no-op when name is blank`() = runTest {
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(null)
        val vm = viewModel()
        vm.setDoseAmount("5")
        var saved = false

        vm.save { saved = true }

        assertFalse(saved)
        coVerify(exactly = 0) { trackingRepository.saveMedicationProfile(any()) }
    }

    @Test
    fun `save persists the profile and invokes the callback`() = runTest {
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(null)
        coEvery { trackingRepository.saveMedicationProfile(any()) } returns Unit
        val vm = viewModel()
        vm.setName("Tirzepatide")
        vm.setDoseAmount("5")
        var saved = false

        vm.save { saved = true }

        assertTrue(saved)
        coVerify(exactly = 1) {
            trackingRepository.saveMedicationProfile(
                match { it.userId == "42" && it.name == "Tirzepatide" && it.doseAmount == 5.0 },
            )
        }
    }
}
