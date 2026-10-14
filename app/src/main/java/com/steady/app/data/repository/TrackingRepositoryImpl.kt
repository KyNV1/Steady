package com.steady.app.data.repository

import androidx.room.withTransaction
import com.steady.app.core.storage.AppDatabase
import com.steady.app.data.local.dao.DailyGoalsDao
import com.steady.app.data.local.dao.MealLogDao
import com.steady.app.data.local.entity.MealLogEntity
import com.steady.app.domain.model.NutritionEstimate
import kotlin.math.roundToInt
import com.steady.app.data.local.dao.DailyIntakeDao
import com.steady.app.data.local.dao.DoseLogDao
import com.steady.app.data.local.dao.MedicationProfileDao
import com.steady.app.data.local.dao.SideEffectLogDao
import com.steady.app.data.local.entity.DailyGoalsEntity
import com.steady.app.data.local.entity.DoseLogEntity
import com.steady.app.data.local.entity.MedicationProfileEntity
import com.steady.app.data.local.entity.SideEffectLogEntity
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.DailyIntake
import com.steady.app.domain.model.DailyNutritionTotals
import com.steady.app.domain.model.DoseLog
import com.steady.app.domain.model.InjectionSite
import com.steady.app.domain.model.MealLog
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.model.ProgressEntry
import com.steady.app.domain.model.SideEffectLog
import com.steady.app.domain.model.SideEffectSeverity
import com.steady.app.domain.repository.TrackingRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Medication profile, daily goals, quick-add daily intake, meal, dose and side-effect logs are wired
 * to Room. Daily nutrition totals and progress aren't built yet — nothing calls those methods,
 * so they stay [TODO] rather than a half-working fake.
 */
