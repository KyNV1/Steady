package com.steady.app.data.repository

import com.steady.app.data.local.dao.NotificationDao
import com.steady.app.data.local.entity.NotificationEntity
import com.steady.app.domain.model.AppNotification
import com.steady.app.domain.model.NotificationType
import com.steady.app.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed inbox. Local for now so read state works offline; a Firestore
 * `users/{uid}/notifications` sync can feed [add] once Firebase is configured.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationRepository {
    override fun observeInbox(userId: String): Flow<List<AppNotification>> =
        notificationDao.observeForUser(userId).map { rows -> rows.map { it.toDomain() } }

    override suspend fun add(notifications: List<AppNotification>) =
        notificationDao.upsertAll(notifications.map { it.toEntity() })

    override suspend fun markRead(id: String) = notificationDao.markRead(id, System.currentTimeMillis())

    override suspend fun markAllRead(userId: String) = notificationDao.markAllRead(userId, System.currentTimeMillis())
}

private fun NotificationEntity.toDomain() = AppNotification(
    id = id,
    userId = userId,
    type = NotificationType.fromKey(type),
    title = title,
    body = body,
    createdAtEpochMillis = createdAtEpochMillis,
    readAtEpochMillis = readAtEpochMillis,
)

private fun AppNotification.toEntity() = NotificationEntity(
    id = id,
    userId = userId,
    type = type.name,
    title = title,
    body = body,
    createdAtEpochMillis = createdAtEpochMillis,
    readAtEpochMillis = readAtEpochMillis,
)
