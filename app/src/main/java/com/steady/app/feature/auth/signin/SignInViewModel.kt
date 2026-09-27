package com.steady.app.feature.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class SignInViewModel @Inject constructor(private val login: LoginUseCase) : ViewModel() {
    private val mutableState = MutableStateFlow(SignInUiState())
    val state: StateFlow<SignInUiState> = mutableState.asStateFlow()

    fun signInWithGoogle(idToken: String) = signIn(idToken)

    fun signInAsDemo() = signIn(googleIdToken = "")

    fun onGoogleSignInFailed(message: String) {
        mutableState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    private fun signIn(googleIdToken: String) {
        if (mutableState.value.isLoading) return
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            login(googleIdToken).fold(
                onSuccess = { mutableState.update { state -> state.copy(isLoading = false, isAuthenticated = true) } },
                onFailure = { error ->
                    mutableState.update { state ->
                        state.copy(isLoading = false, errorMessage = error.message ?: "Sign-in failed.")
                    }
                },
            )
        }
    }
}
