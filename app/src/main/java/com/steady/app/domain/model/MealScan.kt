package com.steady.app.domain.model

const val MEAL_ANALYSIS_SCHEMA_VERSION = 1

data class MealScanRequest(
    val imageBytes: ByteArray,
    val mimeType: String,
    val idempotencyKey: String,
)

data class MealScanResult(
    val schemaVersion: Int,
    val model: String,
    val mealName: String,
    val portionDescription: String,
    val confidencePercent: Int,
    val nutrition: NutritionEstimate,
    val glp1Tip: String?,
)

sealed interface MealScanFailure {
    data object NotAuthenticated : MealScanFailure
    data object QuotaExceeded : MealScanFailure
    data object InvalidImage : MealScanFailure
    data object NoFoodDetected : MealScanFailure
    data object InvalidResponse : MealScanFailure
    data object NetworkUnavailable : MealScanFailure
    data object ServiceUnavailable : MealScanFailure
}
