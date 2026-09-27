package com.steady.app.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.steady.app.data.local.dao.UserDao
import com.steady.app.data.local.entity.CachedUserEntity

@Database(
    entities = [CachedUserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        val MIGRATIONS: Array<Migration> = emptyArray()
    }
}
