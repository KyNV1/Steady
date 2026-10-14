package com.steady.app.domain.usecase

import com.steady.app.domain.repository.AccountDeletionState
import com.steady.app.domain.repository.AccountRepository
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import javax.inject.Inject

/**
 * Requests server-side deletion, then — only once the server has accepted it — wipes this user's
 * local health data and signs out. A RETRY_REQUIRED or failed request leaves everything in place.
 */
class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val trackingRepository: TrackingRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(userId: String): Result<AccountDeletionState> {
        val result = accountRepository.requestAccountDeletion()
        val state = result.getOrNull() ?: return result
        if (state != AccountDeletionState.RETRY_REQUIRED) {
            if (userId.isNotBlank()) trackingRepository.clearUserData(userId)
            authRepository.logout()
        }
        return result
    }
}
