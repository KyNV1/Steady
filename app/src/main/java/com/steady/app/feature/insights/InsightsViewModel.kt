package com.steady.app.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.DailyIntake
import com.steady.app.domain.model.consecutiveDayStreak
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DAYS_IN_WEEK = 7
private const val ON_TRACK_RATIO = 0.9

data class ProteinDay(val epochDay: Long, val grams: Int, val isToday: Boolean)

data class InsightsSummary(
    /** Last seven local days, oldest first, ending today. */
    val week: List<ProteinDay>,
    val todayGrams: Int,
    val goalGrams: Int,
    /** Average over days that have any log; null when nothing was logged this week. */
    val averageGrams: Int?,
    /** This week's average minus last week's; null unless both weeks have logs. */
    val averageDeltaGrams: Int?,
    val isOnTrack: Boolean,
    val streakDays: Int,
) {
    val gramsToGoal: Int get() = (goalGrams - todayGrams).coerceAtLeast(0)
}

/**
 * Builds the Insights numbers from raw daily intake. A day only counts toward averages once
 * something was logged, so an untouched day doesn't drag the average to zero.
 */
fun summarizeProtein(intakes: List<DailyIntake>, today: Long, goalGrams: Int, activeDays: Set<Long>): InsightsSummary {
    val byDay = intakes.associateBy { it.epochDay }
    val thisWeekDays = (today - DAYS_IN_WEEK + 1..today)
    val lastWeekDays = (today - 2 * DAYS_IN_WEEK + 1..today - DAYS_IN_WEEK)

    fun averageOf(days: LongRange): Int? {
        val logged = days.mapNotNull { day -> byDay[day]?.takeIf { day in activeDays }?.proteinGrams }
        return if (logged.isEmpty()) null else logged.average().roundToInt()
    }

    val average = averageOf(thisWeekDays)
    val previous = averageOf(lastWeekDays)
    return InsightsSummary(
        week = thisWeekDays.map { day -> ProteinDay(day, byDay[day]?.proteinGrams ?: 0, isToday = day == today) },
        todayGrams = byDay[today]?.proteinGrams ?: 0,
        goalGrams = goalGrams,
        averageGrams = average,
        averageDeltaGrams = if (average != null && previous != null) average - previous else null,
        isOnTrack = average != null && goalGrams > 0 && average >= goalGrams * ON_TRACK_RATIO,
        streakDays = consecutiveDayStreak(activeDays, today),
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    trackingRepository: TrackingRepository,
    appDataStore: AppDataStore,
) : ViewModel() {
    private val today = MutableStateFlow(localEpochDay(System.currentTimeMillis()))

    val state: StateFlow<InsightsSummary?> =
        combine(appDataStore.preferences.map { it.userId }.distinctUntilChanged(), today) { id, day -> id to day }
            .flatMapLatest { (userId, day) ->
                combine(
                    trackingRepository.observeDailyIntakeRange(userId, day - 2 * DAYS_IN_WEEK + 1, day),
                    trackingRepository.observeDailyGoals(userId),
                    trackingRepository.observeIntakeDays(userId),
                ) { intakes, goals, activeDays -> summarizeProtein(intakes, day, goals.proteinGrams, activeDays) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Re-reads the clock so the week rolls over if the screen stays open past midnight. */
    fun refreshDay() {
        today.value = localEpochDay(System.currentTimeMillis())
    }
}
