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
    val localDate: String,
    val loggedAtEpochMillis: Long,
    val name: String,
    val portionDescription: String,
    val nutrition: NutritionEstimate,
    val confidencePercent: Int?,
    val imageUri: String?,
    val analysisSchemaVersion: Int?,
    val analysisModel: String?,
)
