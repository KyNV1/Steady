package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steady.app.data.local.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query(
        "SELECT * FROM meal_logs WHERE userId = :userId " +
            "AND loggedAtEpochMillis BETWEEN :fromEpochMillis AND :toEpochMillis ORDER BY loggedAtEpochMillis DESC",
    )
    fun observeRange(userId: String, fromEpochMillis: Long, toEpochMillis: Long): Flow<List<MealLogEntity>>

    /** Returns -1 when [entity]'s id already exists, so a repeated save can't double-count totals. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: MealLogEntity): Long
}
