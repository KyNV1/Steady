package com.steady.app.feature.splash

import app.cash.turbine.test
import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.AppPreferences
import com.steady.app.core.testing.MainDispatcherRule
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private val appDataStore = mockk<AppDataStore>()
    private val trackingRepository = mockk<TrackingRepository>()

    private fun viewModel() = SplashViewModel(authRepository, appDataStore, trackingRepository)

    @Test
    fun `first install with no onboarding seen goes to onboarding`() = runTest {
        every { authRepository.isAuthenticated } returns flowOf(false)
        every { appDataStore.preferences } returns flowOf(AppPreferences(onboardingCompleted = false))

        viewModel().destination.test {
            assertEquals(SplashDestination.ONBOARDING, awaitItem())
        }
    }

    @Test
    fun `skipped onboarding but signed out goes straight to sign in`() = runTest {
        every { authRepository.isAuthenticated } returns flowOf(false)
        every { appDataStore.preferences } returns flowOf(AppPreferences(onboardingCompleted = true))

        viewModel().destination.test {
            assertEquals(SplashDestination.SIGN_IN, awaitItem())
        }
    }

    @Test
    fun `returning authenticated user with a saved profile goes home`() = runTest {
        every { authRepository.isAuthenticated } returns flowOf(true)
        every { appDataStore.preferences } returns flowOf(AppPreferences(userId = "42", onboardingCompleted = true))
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(sampleProfile())

        viewModel().destination.test {
            assertEquals(SplashDestination.HOME, awaitItem())
        }
    }

    @Test
    fun `authenticated user without a saved profile resumes profile setup`() = runTest {
        every { authRepository.isAuthenticated } returns flowOf(true)
        every { appDataStore.preferences } returns flowOf(AppPreferences(userId = "42", onboardingCompleted = true))
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(null)

        viewModel().destination.test {
            assertEquals(SplashDestination.PROFILE_SETUP, awaitItem())
        }
    }

    @Test
    fun `expired session drops the user back to sign in`() = runTest {
        val isAuthenticated = MutableStateFlow(true)
        every { authRepository.isAuthenticated } returns isAuthenticated
        every { appDataStore.preferences } returns flowOf(AppPreferences(userId = "42", onboardingCompleted = true))
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(sampleProfile())

        viewModel().destination.test {
            assertEquals(SplashDestination.HOME, awaitItem())
            isAuthenticated.value = false
            assertEquals(SplashDestination.SIGN_IN, awaitItem())
        }
    }

    private fun sampleProfile() = MedicationProfile(
        userId = "42",
        route = MedicationRoute.INJECTION,
        name = "Tirzepatide",
        doseAmount = 5.0,
        doseUnit = "mg",
        frequency = MedicationFrequency.WEEKLY,
        updatedAtEpochMillis = 0L,
    )
}
