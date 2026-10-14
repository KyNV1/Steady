package com.steady.app.feature.notifications

import com.steady.app.domain.model.AppNotification
import com.steady.app.domain.model.NotificationType
import java.util.concurrent.TimeUnit

/**
 * Sample inbox mirroring the Stitch "Steady - Notifications" design. Only reachable from the
 * debug-only "Load sample notifications" action; never shipped as real data. Stable ids make
 * reloading idempotent.
 */
internal fun debugSampleNotifications(userId: String, nowMillis: Long): List<AppNotification> {
    fun hoursAgo(h: Long) = nowMillis - TimeUnit.HOURS.toMillis(h)
    fun daysAgo(d: Long) = nowMillis - TimeUnit.DAYS.toMillis(d)
    fun sample(n: Int, type: NotificationType, title: String, body: String, createdAt: Long, read: Boolean) =
        AppNotification(
            id = "debug-sample-$n",
            userId = userId,
            type = type,
            title = title,
            body = body,
            createdAtEpochMillis = createdAt,
            readAtEpochMillis = if (read) nowMillis else null,
        )
    return listOf(
        sample(1, NotificationType.DOSE_REMINDER, "Time for your Tirzepatide dose", "Scheduled for 8:00 AM today", hoursAgo(2), read = false),
        sample(2, NotificationType.MEAL_REMINDER, "You haven't logged lunch yet", "A quick scan takes about 10 seconds", hoursAgo(3), read = false),
        sample(3, NotificationType.COMMUNITY_LIKE, "Maria K. liked your comment", "“Sautéed chicken skewers with cucumber dill yogurt have been...”", hoursAgo(5), read = true),
        sample(4, NotificationType.COMMUNITY_REPLY, "New reply from David L.", "“Remember to drink your electrolytes, especially on injection...”", daysAgo(2), read = true),
        sample(5, NotificationType.STREAK, "12-day streak achieved!", "You're consistently hitting your daily protein and hydration goals.", daysAgo(3), read = true),
        sample(6, NotificationType.WEEKLY_INSIGHT, "Weekly report is ready", "You were 20% steadier this week with 6 of 7 days logged.", daysAgo(4), read = true),
    )
}
