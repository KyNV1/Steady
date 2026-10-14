package com.steady.app.domain.model

data class NutritionEstimate(
    val calories: Int,
    val proteinGrams: Double,
    val fiberGrams: Double,
    val carbsGrams: Double,
)

data class MealLog(
    val id: String,
    val userId: String,
    /** Local calendar day, same convention as [DailyIntake.epochDay]. */
    val epochDay: Long,
    val loggedAtEpochMillis: Long,
    val name: String,
    val portionDescription: String,
    val nutrition: NutritionEstimate,
    /** Null when the values were entered by hand rather than estimated by AI. */
    val confidencePercent: Int?,
    val imageUri: String?,
    val analysisSchemaVersion: Int?,
    val analysisModel: String?,
)
