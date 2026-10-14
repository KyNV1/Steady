package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "side_effect_logs", indices = [Index(value = ["userId", "recordedAtEpochMillis"])])
data class SideEffectLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val epochDay: Long,
    val recordedAtEpochMillis: Long,
    /** Comma-separated symptom keys. */
    val symptoms: String,
    val severity: String,
    val note: String?,
)
