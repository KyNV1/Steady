package com.steady.app.domain.model

enum class NotificationType { DOSE_REMINDER, COMMUNITY, WEEKLY_INSIGHT, SYSTEM }

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val readAtEpochMillis: Long?,
)
