package com.steady.app.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.steady.app.data.local.dao.DailyGoalsDao
import com.steady.app.data.local.dao.MedicationProfileDao
import com.steady.app.data.local.dao.UserDao
import com.steady.app.data.local.entity.CachedUserEntity
import com.steady.app.data.local.entity.DailyGoalsEntity
import com.steady.app.data.local.entity.MedicationProfileEntity

@Database(
    entities = [CachedUserEntity::class, MedicationProfileEntity::class, DailyGoalsEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicationProfileDao(): MedicationProfileDao
    abstract fun dailyGoalsDao(): DailyGoalsDao

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

        val MIGRATIONS: Array<Migration> = arrayOf(MIGRATION_1_2)
    }
}
