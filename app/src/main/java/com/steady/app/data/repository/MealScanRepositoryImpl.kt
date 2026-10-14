package com.steady.app.data.repository

import android.util.Base64
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.steady.app.BuildConfig
import com.steady.app.domain.model.MEAL_ANALYSIS_SCHEMA_VERSION
import com.steady.app.domain.model.MealScanFailure
import com.steady.app.domain.model.MealScanRequest
import com.steady.app.domain.model.MealScanResult
import com.steady.app.domain.model.NutritionEstimate
import com.steady.app.domain.repository.MealScanOutcome
import com.steady.app.domain.repository.MealScanRepository
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import timber.log.Timber

private const val ANALYZE_MEAL_FUNCTION = "analyzeMeal"
private const val ANALYZE_TIMEOUT_SECONDS = 60L

/**
 * Sends the photo to the protected `analyzeMeal` callable; the Gemini key never lives in the app.
 * Returns ServiceUnavailable without touching Firebase when the project isn't configured.
 */
@Singleton
class MealScanRepositoryImpl @Inject constructor(
    private val firebaseProvider: FirebaseProvider,
) : MealScanRepository {
    override suspend fun analyze(request: MealScanRequest): MealScanOutcome {
        if (!BuildConfig.FIREBASE_CONFIGURED || !firebaseProvider.initialize()) {
            return MealScanOutcome.Failure(MealScanFailure.ServiceUnavailable)
        }
        return try {
            val payload = mapOf(
                "imageBase64" to Base64.encodeToString(request.imageBytes, Base64.NO_WRAP),
                "mimeType" to request.mimeType,
                "idempotencyKey" to request.idempotencyKey,
            )
            val response = FirebaseFunctions.getInstance()
                .getHttpsCallable(ANALYZE_MEAL_FUNCTION)
                .apply { setTimeout(ANALYZE_TIMEOUT_SECONDS, TimeUnit.SECONDS) }
                .call(payload)
                .await()
            parseMealScanResponse(response.getData())
                ?.let { MealScanOutcome.Success(it) }
                ?: MealScanOutcome.Failure(MealScanFailure.InvalidResponse)
        } catch (e: FirebaseFunctionsException) {
            Timber.w("analyzeMeal failed: %s", e.code)
            MealScanOutcome.Failure(e.code.toFailure())
        } catch (e: IOException) {
            MealScanOutcome.Failure(MealScanFailure.NetworkUnavailable)
        }
    }
}

private fun FirebaseFunctionsException.Code.toFailure(): MealScanFailure = when (this) {
    FirebaseFunctionsException.Code.UNAUTHENTICATED,
    FirebaseFunctionsException.Code.PERMISSION_DENIED -> MealScanFailure.NotAuthenticated
    FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> MealScanFailure.QuotaExceeded
    FirebaseFunctionsException.Code.INVALID_ARGUMENT -> MealScanFailure.InvalidImage
    FirebaseFunctionsException.Code.NOT_FOUND -> MealScanFailure.NoFoodDetected
    FirebaseFunctionsException.Code.DATA_LOSS -> MealScanFailure.InvalidResponse
    else -> MealScanFailure.ServiceUnavailable
}

private const val MAX_CALORIES = 5_000
private const val MAX_MACRO_GRAMS = 500.0

/**
 * Validates the callable's JSON against the versioned contract. AI output is untrusted: any
 * missing field, wrong schema version or out-of-range value rejects the whole result.
 */
internal fun parseMealScanResponse(data: Any?): MealScanResult? {
    val root = data as? Map<*, *> ?: return null
    val nutrition = root["nutrition"] as? Map<*, *> ?: return null
    val schema = (root["schemaVersion"] as? Number)?.toInt()
    if (schema != MEAL_ANALYSIS_SCHEMA_VERSION) return null

    val name = (root["mealName"] as? String)?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val calories = (nutrition["calories"] as? Number)?.toInt()?.takeIf { it in 0..MAX_CALORIES } ?: return null
    fun grams(key: String) = (nutrition[key] as? Number)?.toDouble()?.takeIf { it in 0.0..MAX_MACRO_GRAMS }
    val protein = grams("proteinGrams") ?: return null
    val fiber = grams("fiberGrams") ?: return null
    val carbs = grams("carbsGrams") ?: return null
    val confidence = (root["confidencePercent"] as? Number)?.toInt()?.takeIf { it in 0..100 } ?: return null

    return MealScanResult(
        schemaVersion = schema,
        model = root["model"] as? String ?: "unknown",
        mealName = name,
        portionDescription = (root["portionDescription"] as? String)?.trim().orEmpty(),
        confidencePercent = confidence,
        nutrition = NutritionEstimate(calories, protein, fiber, carbs),
        glp1Tip = (root["glp1Tip"] as? String)?.trim()?.takeIf { it.isNotEmpty() },
    )
}
