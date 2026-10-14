package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.steady.app.data.local.entity.SideEffectLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SideEffectLogDao {
    @Query(
        "SELECT * FROM side_effect_logs WHERE userId = :userId " +
            "AND recordedAtEpochMillis BETWEEN :fromEpochMillis AND :toEpochMillis ORDER BY recordedAtEpochMillis DESC",
    )
    fun observeRange(userId: String, fromEpochMillis: Long, toEpochMillis: Long): Flow<List<SideEffectLogEntity>>

    @Upsert
    suspend fun upsert(entity: SideEffectLogEntity)
}
