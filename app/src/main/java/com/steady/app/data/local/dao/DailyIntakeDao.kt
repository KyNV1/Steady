package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.steady.app.data.local.entity.DailyIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DailyIntakeDao {
    @Query("SELECT * FROM daily_intake WHERE userId = :userId AND epochDay = :epochDay LIMIT 1")
    abstract fun observe(userId: String, epochDay: Long): Flow<DailyIntakeEntity?>

    @Query("SELECT * FROM daily_intake WHERE userId = :userId AND epochDay BETWEEN :fromEpochDay AND :toEpochDay")
    abstract fun observeRange(userId: String, fromEpochDay: Long, toEpochDay: Long): Flow<List<DailyIntakeEntity>>

    @Query(
        "SELECT epochDay FROM daily_intake WHERE userId = :userId " +
            "AND (fiberGrams > 0 OR proteinGrams > 0 OR waterMilliliters > 0)",
    )
    abstract fun observeActiveDays(userId: String): Flow<List<Long>>

    @Query("SELECT * FROM daily_intake WHERE userId = :userId AND epochDay = :epochDay LIMIT 1")
    protected abstract suspend fun find(userId: String, epochDay: Long): DailyIntakeEntity?

    @Upsert
    protected abstract suspend fun upsert(entity: DailyIntakeEntity)

    /** Read-modify-write in one transaction so rapid taps can't lose an increment; never goes below 0. */
    @Transaction
    open suspend fun adjust(userId: String, epochDay: Long, fiberDelta: Int, proteinDelta: Int, waterDeltaMl: Int) {
        val current = find(userId, epochDay) ?: DailyIntakeEntity(userId, epochDay, 0, 0, 0)
        upsert(
            current.copy(
                fiberGrams = (current.fiberGrams + fiberDelta).coerceAtLeast(0),
                proteinGrams = (current.proteinGrams + proteinDelta).coerceAtLeast(0),
                waterMilliliters = (current.waterMilliliters + waterDeltaMl).coerceAtLeast(0),
            ),
        )
    }
}
