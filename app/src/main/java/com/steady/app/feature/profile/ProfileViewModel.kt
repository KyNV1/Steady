package com.steady.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.ThemeMode
import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.consecutiveDayStreak
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import com.steady.app.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What the "next dose" banner should say, derived from the medication profile and dose history. */
sealed interface DoseSchedule {
    data object NoMedication : DoseSchedule
    data object NoDoseLogged : DoseSchedule

    /** [daysUntil] is relative to today; zero or negative means the dose is due now. */
    data class Next(val dueEpochDay: Long, val daysUntil: Long, val lastTakenMillis: Long) : DoseSchedule
}

data class ProfileUiState(
    val displayName: String = "",
    val medication: MedicationProfile? = null,
    val streakDays: Int = 0,
    /** First day the user tracked anything locally; null until there's any activity. */
    val memberSinceEpochDay: Long? = null,
    val doseSchedule: DoseSchedule = DoseSchedule.NoMedication,
    val isLoggingOut: Boolean = false,
    val loggedOut: Boolean = false,
)

private data class ProfileData(
    val medication: MedicationProfile?,
    val activeDays: Set<Long>,
    val doses: List<DoseLog>,
)

private data class SessionState(val isLoggingOut: Boolean = false, val loggedOut: Boolean = false)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logout: LogoutUseCase,
    private val appDataStore: AppDataStore,
    authRepository: AuthRepository,
    trackingRepository: TrackingRepository,
) : ViewModel() {
    private val session = MutableStateFlow(SessionState())

    private val profileData = appDataStore.preferences.map { it.userId }.distinctUntilChanged().flatMapLatest { id ->
        combine(
            trackingRepository.observeMedicationProfile(id),
            trackingRepository.observeIntakeDays(id),
            trackingRepository.observeDoseLogs(id),
        ) { medication, days, doses -> ProfileData(medication, days, doses) }
    }

    val state: StateFlow<ProfileUiState> = combine(authRepository.currentUser, profileData, session) { user, data, session ->
        val today = localEpochDay(System.currentTimeMillis())
        ProfileUiState(
            displayName = user?.displayName.orEmpty(),
            medication = data.medication,
            streakDays = consecutiveDayStreak(data.activeDays, today),
            memberSinceEpochDay = memberSinceEpochDay(data.activeDays, data.doses, data.medication),
            doseSchedule = doseSchedule(data.medication, data.doses, today),
            isLoggingOut = session.isLoggingOut,
            loggedOut = session.loggedOut,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    val themeMode: StateFlow<ThemeMode> = appDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { appDataStore.setThemeMode(mode) }
    }

    fun signOut() {
        if (session.value.isLoggingOut) return
        viewModelScope.launch {
            session.update { it.copy(isLoggingOut = true) }
            logout.invoke()
            session.value = SessionState(loggedOut = true)
        }
    }
}

/** Earliest local day with any tracked activity; there's no server-side account creation date yet. */
internal fun memberSinceEpochDay(activeDays: Set<Long>, doses: List<DoseLog>, medication: MedicationProfile?): Long? =
    listOfNotNull(
        activeDays.minOrNull(),
        doses.minOfOrNull { localEpochDay(it.takenAtEpochMillis) },
        medication?.let { localEpochDay(it.updatedAtEpochMillis) },
    ).minOrNull()

/** Next due day = last logged dose + one interval of the profile's frequency. */
internal fun doseSchedule(medication: MedicationProfile?, doses: List<DoseLog>, today: Long): DoseSchedule {
    if (medication == null) return DoseSchedule.NoMedication
    val last = doses.maxByOrNull { it.takenAtEpochMillis } ?: return DoseSchedule.NoDoseLogged
    val interval = when (medication.frequency) {
        MedicationFrequency.WEEKLY -> 7
        MedicationFrequency.DAILY -> 1
    }
    val due = localEpochDay(last.takenAtEpochMillis) + interval
    return DoseSchedule.Next(dueEpochDay = due, daysUntil = due - today, lastTakenMillis = last.takenAtEpochMillis)
}
