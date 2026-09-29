package com.steady.app.feature.auth.signin

import com.steady.app.core.testing.MainDispatcherRule
import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.model.AuthToken
import com.steady.app.domain.model.User
import com.steady.app.domain.repository.TrackingRepository
import com.steady.app.domain.usecase.LoginUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SignInViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val login = mockk<LoginUseCase>()
    private val trackingRepository = mockk<TrackingRepository>()
    private val viewModel = SignInViewModel(login, trackingRepository)

    @Test
    fun `signing in with an existing medication profile routes home`() = runTest {
        coEvery { login("token") } returns Result.success(session())
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(mockk(relaxed = true))

        viewModel.signInWithGoogle("token")

        val state = viewModel.state.value
        assertEquals(true, state.isAuthenticated)
        assertEquals(PostAuthDestination.HOME, state.nextDestination)
    }

    @Test
    fun `signing in without a medication profile routes to setup`() = runTest {
        coEvery { login("token") } returns Result.success(session())
        every { trackingRepository.observeMedicationProfile("42") } returns flowOf(null)

        viewModel.signInWithGoogle("token")

        assertEquals(PostAuthDestination.PROFILE_SETUP, viewModel.state.value.nextDestination)
    }

    @Test
    fun `a cancelled sign-in surfaces a message without authenticating`() {
        viewModel.onGoogleSignInFailed("Sign-in was cancelled.")

        val state = viewModel.state.value
        assertEquals(false, state.isAuthenticated)
        assertEquals("Sign-in was cancelled.", state.errorMessage)
        assertNull(state.nextDestination)
    }

    private fun session() = AuthSession(User("42", "user@example.com", "User"), AuthToken("a", "r"))
}
