package com.steady.app.domain.model

enum class MedicationRoute { INJECTION, ORAL }

enum class MedicationFrequency { DAILY, WEEKLY }

data class MedicationProfile(
    val userId: String,
    val route: MedicationRoute,
    val name: String,
    val doseAmount: Double,
    val doseUnit: String,
    val frequency: MedicationFrequency,
    val updatedAtEpochMillis: Long,
)
