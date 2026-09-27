package com.steady.app.domain.repository

import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.DailyNutritionTotals
import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.MealLog
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.ProgressEntry
import com.steady.app.domain.model.SideEffectLog
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    fun observeMedicationProfile(userId: String): Flow<MedicationProfile?>
    fun observeDailyGoals(userId: String): Flow<DailyGoals>
    fun observeDailyTotals(userId: String, localDate: String): Flow<DailyNutritionTotals>
    fun observeMeals(userId: String, fromEpochMillis: Long, toEpochMillis: Long): Flow<List<MealLog>>
    fun observeDoseLogs(userId: String): Flow<List<DoseLog>>
    fun observeSideEffects(userId: String, fromEpochMillis: Long, toEpochMillis: Long): Flow<List<SideEffectLog>>
    fun observeProgress(userId: String): Flow<List<ProgressEntry>>

    suspend fun saveMedicationProfile(profile: MedicationProfile)
    suspend fun saveDailyGoals(userId: String, goals: DailyGoals)
    suspend fun saveMeal(meal: MealLog)
    suspend fun saveDose(dose: DoseLog)
    suspend fun saveSideEffect(log: SideEffectLog)
    suspend fun saveProgress(entry: ProgressEntry)
    suspend fun clearUserData(userId: String)
}
