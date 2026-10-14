package com.steady.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val appDataStore: AppDataStore) : ViewModel() {
    private var completing = false

    /** Runs [onDone] once even if Skip / Get started / Sign in are tapped repeatedly or together. */
    fun markCompleted(onDone: () -> Unit) {
        if (completing) return
        completing = true
        viewModelScope.launch {
            appDataStore.setOnboardingCompleted()
            onDone()
        }
    }
}
