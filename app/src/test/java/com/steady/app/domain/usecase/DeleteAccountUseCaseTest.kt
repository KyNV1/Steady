package com.steady.app.domain.usecase

import com.steady.app.data.repository.parseDeletionState
import com.steady.app.domain.repository.AccountDeletionError
import com.steady.app.domain.repository.AccountDeletionException
import com.steady.app.domain.repository.AccountDeletionState
import com.steady.app.domain.repository.AccountRepository
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteAccountUseCaseTest {
    private val accountRepository = mockk<AccountRepository>()
    private val trackingRepository = mockk<TrackingRepository>(relaxUnitFun = true)
    private val authRepository = mockk<AuthRepository>(relaxUnitFun = true)
    private val useCase = DeleteAccountUseCase(accountRepository, trackingRepository, authRepository)

    @Test
    fun `accepted deletion wipes local data and signs out`() = runTest {
        coEvery { accountRepository.requestAccountDeletion() } returns Result.success(AccountDeletionState.REQUESTED)

        useCase("user-1")

        coVerify(exactly = 1) { trackingRepository.clearUserData("user-1") }
        coVerify(exactly = 1) { authRepository.logout() }
    }

    @Test
    fun `failed or retry-required deletion keeps local data`() = runTest {
        coEvery { accountRepository.requestAccountDeletion() } returnsMany listOf(
            Result.failure(AccountDeletionException(AccountDeletionError.UNAVAILABLE)),
            Result.success(AccountDeletionState.RETRY_REQUIRED),
        )

        useCase("user-1")
        useCase("user-1")

        coVerify(exactly = 0) { trackingRepository.clearUserData(any()) }
        coVerify(exactly = 0) { authRepository.logout() }
    }

    @Test
    fun `unknown server state is treated as retry required`() {
        assertEquals(AccountDeletionState.COMPLETED, parseDeletionState(mapOf("state" to "COMPLETED")))
        assertEquals(AccountDeletionState.RETRY_REQUIRED, parseDeletionState(mapOf("state" to "DONE")))
        assertEquals(AccountDeletionState.RETRY_REQUIRED, parseDeletionState(null))
    }
}
