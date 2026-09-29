package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_profiles")
data class MedicationProfileEntity(
    @PrimaryKey val userId: String,
    val route: String,
    val name: String,
    val doseAmount: Double,
    val doseUnit: String,
    val frequency: String,
    val updatedAtEpochMillis: Long,
)
