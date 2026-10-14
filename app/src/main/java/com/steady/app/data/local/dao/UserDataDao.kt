package com.steady.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

/** Wipes every user-scoped table in one transaction, used after the account is deleted server-side. */
@Dao
abstract class UserDataDao {
    @Query("DELETE FROM medication_profiles WHERE userId = :userId")
    protected abstract suspend fun deleteMedicationProfiles(userId: String)

    @Query("DELETE FROM daily_goals WHERE userId = :userId")
    protected abstract suspend fun deleteDailyGoals(userId: String)

    @Query("DELETE FROM daily_intake WHERE userId = :userId")
    protected abstract suspend fun deleteDailyIntake(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    protected abstract suspend fun deleteNotifications(userId: String)

    @Query("DELETE FROM side_effect_logs WHERE userId = :userId")
    protected abstract suspend fun deleteSideEffectLogs(userId: String)

    @Query("DELETE FROM dose_logs WHERE userId = :userId")
    protected abstract suspend fun deleteDoseLogs(userId: String)

    @Query("DELETE FROM meal_logs WHERE userId = :userId")
    protected abstract suspend fun deleteMealLogs(userId: String)

    @Transaction
    open suspend fun clearAll(userId: String) {
        deleteMedicationProfiles(userId)
        deleteDailyGoals(userId)
        deleteDailyIntake(userId)
        deleteNotifications(userId)
        deleteSideEffectLogs(userId)
        deleteDoseLogs(userId)
        deleteMealLogs(userId)
    }
}
