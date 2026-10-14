package com.steady.app.domain.repository

import com.steady.app.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

/** The signed-in user's in-app notification inbox, newest first. */
interface NotificationRepository {
    fun observeInbox(userId: String): Flow<List<AppNotification>>

    suspend fun add(notifications: List<AppNotification>)
    suspend fun markRead(id: String)
    suspend fun markAllRead(userId: String)
}
