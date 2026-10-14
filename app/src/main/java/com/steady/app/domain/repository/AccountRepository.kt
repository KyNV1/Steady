package com.steady.app.domain.repository

enum class AccountDeletionState { REQUESTED, RUNNING, COMPLETED, RETRY_REQUIRED }

enum class AccountDeletionError { UNAVAILABLE, REAUTHENTICATION_REQUIRED, NETWORK }

/** Failure carried by [AccountRepository.requestAccountDeletion]'s [Result]. */
class AccountDeletionException(val error: AccountDeletionError) : Exception("Account deletion failed: $error")

interface AccountRepository {
    suspend fun requestAccountDeletion(): Result<AccountDeletionState>
}
