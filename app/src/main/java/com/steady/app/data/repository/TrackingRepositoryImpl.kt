package com.steady.app.data.repository

import com.steady.app.data.local.dao.DailyGoalsDao
import com.steady.app.data.local.dao.MedicationProfileDao
import com.steady.app.data.local.entity.DailyGoalsEntity
import com.steady.app.data.local.entity.MedicationProfileEntity
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.DailyNutritionTotals
import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.MealLog
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.model.ProgressEntry
import com.steady.app.domain.model.SideEffectLog
import com.steady.app.domain.repository.TrackingRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Only medication profile + daily goals are wired to Room (Phase 3 needs these for
 * first-run setup routing). Meal/dose/side-effect/progress persistence is Phase 4/5 scope —
 * nothing calls those methods yet, so they stay [TODO] rather than a half-working fake.
 */
@Singleton
class TrackingRepositoryImpl @Inject constructor(
    private val medicationProfileDao: MedicationProfileDao,
    private val dailyGoalsDao: DailyGoalsDao,
) : TrackingRepository {

    override fun observeMedicationProfile(userId: String): Flow<MedicationProfile?> =
        medicationProfileDao.observeByUserId(userId).map { it?.toDomain() }

    override fun observeDailyGoals(userId: String): Flow<DailyGoals> =
        dailyGoalsDao.observeByUserId(userId).map { it?.toDomain() ?: RECOMMENDED_DEFAULT_GOALS }

    override suspend fun saveMedicationProfile(profile: MedicationProfile) {
        medicationProfileDao.upsert(profile.toEntity())
    }

    override suspend fun saveDailyGoals(userId: String, goals: DailyGoals) {
        dailyGoalsDao.upsert(goals.toEntity(userId))
    }

    override fun observeDailyTotals(userId: String, localDate: String): Flow<DailyNutritionTotals> =
        TODO("Phase 5 — meal logging isn't built yet, no caller exists")

    override fun observeMeals(userId: String, fromEpochMillis: Long, toEpochMillis: Long): Flow<List<MealLog>> =
        TODO("Phase 5 — meal logging isn't built yet, no caller exists")

    override fun observeDoseLogs(userId: String): Flow<List<DoseLog>> =
        TODO("Phase 4 — dose logging isn't built yet, no caller exists")

    override fun observeSideEffects(
        userId: String,
        fromEpochMillis: Long,
        toEpochMillis: Long,
    ): Flow<List<SideEffectLog>> = TODO("Phase 4 — side-effect logging isn't built yet, no caller exists")

    override fun observeProgress(userId: String): Flow<List<ProgressEntry>> =
        TODO("Phase 6 — Journey progress isn't built yet, no caller exists")

    override suspend fun saveMeal(meal: MealLog): Unit =
        TODO("Phase 5 — meal logging isn't built yet, no caller exists")

    override suspend fun saveDose(dose: DoseLog): Unit =
        TODO("Phase 4 — dose logging isn't built yet, no caller exists")

    override suspend fun saveSideEffect(log: SideEffectLog): Unit =
        TODO("Phase 4 — side-effect logging isn't built yet, no caller exists")

    override suspend fun saveProgress(entry: ProgressEntry): Unit =
        TODO("Phase 6 — Journey progress isn't built yet, no caller exists")

    override suspend fun clearUserData(userId: String): Unit =
        TODO("Phase 7 — account deletion orchestration isn't built yet, no caller exists")

    companion object {
        val RECOMMENDED_DEFAULT_GOALS = DailyGoals(proteinGrams = 70, fiberGrams = 25, waterMilliliters = 1893)
    }
}

private fun MedicationProfileEntity.toDomain() = MedicationProfile(
    userId = userId,
    route = MedicationRoute.valueOf(route),
    name = name,
    doseAmount = doseAmount,
    doseUnit = doseUnit,
    frequency = MedicationFrequency.valueOf(frequency),
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun MedicationProfile.toEntity() = MedicationProfileEntity(
    userId = userId,
    route = route.name,
    name = name,
    doseAmount = doseAmount,
    doseUnit = doseUnit,
    frequency = frequency.name,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun DailyGoalsEntity.toDomain() = DailyGoals(
    proteinGrams = proteinGrams,
    fiberGrams = fiberGrams,
    waterMilliliters = waterMilliliters,
)

private fun DailyGoals.toEntity(userId: String) = DailyGoalsEntity(
    userId = userId,
    proteinGrams = proteinGrams,
    fiberGrams = fiberGrams,
    waterMilliliters = waterMilliliters,
)
