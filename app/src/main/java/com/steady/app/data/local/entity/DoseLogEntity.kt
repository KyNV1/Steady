package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dose_logs", indices = [Index(value = ["userId", "takenAtEpochMillis"])])
data class DoseLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val medicationName: String,
    val doseAmount: Double,
    val doseUnit: String,
    val takenAtEpochMillis: Long,
    val injectionSite: String?,
    val note: String?,
)
