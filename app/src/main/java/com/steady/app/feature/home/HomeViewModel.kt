package com.steady.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.DailyIntake
import com.steady.app.domain.model.ML_PER_FL_OZ
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.consecutiveDayStreak
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.AuthRepository
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val FIBER_QUICK_ADD_GRAMS = 1
const val PROTEIN_QUICK_ADD_GRAMS = 5
const val WATER_QUICK_ADD_OZ = 8

enum class Greeting { MORNING, AFTERNOON, EVENING }

fun greetingForHour(hourOfDay: Int): Greeting = when (hourOfDay) {
    in 5..11 -> Greeting.MORNING
    in 12..17 -> Greeting.AFTERNOON
    else -> Greeting.EVENING
}

data class MetricProgress(val current: Int, val goal: Int) {
    val fraction: Float get() = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val displayName: String? = null,
    val greeting: Greeting = Greeting.MORNING,
    val todayMillis: Long = 0L,
    val streakDays: Int = 0,
    val fiber: MetricProgress = MetricProgress(0, 0),
    val protein: MetricProgress = MetricProgress(0, 0),
    val waterOunces: MetricProgress = MetricProgress(0, 0),
    val medication: MedicationProfile? = null,
)

private data class Today(val epochDay: Long, val millis: Long)

private fun currentDay(): Today {
    val now = System.currentTimeMillis()
    return Today(localEpochDay(now), now)
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    authRepository: AuthRepository,
    appDataStore: AppDataStore,
) : ViewModel() {
    private val today = MutableStateFlow(currentDay())
    private val userId = appDataStore.preferences.map { it.userId }.distinctUntilChanged()

    private val trackingData = combine(userId, today) { id, day -> id to day }.flatMapLatest { (id, day) ->
        combine(
            trackingRepository.observeDailyGoals(id),
            trackingRepository.observeDailyIntake(id, day.epochDay),
            trackingRepository.observeIntakeDays(id),
            trackingRepository.observeMedicationProfile(id),
        ) { goals, intake, activeDays, medication ->
            TrackingSnapshot(day, goals, intake, consecutiveDayStreak(activeDays, day.epochDay), medication)
        }
    }

    val state: StateFlow<HomeUiState> = combine(authRepository.currentUser, trackingData) { user, data ->
        HomeUiState(
            isLoading = false,
            displayName = user?.displayName?.substringBefore(' ')?.takeIf { it.isNotBlank() },
            greeting = greetingForHour(Calendar.getInstance().apply { timeInMillis = data.day.millis }[Calendar.HOUR_OF_DAY]),
            todayMillis = data.day.millis,
            streakDays = data.streak,
            fiber = MetricProgress(data.intake.fiberGrams, data.goals.fiberGrams),
            protein = MetricProgress(data.intake.proteinGrams, data.goals.proteinGrams),
            waterOunces = MetricProgress(data.intake.waterMilliliters.toOunces(), data.goals.waterMilliliters.toOunces()),
            medication = data.medication,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    /** Re-reads the clock so a screen left open past midnight starts a fresh day. */
    fun refreshDay() {
        today.value = currentDay()
    }

    fun adjustFiber(increase: Boolean) = adjust(fiber = if (increase) FIBER_QUICK_ADD_GRAMS else -FIBER_QUICK_ADD_GRAMS)
    fun adjustProtein(increase: Boolean) = adjust(protein = if (increase) PROTEIN_QUICK_ADD_GRAMS else -PROTEIN_QUICK_ADD_GRAMS)
    fun adjustWater(increase: Boolean) {
        val ml = (WATER_QUICK_ADD_OZ * ML_PER_FL_OZ).roundToInt()
        adjust(waterMl = if (increase) ml else -ml)
    }

    private fun adjust(fiber: Int = 0, protein: Int = 0, waterMl: Int = 0) {
        viewModelScope.launch {
            val id = userId.first()
            if (id.isBlank()) return@launch
            trackingRepository.adjustDailyIntake(id, today.value.epochDay, fiber, protein, waterMl)
        }
    }
}

private data class TrackingSnapshot(
    val day: Today,
    val goals: DailyGoals,
    val intake: DailyIntake,
    val streak: Int,
    val medication: MedicationProfile?,
)

private fun Int.toOunces(): Int = (this / ML_PER_FL_OZ).roundToInt()
