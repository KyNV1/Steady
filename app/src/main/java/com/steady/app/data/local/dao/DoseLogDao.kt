package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.steady.app.data.local.entity.DoseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {
    @Query("SELECT * FROM dose_logs WHERE userId = :userId ORDER BY takenAtEpochMillis DESC")
    fun observeForUser(userId: String): Flow<List<DoseLogEntity>>

    @Upsert
    suspend fun upsert(entity: DoseLogEntity)
}
