package com.steady.app.feature.goals

import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.AppPreferences
import com.steady.app.core.testing.MainDispatcherRule
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.repository.TrackingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DailyGoalsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val trackingRepository = mockk<TrackingRepository>()
    private val appDataStore = mockk<AppDataStore>()

    private fun viewModel(goals: DailyGoals): DailyGoalsViewModel {
        every { appDataStore.preferences } returns flowOf(AppPreferences(userId = "42"))
        every { trackingRepository.observeDailyGoals("42") } returns flowOf(goals)
        return DailyGoalsViewModel(trackingRepository, appDataStore)
    }

    @Test
    fun `recommended defaults load in the display unit`() = runTest {
        val state = viewModel(DailyGoals(proteinGrams = 70, fiberGrams = 25, waterMilliliters = 1893)).state.value

        assertEquals(70, state.proteinGrams)
        assertEquals(25, state.fiberGrams)
        assertEquals(64, state.waterOunces)
    }

    @Test
    fun `stepper increments and decrements by fixed steps`() = runTest {
        val vm = viewModel(DailyGoals(proteinGrams = 70, fiberGrams = 25, waterMilliliters = 1893))

        vm.incrementFiber()
        vm.decrementProtein()
        vm.incrementWater()

        val state = vm.state.value
        assertEquals(30, state.fiberGrams)
        assertEquals(65, state.proteinGrams)
        assertEquals(72, state.waterOunces)
    }

    @Test
    fun `reset restores recommended defaults after edits`() = runTest {
        val vm = viewModel(DailyGoals(proteinGrams = 70, fiberGrams = 25, waterMilliliters = 1893))
        vm.incrementFiber()
        vm.incrementProtein()
        vm.incrementWater()

        vm.resetToRecommended()

        val state = vm.state.value
        assertEquals(25, state.fiberGrams)
        assertEquals(70, state.proteinGrams)
        assertEquals(64, state.waterOunces)
    }

    @Test
    fun `save converts ounces back to milliliters`() = runTest {
        coEvery { trackingRepository.saveDailyGoals(any(), any()) } returns Unit
        val vm = viewModel(DailyGoals(proteinGrams = 70, fiberGrams = 25, waterMilliliters = 1893))

        vm.save {}

        coVerify(exactly = 1) {
            trackingRepository.saveDailyGoals(
                "42",
                match { it.proteinGrams == 70 && it.fiberGrams == 25 && it.waterMilliliters in 1880..1900 },
            )
        }
    }
}
