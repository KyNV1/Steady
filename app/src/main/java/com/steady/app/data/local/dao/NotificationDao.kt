package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.steady.app.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAtEpochMillis DESC")
    fun observeForUser(userId: String): Flow<List<NotificationEntity>>

    @Upsert
    suspend fun upsertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET readAtEpochMillis = :readAt WHERE id = :id AND readAtEpochMillis IS NULL")
    suspend fun markRead(id: String, readAt: Long)

    @Query("UPDATE notifications SET readAtEpochMillis = :readAt WHERE userId = :userId AND readAtEpochMillis IS NULL")
    suspend fun markAllRead(userId: String, readAt: Long)
}
