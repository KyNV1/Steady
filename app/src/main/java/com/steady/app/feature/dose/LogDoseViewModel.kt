package com.steady.app.feature.dose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.InjectionSite
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val DOSE_NOTE_MAX_LENGTH = 200

data class LogDoseUiState(
    val isLoading: Boolean = true,
    val medication: MedicationProfile? = null,
    val site: InjectionSite? = null,
    val takenAtMillis: Long = 0L,
    val note: String = "",
    val isSaving: Boolean = false,
) {
    val isInjection: Boolean get() = medication?.route == MedicationRoute.INJECTION
    val canSave: Boolean get() = medication != null && (!isInjection || site != null) && !isSaving
}

/** Next site in the abdomen → thigh → arm rotation, so consecutive injections don't reuse a spot. */
fun nextInjectionSite(last: InjectionSite?): InjectionSite = when (last) {
    null, InjectionSite.ARM -> InjectionSite.ABDOMEN
    InjectionSite.ABDOMEN -> InjectionSite.THIGH
    InjectionSite.THIGH -> InjectionSite.ARM
}

/**
 * Material's DatePicker returns the chosen day as UTC midnight; keep that calendar date and
 * attach the local [hour]:[minute] in [zone].
 */
fun combineDateAndTime(dateUtcMillis: Long, hour: Int, minute: Int, zone: TimeZone = TimeZone.getDefault()): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = dateUtcMillis }
    return Calendar.getInstance(zone).apply {
        clear()
        set(utc[Calendar.YEAR], utc[Calendar.MONTH], utc[Calendar.DAY_OF_MONTH], hour, minute)
    }.timeInMillis
}

@HiltViewModel
class LogDoseViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LogDoseUiState(takenAtMillis = System.currentTimeMillis()))
    val state: StateFlow<LogDoseUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            val medication = trackingRepository.observeMedicationProfile(userId).first()
            val lastSite = trackingRepository.observeDoseLogs(userId).first().firstOrNull { it.injectionSite != null }?.injectionSite
            mutableState.update {
                it.copy(
                    isLoading = false,
                    medication = medication,
                    site = if (medication?.route == MedicationRoute.INJECTION) nextInjectionSite(lastSite) else null,
                )
            }
        }
    }

    fun setSite(site: InjectionSite) = mutableState.update { it.copy(site = site) }

    fun setDate(dateUtcMillis: Long) = mutableState.update { state ->
        val local = Calendar.getInstance().apply { timeInMillis = state.takenAtMillis }
        state.copy(takenAtMillis = clampToNow(combineDateAndTime(dateUtcMillis, local[Calendar.HOUR_OF_DAY], local[Calendar.MINUTE])))
    }

    fun setTime(hour: Int, minute: Int) = mutableState.update { state ->
        val local = Calendar.getInstance().apply {
            timeInMillis = state.takenAtMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        state.copy(takenAtMillis = clampToNow(local.timeInMillis))
    }

    fun setNote(note: String) = mutableState.update { it.copy(note = note.take(DOSE_NOTE_MAX_LENGTH)) }

    /** Quick tag chips append their phrase to the note instead of replacing what was typed. */
    fun appendTag(tag: String) = mutableState.update { state ->
        val joined = if (state.note.isBlank()) tag else "${state.note.trimEnd()}, $tag"
        state.copy(note = joined.take(DOSE_NOTE_MAX_LENGTH))
    }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        val medication = current.medication
        if (!current.canSave || medication == null) return
        mutableState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            trackingRepository.saveDose(
                DoseLog(
                    id = UUID.randomUUID().toString(),
                    userId = medication.userId,
                    medicationName = medication.name,
                    doseAmount = medication.doseAmount,
                    doseUnit = medication.doseUnit,
                    takenAtEpochMillis = current.takenAtMillis,
                    injectionSite = if (current.isInjection) current.site else null,
                    note = current.note.trim().ifBlank { null },
                ),
            )
            onSaved()
        }
    }

    // A dose can't be logged in the future; picking "now + later today" snaps back to now.
    private fun clampToNow(millis: Long): Long = minOf(millis, System.currentTimeMillis())
}
