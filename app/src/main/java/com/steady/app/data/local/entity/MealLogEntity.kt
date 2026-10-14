package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs", indices = [Index(value = ["userId", "loggedAtEpochMillis"])])
data class MealLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val epochDay: Long,
    val loggedAtEpochMillis: Long,
    val name: String,
    val portionDescription: String,
    val calories: Int,
    val proteinGrams: Double,
    val fiberGrams: Double,
    val carbsGrams: Double,
    val confidencePercent: Int?,
    val imageUri: String?,
    val analysisSchemaVersion: Int?,
    val analysisModel: String?,
)
