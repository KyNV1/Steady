package com.steady.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.steady.app.core.network.TokenProvider
import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.AppDatabase
import com.steady.app.core.storage.AppPreferences
import com.steady.app.core.storage.AppPreferencesSerializer
import com.steady.app.core.utils.Constants
import com.steady.app.data.local.dao.DailyGoalsDao
import com.steady.app.data.local.dao.MedicationProfileDao
import com.steady.app.data.local.dao.UserDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {
    @Binds
    abstract fun bindTokenProvider(appDataStore: AppDataStore): TokenProvider

    companion object {
        @Provides
        @Singleton
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Provides
        @Singleton
        fun provideAppPreferencesDataStore(
            @ApplicationContext context: Context,
            scope: CoroutineScope,
        ): DataStore<AppPreferences> = DataStoreFactory.create(
            serializer = AppPreferencesSerializer,
            scope = scope,
            produceFile = { context.dataStoreFile(Constants.DATASTORE_FILE_NAME) },
        )

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .addMigrations(*AppDatabase.MIGRATIONS)
                .build()

        @Provides
        fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

        @Provides
        fun provideMedicationProfileDao(database: AppDatabase): MedicationProfileDao =
            database.medicationProfileDao()

        @Provides
        fun provideDailyGoalsDao(database: AppDatabase): DailyGoalsDao = database.dailyGoalsDao()
    }
}
