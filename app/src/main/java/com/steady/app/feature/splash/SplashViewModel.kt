package com.steady.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class SplashDestination { SIGN_IN, HOME }

@HiltViewModel
class SplashViewModel @Inject constructor(repository: AuthRepository) : ViewModel() {
    val destination: StateFlow<SplashDestination?> = repository.isAuthenticated
        .map { authenticated -> if (authenticated) SplashDestination.HOME else SplashDestination.SIGN_IN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
