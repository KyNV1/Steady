package com.steady.app.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.repository.AccountDeletionError
import com.steady.app.domain.repository.AccountDeletionException
import com.steady.app.domain.repository.AccountDeletionState
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.usecase.DeleteAccountUseCase
import com.steady.app.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** One-shot messages shown in the snackbar. */
enum class AccountMessage { PHOTO_COMING_SOON, DELETE_UNAVAILABLE, DELETE_REAUTH, DELETE_NETWORK, DELETE_RETRY }

data class AccountUiState(
    val displayName: String = "",
    val email: String = "",
    val isBusy: Boolean = false,
    val signedOut: Boolean = false,
    val message: AccountMessage? = null,
)

private data class AccountSession(
    val isBusy: Boolean = false,
    val signedOut: Boolean = false,
    val message: AccountMessage? = null,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val appDataStore: AppDataStore,
    private val logout: LogoutUseCase,
    private val deleteAccount: DeleteAccountUseCase,
) : ViewModel() {
    private val session = MutableStateFlow(AccountSession())

    val state: StateFlow<AccountUiState> = combine(authRepository.currentUser, session) { user, session ->
        AccountUiState(
            displayName = user?.displayName.orEmpty(),
            email = user?.email.orEmpty(),
            isBusy = session.isBusy,
            signedOut = session.signedOut,
            message = session.message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AccountUiState())

    fun onChangePhoto() = session.update { it.copy(message = AccountMessage.PHOTO_COMING_SOON) }

    fun onMessageShown() = session.update { it.copy(message = null) }

    fun signOut() = runExclusive {
        logout.invoke()
        session.value = AccountSession(signedOut = true)
    }

    fun deleteAccount() = runExclusive {
        val userId = appDataStore.preferences.first().userId
        deleteAccount.invoke(userId)
            .onSuccess { state ->
                session.value = if (state == AccountDeletionState.RETRY_REQUIRED) {
                    AccountSession(message = AccountMessage.DELETE_RETRY)
                } else {
                    AccountSession(signedOut = true)
                }
            }
            .onFailure { error -> session.value = AccountSession(message = deletionMessage(error)) }
    }

    private fun runExclusive(block: suspend () -> Unit) {
        if (session.value.isBusy) return
        session.update { it.copy(isBusy = true) }
        viewModelScope.launch { block() }
    }
}

internal fun deletionMessage(error: Throwable): AccountMessage = when ((error as? AccountDeletionException)?.error) {
    AccountDeletionError.REAUTHENTICATION_REQUIRED -> AccountMessage.DELETE_REAUTH
    AccountDeletionError.NETWORK -> AccountMessage.DELETE_NETWORK
    AccountDeletionError.UNAVAILABLE, null -> AccountMessage.DELETE_UNAVAILABLE
}
