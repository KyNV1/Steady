package com.steady.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class SplashDestination { ONBOARDING, SIGN_IN, PROFILE_SETUP, HOME }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository,
    appDataStore: AppDataStore,
    trackingRepository: TrackingRepository,
) : ViewModel() {
    val destination: StateFlow<SplashDestination?> = combine(
        authRepository.isAuthenticated,
        appDataStore.preferences,
    ) { authenticated, preferences -> authenticated to preferences }
        .flatMapLatest { (authenticated, preferences) ->
            when {
                !authenticated && !preferences.onboardingCompleted -> flowOf(SplashDestination.ONBOARDING)
                !authenticated -> flowOf(SplashDestination.SIGN_IN)
                else -> trackingRepository.observeMedicationProfile(preferences.userId)
                    .map { profile -> if (profile != null) SplashDestination.HOME else SplashDestination.PROFILE_SETUP }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
