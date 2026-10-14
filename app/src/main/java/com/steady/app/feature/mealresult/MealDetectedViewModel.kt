package com.steady.app.feature.mealresult

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.steady.app.BuildConfig
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.MealLog
import com.steady.app.domain.model.MealScanFailure
import com.steady.app.domain.model.MealScanRequest
import com.steady.app.domain.model.MealScanResult
import com.steady.app.domain.model.NutritionEstimate
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.MealScanOutcome
import com.steady.app.domain.repository.MealScanRepository
import com.steady.app.domain.repository.TrackingRepository
import com.steady.app.navigation.MealDetectedRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MealAnalysis {
    data object Analyzing : MealAnalysis
    data class Detected(val result: MealScanResult) : MealAnalysis
    data class Failed(val reason: MealScanFailure) : MealAnalysis
}

/** Editable copy of the meal; numbers stay as text so the user can clear a field while typing. */
data class MealDraft(
    val name: String = "",
    val portion: String = "",
    val calories: String = "",
    val protein: String = "",
    val fiber: String = "",
    val carbs: String = "",
) {
    fun toNutrition(): NutritionEstimate? {
        val kcal = calories.trim().toIntOrNull()?.takeIf { it >= 0 } ?: return null
        fun grams(text: String) = text.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it >= 0 }
        return NutritionEstimate(kcal, grams(protein) ?: return null, grams(fiber) ?: return null, grams(carbs) ?: return null)
    }

    companion object {
        fun from(result: MealScanResult) = MealDraft(
            name = result.mealName,
            portion = result.portionDescription,
            calories = result.nutrition.calories.toString(),
            protein = formatGrams(result.nutrition.proteinGrams),
            fiber = formatGrams(result.nutrition.fiberGrams),
            carbs = formatGrams(result.nutrition.carbsGrams),
        )
    }
}

internal fun formatGrams(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else "%.1f".format(java.util.Locale.US, value)

data class MealDetectedUiState(
    val photoUri: String,
    val analysis: MealAnalysis = MealAnalysis.Analyzing,
    val draft: MealDraft? = null,
    val isEditing: Boolean = false,
    /** True once the user changed any AI value, so the confidence badge no longer applies. */
    val isEdited: Boolean = false,
    val isSaving: Boolean = false,
    val canUseSample: Boolean = BuildConfig.DEBUG,
) {
    val canSave: Boolean get() = draft != null && draft.name.isNotBlank() && draft.toNutrition() != null && !isSaving
    val confidencePercent: Int? get() = (analysis as? MealAnalysis.Detected)?.result?.confidencePercent?.takeIf { !isEdited }
}

@HiltViewModel
class MealDetectedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealScanRepository: MealScanRepository,
    private val trackingRepository: TrackingRepository,
    private val photoEncoder: MealPhotoEncoder,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    private val photoUri = savedStateHandle.toRoute<MealDetectedRoute>().photoUri
    // One key per photo so a retry after a timeout can't be billed twice by the backend.
    private val idempotencyKey = UUID.randomUUID().toString()

    private val mutableState = MutableStateFlow(MealDetectedUiState(photoUri = photoUri))
    val state: StateFlow<MealDetectedUiState> = mutableState.asStateFlow()

    init {
        analyze()
    }

    fun analyze() {
        mutableState.update { it.copy(analysis = MealAnalysis.Analyzing) }
        viewModelScope.launch {
            val bytes = photoEncoder.encode(photoUri.toUri())
            val outcome = if (bytes == null) {
                MealScanOutcome.Failure(MealScanFailure.InvalidImage)
            } else {
                mealScanRepository.analyze(MealScanRequest(bytes, "image/jpeg", idempotencyKey))
            }
            mutableState.update { state ->
                when (outcome) {
                    is MealScanOutcome.Success -> state.copy(
                        analysis = MealAnalysis.Detected(outcome.result),
                        draft = MealDraft.from(outcome.result),
                        isEditing = false,
                        isEdited = false,
                    )
                    is MealScanOutcome.Failure -> state.copy(analysis = MealAnalysis.Failed(outcome.reason))
                }
            }
        }
    }

    fun startEditing() = mutableState.update { it.copy(isEditing = true, draft = it.draft ?: MealDraft()) }

    fun finishEditing() = mutableState.update { it.copy(isEditing = false) }

    fun updateDraft(transform: (MealDraft) -> MealDraft) = mutableState.update { state ->
        state.copy(draft = transform(state.draft ?: MealDraft()), isEdited = true)
    }

    /** Debug-only: fills the Stitch design's sample result so the layout can be reviewed offline. */
    fun useSampleResult() {
        if (!BuildConfig.DEBUG) return
        val sample = MealScanResult(
            schemaVersion = 1,
            model = "debug-sample",
            mealName = "Grilled chicken salad bowl",
            portionDescription = "Mixed greens, grilled chicken, avocado, vinaigrette",
            confidencePercent = 96,
            nutrition = NutritionEstimate(calories = 420, proteinGrams = 34.0, fiberGrams = 6.0, carbsGrams = 18.0),
            glp1Tip = "High protein, gentle on digestion — a solid pick while on your GLP-1.",
        )
        mutableState.update {
            it.copy(analysis = MealAnalysis.Detected(sample), draft = MealDraft.from(sample), isEditing = false, isEdited = false)
        }
    }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        val draft = current.draft ?: return
        val nutrition = draft.toNutrition() ?: return
        if (!current.canSave) return
        val detected = (current.analysis as? MealAnalysis.Detected)?.result
        mutableState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            trackingRepository.saveMeal(
                MealLog(
                    id = idempotencyKey,
                    userId = appDataStore.preferences.first().userId,
                    epochDay = localEpochDay(now),
                    loggedAtEpochMillis = now,
                    name = draft.name.trim(),
                    portionDescription = draft.portion.trim(),
                    nutrition = nutrition,
                    confidencePercent = current.confidencePercent,
                    imageUri = null,
                    analysisSchemaVersion = detected?.schemaVersion,
                    analysisModel = detected?.model,
                ),
            )
            onSaved()
        }
    }

    override fun onCleared() {
        // The photo was only needed for analysis; never keep meal pictures around on disk.
        photoEncoder.discardIfOwned(photoUri.toUri())
    }
}
