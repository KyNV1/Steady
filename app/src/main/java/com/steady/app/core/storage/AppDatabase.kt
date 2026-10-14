package com.steady.app.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.steady.app.data.local.dao.DailyGoalsDao
import com.steady.app.data.local.dao.DailyIntakeDao
import com.steady.app.data.local.dao.DoseLogDao
import com.steady.app.data.local.dao.MealLogDao
import com.steady.app.data.local.dao.MedicationProfileDao
import com.steady.app.data.local.dao.NotificationDao
import com.steady.app.data.local.dao.SideEffectLogDao
import com.steady.app.data.local.dao.UserDao
import com.steady.app.data.local.dao.UserDataDao
import com.steady.app.data.local.entity.CachedUserEntity
import com.steady.app.data.local.entity.DailyGoalsEntity
import com.steady.app.data.local.entity.DailyIntakeEntity
import com.steady.app.data.local.entity.DoseLogEntity
import com.steady.app.data.local.entity.MealLogEntity
import com.steady.app.data.local.entity.MedicationProfileEntity
import com.steady.app.data.local.entity.NotificationEntity
import com.steady.app.data.local.entity.SideEffectLogEntity

@Database(
    entities = [
        CachedUserEntity::class,
        MedicationProfileEntity::class,
        DailyGoalsEntity::class,
        DailyIntakeEntity::class,
        NotificationEntity::class,
        SideEffectLogEntity::class,
        DoseLogEntity::class,
        MealLogEntity::class,
    ],
    version = 7,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicationProfileDao(): MedicationProfileDao
    abstract fun dailyGoalsDao(): DailyGoalsDao
    abstract fun dailyIntakeDao(): DailyIntakeDao
    abstract fun notificationDao(): NotificationDao
    abstract fun sideEffectLogDao(): SideEffectLogDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `medication_profiles` (" +
                        "`userId` TEXT NOT NULL, `route` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                        "`doseAmount` REAL NOT NULL, `doseUnit` TEXT NOT NULL, `frequency` TEXT NOT NULL, " +
                        "`updatedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`userId`))",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_goals` (" +
                        "`userId` TEXT NOT NULL, `proteinGrams` INTEGER NOT NULL, " +
                        "`fiberGrams` INTEGER NOT NULL, `waterMilliliters` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`userId`))",
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_intake` (" +
                        "`userId` TEXT NOT NULL, `epochDay` INTEGER NOT NULL, `fiberGrams` INTEGER NOT NULL, " +
                        "`proteinGrams` INTEGER NOT NULL, `waterMilliliters` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`userId`, `epochDay`))",
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `notifications` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, " +
                        "`body` TEXT NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `readAtEpochMillis` INTEGER, " +
                        "PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_notifications_userId_createdAtEpochMillis` " +
                        "ON `notifications` (`userId`, `createdAtEpochMillis`)",
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `side_effect_logs` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `epochDay` INTEGER NOT NULL, " +
                        "`recordedAtEpochMillis` INTEGER NOT NULL, `symptoms` TEXT NOT NULL, `severity` TEXT NOT NULL, " +
                        "`note` TEXT, PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_side_effect_logs_userId_recordedAtEpochMillis` " +
                        "ON `side_effect_logs` (`userId`, `recordedAtEpochMillis`)",
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `dose_logs` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `medicationName` TEXT NOT NULL, " +
                        "`doseAmount` REAL NOT NULL, `doseUnit` TEXT NOT NULL, `takenAtEpochMillis` INTEGER NOT NULL, " +
                        "`injectionSite` TEXT, `note` TEXT, PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_dose_logs_userId_takenAtEpochMillis` " +
                        "ON `dose_logs` (`userId`, `takenAtEpochMillis`)",
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `meal_logs` (" +
                        "`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `epochDay` INTEGER NOT NULL, " +
                        "`loggedAtEpochMillis` INTEGER NOT NULL, `name` TEXT NOT NULL, `portionDescription` TEXT NOT NULL, " +
                        "`calories` INTEGER NOT NULL, `proteinGrams` REAL NOT NULL, `fiberGrams` REAL NOT NULL, " +
                        "`carbsGrams` REAL NOT NULL, `confidencePercent` INTEGER, `imageUri` TEXT, " +
                        "`analysisSchemaVersion` INTEGER, `analysisModel` TEXT, PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_meal_logs_userId_loggedAtEpochMillis` " +
                        "ON `meal_logs` (`userId`, `loggedAtEpochMillis`)",
                )
            }
        }

        val MIGRATIONS: Array<Migration> =
            arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
    }
}
