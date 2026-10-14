package com.steady.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "daily_intake", primaryKeys = ["userId", "epochDay"])
data class DailyIntakeEntity(
    val userId: String,
    val epochDay: Long,
    val fiberGrams: Int,
    val proteinGrams: Int,
    val waterMilliliters: Int,
)
