package com.steady.app.domain.repository

enum class AccountDeletionState { REQUESTED, RUNNING, COMPLETED, RETRY_REQUIRED }

interface AccountRepository {
    suspend fun requestAccountDeletion(): Result<AccountDeletionState>
}
