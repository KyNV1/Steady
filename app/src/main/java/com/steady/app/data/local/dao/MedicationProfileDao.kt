package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.steady.app.data.local.entity.MedicationProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationProfileDao {
    @Query("SELECT * FROM medication_profiles WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<MedicationProfileEntity?>

    @Upsert
    suspend fun upsert(entity: MedicationProfileEntity)
}
