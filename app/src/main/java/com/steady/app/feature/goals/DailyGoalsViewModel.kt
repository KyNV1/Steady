package com.steady.app.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.DailyGoals
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

private const val ML_PER_OZ = 29.5735
private const val RECOMMENDED_FIBER_GRAMS = 25
private const val RECOMMENDED_PROTEIN_GRAMS = 70
private const val RECOMMENDED_WATER_OZ = 64
private const val FIBER_STEP = 5
private const val PROTEIN_STEP = 5
private const val WATER_STEP_OZ = 8

data class DailyGoalsUiState(
    val fiberGrams: Int = RECOMMENDED_FIBER_GRAMS,
    val proteinGrams: Int = RECOMMENDED_PROTEIN_GRAMS,
    val waterOunces: Int = RECOMMENDED_WATER_OZ,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
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
                    waterOunces = (goals.waterMilliliters / ML_PER_OZ).roundToInt(),
                    isLoading = false,
                )
            }
        }
    }

    fun incrementFiber() = mutableState.update { it.copy(fiberGrams = it.fiberGrams + FIBER_STEP) }
    fun decrementFiber() = mutableState.update { it.copy(fiberGrams = (it.fiberGrams - FIBER_STEP).coerceAtLeast(0)) }
    fun incrementProtein() = mutableState.update { it.copy(proteinGrams = it.proteinGrams + PROTEIN_STEP) }
    fun decrementProtein() = mutableState.update { it.copy(proteinGrams = (it.proteinGrams - PROTEIN_STEP).coerceAtLeast(0)) }
    fun incrementWater() = mutableState.update { it.copy(waterOunces = it.waterOunces + WATER_STEP_OZ) }
    fun decrementWater() = mutableState.update { it.copy(waterOunces = (it.waterOunces - WATER_STEP_OZ).coerceAtLeast(0)) }

    fun resetToRecommended() = mutableState.update {
        it.copy(
            fiberGrams = RECOMMENDED_FIBER_GRAMS,
            proteinGrams = RECOMMENDED_PROTEIN_GRAMS,
            waterOunces = RECOMMENDED_WATER_OZ,
        )
    }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        viewModelScope.launch {
            val userId = appDataStore.preferences.first().userId
            trackingRepository.saveDailyGoals(
                userId,
                DailyGoals(
                    proteinGrams = current.proteinGrams,
                    fiberGrams = current.fiberGrams,
                    waterMilliliters = (current.waterOunces * ML_PER_OZ).roundToInt(),
                ),
            )
            mutableState.update { it.copy(isSaved = true) }
            onSaved()
        }
    }
}
