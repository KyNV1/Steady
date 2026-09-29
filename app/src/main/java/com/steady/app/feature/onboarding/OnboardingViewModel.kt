package com.steady.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val appDataStore: AppDataStore) : ViewModel() {
    fun markCompleted(onDone: () -> Unit) {
        viewModelScope.launch {
            appDataStore.setOnboardingCompleted()
            onDone()
        }
    }
}
