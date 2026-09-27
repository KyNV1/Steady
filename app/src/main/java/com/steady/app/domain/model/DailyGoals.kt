package com.steady.app.domain.model

data class DailyGoals(
    val proteinGrams: Int,
    val fiberGrams: Int,
    val waterMilliliters: Int,
)

data class DailyNutritionTotals(
    val localDate: String,
    val calories: Int,
    val proteinGrams: Double,
    val fiberGrams: Double,
    val carbsGrams: Double,
    val waterMilliliters: Int,
)
