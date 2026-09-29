package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.steady.app.data.local.entity.DailyGoalsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalsDao {
    @Query("SELECT * FROM daily_goals WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<DailyGoalsEntity?>

    @Upsert
    suspend fun upsert(entity: DailyGoalsEntity)
}
