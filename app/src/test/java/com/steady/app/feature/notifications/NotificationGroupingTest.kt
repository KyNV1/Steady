package com.steady.app.feature.notifications

import com.steady.app.domain.model.AppNotification
import com.steady.app.domain.model.NotificationType
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotificationGroupingTest {
    private lateinit var originalZone: TimeZone

    @Before
    fun pinTimeZone() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreTimeZone() = TimeZone.setDefault(originalZone)

    private val now = TimeUnit.DAYS.toMillis(100) + TimeUnit.HOURS.toMillis(12) // day 100, noon UTC

    private fun item(id: String, createdAt: Long) =
        AppNotification(id, "u", NotificationType.SYSTEM, "t", "b", createdAt, readAtEpochMillis = null)

    @Test
    fun `buckets by local day and drops empty groups`() {
        val sections = groupNotifications(
            listOf(
                item("today", now - TimeUnit.HOURS.toMillis(2)),
                item("yesterday", now - TimeUnit.DAYS.toMillis(1)),
                item("sixDays", now - TimeUnit.DAYS.toMillis(6)),
                item("old", now - TimeUnit.DAYS.toMillis(8)),
            ),
            now,
        )
        assertEquals(
            listOf(NotificationGroup.TODAY, NotificationGroup.EARLIER_THIS_WEEK, NotificationGroup.OLDER),
            sections.map { it.group },
        )
        assertEquals(listOf("yesterday", "sixDays"), sections[1].items.map { it.id })
    }

    @Test
    fun `just after midnight counts as today, just before counts as earlier`() {
        val midnight = TimeUnit.DAYS.toMillis(100)
        val sections = groupNotifications(listOf(item("a", midnight + 1), item("b", midnight - 1)), now)
        assertEquals(listOf("a"), sections.single { it.group == NotificationGroup.TODAY }.items.map { it.id })
        assertEquals(listOf("b"), sections.single { it.group == NotificationGroup.EARLIER_THIS_WEEK }.items.map { it.id })
    }

    @Test
    fun `unknown push type falls back to system`() {
        assertEquals(NotificationType.SYSTEM, NotificationType.fromKey("something_new"))
        assertEquals(NotificationType.DOSE_REMINDER, NotificationType.fromKey("dose_reminder"))
    }
}
