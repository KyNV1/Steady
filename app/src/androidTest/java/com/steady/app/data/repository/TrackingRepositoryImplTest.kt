package com.steady.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.steady.app.core.storage.AppDatabase
import com.steady.app.domain.model.DailyGoals
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackingRepositoryImplTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: TrackingRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TrackingRepositoryImpl(database.medicationProfileDao(), database.dailyGoalsDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeMedicationProfile_returnsNullBeforeAnyProfileIsSaved() = runTest {
        assertNull(repository.observeMedicationProfile("42").first())
    }

    @Test
    fun saveMedicationProfile_roundTripsThroughRoom() = runTest {
        val profile = MedicationProfile(
            userId = "42",
            route = MedicationRoute.INJECTION,
            name = "Tirzepatide",
            doseAmount = 5.0,
            doseUnit = "mg",
            frequency = MedicationFrequency.WEEKLY,
            updatedAtEpochMillis = 1_000L,
        )

        repository.saveMedicationProfile(profile)

        assertEquals(profile, repository.observeMedicationProfile("42").first())
    }

    @Test
    fun observeDailyGoals_fallsBackToRecommendedDefaultsBeforeAnyGoalsAreSaved() = runTest {
        assertEquals(
            TrackingRepositoryImpl.RECOMMENDED_DEFAULT_GOALS,
            repository.observeDailyGoals("42").first(),
        )
    }

    @Test
    fun saveDailyGoals_roundTripsThroughRoom() = runTest {
        val goals = DailyGoals(proteinGrams = 90, fiberGrams = 30, waterMilliliters = 2000)

        repository.saveDailyGoals("42", goals)

        assertEquals(goals, repository.observeDailyGoals("42").first())
    }
}
