package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalsEntity(
    @PrimaryKey val userId: String,
    val proteinGrams: Int,
    val fiberGrams: Int,
    val waterMilliliters: Int,
)
