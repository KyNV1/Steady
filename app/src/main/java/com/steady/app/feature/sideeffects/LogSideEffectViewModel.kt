package com.steady.app.feature.sideeffects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.model.SideEffectLog
import com.steady.app.domain.model.SideEffectSeverity
import com.steady.app.domain.model.SideEffectSymptom
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val SIDE_EFFECT_NOTE_MAX_LENGTH = 240

data class LogSideEffectUiState(
    val symptoms: List<SideEffectSymptom> = SideEffectSymptom.entries,
    val selected: Set<SideEffectSymptom> = emptySet(),
    val severity: SideEffectSeverity? = null,
    val note: String = "",
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = selected.isNotEmpty() && severity != null && !isSaving
    val showSevereWarning: Boolean get() = severity == SideEffectSeverity.SEVERE
}

@HiltViewModel
class LogSideEffectViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LogSideEffectUiState())
    val state: StateFlow<LogSideEffectUiState> = mutableState.asStateFlow()

    init {
        // "Injection site" only makes sense for injected medication; hide it for oral users.
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            val medication = trackingRepository.observeMedicationProfile(userId).first()
            if (medication?.route == MedicationRoute.ORAL) {
                mutableState.update { state ->
                    state.copy(
                        symptoms = state.symptoms - SideEffectSymptom.INJECTION_SITE,
                        selected = state.selected - SideEffectSymptom.INJECTION_SITE,
                    )
                }
            }
        }
    }

    fun toggle(symptom: SideEffectSymptom) = mutableState.update { state ->
        state.copy(selected = if (symptom in state.selected) state.selected - symptom else state.selected + symptom)
    }

    fun setSeverity(severity: SideEffectSeverity) = mutableState.update { it.copy(severity = severity) }

    fun setNote(note: String) = mutableState.update { it.copy(note = note.take(SIDE_EFFECT_NOTE_MAX_LENGTH)) }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        val severity = current.severity
        if (!current.canSave || severity == null) return
        mutableState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            trackingRepository.saveSideEffect(
                SideEffectLog(
                    id = UUID.randomUUID().toString(),
                    userId = appDataStore.preferences.first().userId,
                    epochDay = localEpochDay(now),
                    recordedAtEpochMillis = now,
                    symptoms = current.selected.map { it.key }.toSet(),
                    severity = severity,
                    note = current.note.trim().ifBlank { null },
                ),
            )
            onSaved()
        }
    }
}
