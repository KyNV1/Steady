package com.steady.app.domain.model

enum class NotificationType {
    DOSE_REMINDER,
    MEAL_REMINDER,
    COMMUNITY_LIKE,
    COMMUNITY_REPLY,
    STREAK,
    WEEKLY_INSIGHT,
    SYSTEM,
    ;

    companion object {
        /** Lenient parse for push payloads/DB rows written by older or newer app versions. */
        fun fromKey(key: String?): NotificationType = entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: SYSTEM
    }
}

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val readAtEpochMillis: Long?,
) {
    val isUnread: Boolean get() = readAtEpochMillis == null
}
