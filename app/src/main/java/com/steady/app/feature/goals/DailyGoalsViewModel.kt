package com.steady.app.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.ML_PER_FL_OZ
import com.steady.app.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RECOMMENDED_FIBER_GRAMS = 25
private const val RECOMMENDED_PROTEIN_GRAMS = 70
private const val RECOMMENDED_WATER_OZ = 64

/** Step size and allowed range for one goal's stepper. */
data class GoalRange(val step: Int, val min: Int, val max: Int) {
    fun canDecrement(value: Int) = value > min
    fun canIncrement(value: Int) = value < max

    /** Moves one step toward the bound; a value already outside the range only moves back inside. */
    fun decrement(value: Int) = if (canDecrement(value)) (value - step).coerceIn(min, max) else value
    fun increment(value: Int) = if (canIncrement(value)) (value + step).coerceIn(min, max) else value
}

val FIBER_RANGE = GoalRange(step = 1, min = 10, max = 60)
val PROTEIN_RANGE = GoalRange(step = 5, min = 30, max = 180)
val WATER_OZ_RANGE = GoalRange(step = 4, min = 32, max = 160)

data class DailyGoalsUiState(
    val fiberGrams: Int = RECOMMENDED_FIBER_GRAMS,
    val proteinGrams: Int = RECOMMENDED_PROTEIN_GRAMS,
    val waterOunces: Int = RECOMMENDED_WATER_OZ,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
)

@HiltViewModel
class DailyGoalsViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private val mutableState = MutableStateFlow(DailyGoalsUiState())
    val state: StateFlow<DailyGoalsUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            val goals = trackingRepository.observeDailyGoals(userId).first()
            mutableState.update {
                it.copy(
                    fiberGrams = goals.fiberGrams,
                    proteinGrams = goals.proteinGrams,
                    waterOunces = (goals.waterMilliliters / ML_PER_FL_OZ).roundToInt(),
                    isLoading = false,
                )
            }
        }
    }

    fun incrementFiber() = mutableState.update { it.copy(fiberGrams = FIBER_RANGE.increment(it.fiberGrams)) }
    fun decrementFiber() = mutableState.update { it.copy(fiberGrams = FIBER_RANGE.decrement(it.fiberGrams)) }
    fun incrementProtein() = mutableState.update { it.copy(proteinGrams = PROTEIN_RANGE.increment(it.proteinGrams)) }
    fun decrementProtein() = mutableState.update { it.copy(proteinGrams = PROTEIN_RANGE.decrement(it.proteinGrams)) }
    fun incrementWater() = mutableState.update { it.copy(waterOunces = WATER_OZ_RANGE.increment(it.waterOunces)) }
    fun decrementWater() = mutableState.update { it.copy(waterOunces = WATER_OZ_RANGE.decrement(it.waterOunces)) }

    fun resetToRecommended() = mutableState.update {
        it.copy(
            fiberGrams = RECOMMENDED_FIBER_GRAMS,
            proteinGrams = RECOMMENDED_PROTEIN_GRAMS,
            waterOunces = RECOMMENDED_WATER_OZ,
        )
    }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        if (current.isSaving) return
        mutableState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            trackingRepository.saveDailyGoals(
                userId,
                DailyGoals(
                    proteinGrams = current.proteinGrams,
                    fiberGrams = current.fiberGrams,
                    waterMilliliters = (current.waterOunces * ML_PER_FL_OZ).roundToInt(),
                ),
            )
            onSaved()
        }
    }
}
