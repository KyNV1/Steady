package com.steady.app.domain.usecase

import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.model.AuthToken
import com.steady.app.domain.model.User
import com.steady.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginUseCaseTest {
    private val repository = mockk<AuthRepository>()
    private val useCase = LoginUseCase(repository)

    @Test
    fun `delegates google id token to repository`() = runTest {
        val session = AuthSession(User("1", "user@example.com", "User"), AuthToken("a", "r"))
        coEvery { repository.login("id-token") } returns Result.success(session)

        val result = useCase("id-token")

        assertEquals(session, result.getOrThrow())
        coVerify(exactly = 1) { repository.login("id-token") }
    }
}
