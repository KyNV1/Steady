package com.steady.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notifications", indices = [Index(value = ["userId", "createdAtEpochMillis"])])
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val readAtEpochMillis: Long?,
)
