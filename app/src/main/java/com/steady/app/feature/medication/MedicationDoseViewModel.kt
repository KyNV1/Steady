package com.steady.app.feature.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.repository.TrackingRepository
import com.steady.app.feature.dose.formatDoseAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationDoseUiState(
    val route: MedicationRoute = MedicationRoute.INJECTION,
    val name: String = "",
    val doseAmount: String = "",
    val doseUnit: String = "mg",
    val frequency: MedicationFrequency = MedicationFrequency.WEEKLY,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
) {
    val canSave: Boolean get() = name.isNotBlank() && parseDoseAmount(doseAmount) != null
}

@HiltViewModel
class MedicationDoseViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MedicationDoseUiState())
    val state: StateFlow<MedicationDoseUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            val existing = trackingRepository.observeMedicationProfile(userId).first()
            mutableState.update {
                if (existing != null) {
                    it.copy(
                        route = existing.route,
                        name = existing.name,
                        doseAmount = formatDoseAmount(existing.doseAmount),
                        doseUnit = existing.doseUnit,
                        frequency = existing.frequency,
                        isLoading = false,
                    )
                } else {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun setRoute(route: MedicationRoute) = mutableState.update { it.copy(route = route) }
    fun setName(name: String) = mutableState.update { it.copy(name = name) }
    fun setDoseAmount(value: String) = mutableState.update { it.copy(doseAmount = value) }
    fun setFrequency(frequency: MedicationFrequency) = mutableState.update { it.copy(frequency = frequency) }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        if (!current.canSave || current.isSaved) return
        val amount = parseDoseAmount(current.doseAmount) ?: return
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            trackingRepository.saveMedicationProfile(
                MedicationProfile(
                    userId = userId,
                    route = current.route,
                    name = current.name.trim(),
                    doseAmount = amount,
                    doseUnit = current.doseUnit,
                    frequency = current.frequency,
                    updatedAtEpochMillis = System.currentTimeMillis(),
                ),
            )
            mutableState.update { it.copy(isSaved = true) }
            onSaved()
        }
    }
}