@Singleton
class TrackingRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val mealLogDao: MealLogDao,
    private val medicationProfileDao: MedicationProfileDao,
    private val dailyGoalsDao: DailyGoalsDao,
    private val dailyIntakeDao: DailyIntakeDao,
    private val sideEffectLogDao: SideEffectLogDao,
    private val doseLogDao: DoseLogDao,
) : TrackingRepository {

    override fun observeDailyIntake(userId: String, epochDay: Long): Flow<DailyIntake> =
        dailyIntakeDao.observe(userId, epochDay).map { entity ->
            entity?.let { DailyIntake(it.epochDay, it.fiberGrams, it.proteinGrams, it.waterMilliliters) }
                ?: DailyIntake(epochDay)
        }

    override fun observeDailyIntakeRange(userId: String, fromEpochDay: Long, toEpochDay: Long): Flow<List<DailyIntake>> =
        dailyIntakeDao.observeRange(userId, fromEpochDay, toEpochDay).map { rows ->
            rows.map { DailyIntake(it.epochDay, it.fiberGrams, it.proteinGrams, it.waterMilliliters) }
        }

    override fun observeIntakeDays(userId: String): Flow<Set<Long>> =
        dailyIntakeDao.observeActiveDays(userId).map { it.toSet() }

    override suspend fun adjustDailyIntake(
        userId: String,
        epochDay: Long,
        fiberDelta: Int,
        proteinDelta: Int,
        waterDeltaMl: Int,
    ) = dailyIntakeDao.adjust(userId, epochDay, fiberDelta, proteinDelta, waterDeltaMl)

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
        mealLogDao.observeRange(userId, fromEpochMillis, toEpochMillis).map { rows -> rows.map { it.toDomain() } }

    override fun observeDoseLogs(userId: String): Flow<List<DoseLog>> =
        doseLogDao.observeForUser(userId).map { rows -> rows.map { it.toDomain() } }

    override fun observeSideEffects(
        userId: String,
        fromEpochMillis: Long,
        toEpochMillis: Long,
    ): Flow<List<SideEffectLog>> =
        sideEffectLogDao.observeRange(userId, fromEpochMillis, toEpochMillis).map { rows -> rows.map { it.toDomain() } }

    override fun observeProgress(userId: String): Flow<List<ProgressEntry>> =
        TODO("Phase 6 — Journey progress isn't built yet, no caller exists")

    /** Stores the meal and adds its protein/fiber to that day's Home totals in one transaction. */
    override suspend fun saveMeal(meal: MealLog) {
        database.withTransaction {
            val inserted = mealLogDao.insert(meal.toEntity()) != -1L
            if (inserted) {
                dailyIntakeDao.adjust(
                    userId = meal.userId,
                    epochDay = meal.epochDay,
                    fiberDelta = meal.nutrition.fiberGrams.roundToInt(),
                    proteinDelta = meal.nutrition.proteinGrams.roundToInt(),
                    waterDeltaMl = 0,
                )
            }
        }
    }

    override suspend fun saveDose(dose: DoseLog) = doseLogDao.upsert(dose.toEntity())

    override suspend fun saveSideEffect(log: SideEffectLog) = sideEffectLogDao.upsert(log.toEntity())

    override suspend fun saveProgress(entry: ProgressEntry): Unit =
        TODO("Phase 6 — Journey progress isn't built yet, no caller exists")

    override suspend fun clearUserData(userId: String) = database.userDataDao().clearAll(userId)

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

private fun MealLogEntity.toDomain() = MealLog(
    id = id,
    userId = userId,
    epochDay = epochDay,
    loggedAtEpochMillis = loggedAtEpochMillis,
    name = name,
    portionDescription = portionDescription,
    nutrition = NutritionEstimate(calories, proteinGrams, fiberGrams, carbsGrams),
    confidencePercent = confidencePercent,
    imageUri = imageUri,
    analysisSchemaVersion = analysisSchemaVersion,
    analysisModel = analysisModel,
)

private fun MealLog.toEntity() = MealLogEntity(
    id = id,
    userId = userId,
    epochDay = epochDay,
    loggedAtEpochMillis = loggedAtEpochMillis,
    name = name,
    portionDescription = portionDescription,
    calories = nutrition.calories,
    proteinGrams = nutrition.proteinGrams,
    fiberGrams = nutrition.fiberGrams,
    carbsGrams = nutrition.carbsGrams,
    confidencePercent = confidencePercent,
    imageUri = imageUri,
    analysisSchemaVersion = analysisSchemaVersion,
    analysisModel = analysisModel,
)

private fun DoseLogEntity.toDomain() = DoseLog(
    id = id,
    userId = userId,
    medicationName = medicationName,
    doseAmount = doseAmount,
    doseUnit = doseUnit,
    takenAtEpochMillis = takenAtEpochMillis,
    injectionSite = injectionSite?.let { site -> InjectionSite.entries.firstOrNull { it.name == site } },
    note = note,
)

private fun DoseLog.toEntity() = DoseLogEntity(
    id = id,
    userId = userId,
    medicationName = medicationName,
    doseAmount = doseAmount,
    doseUnit = doseUnit,
    takenAtEpochMillis = takenAtEpochMillis,
    injectionSite = injectionSite?.name,
    note = note?.takeIf { it.isNotBlank() },
)

private fun SideEffectLogEntity.toDomain() = SideEffectLog(
    id = id,
    userId = userId,
    epochDay = epochDay,
    recordedAtEpochMillis = recordedAtEpochMillis,
    symptoms = symptoms.split(',').filter { it.isNotBlank() }.toSet(),
    severity = SideEffectSeverity.valueOf(severity),
    note = note,
)

private fun SideEffectLog.toEntity() = SideEffectLogEntity(
    id = id,
    userId = userId,
    epochDay = epochDay,
    recordedAtEpochMillis = recordedAtEpochMillis,
    symptoms = symptoms.sorted().joinToString(","),
    severity = severity.name,
    note = note?.takeIf { it.isNotBlank() },
)
