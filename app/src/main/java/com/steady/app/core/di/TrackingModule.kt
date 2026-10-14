package com.steady.app.core.di

import com.steady.app.data.repository.AccountRepositoryImpl
import com.steady.app.data.repository.MealScanRepositoryImpl
import com.steady.app.domain.repository.AccountRepository
import com.steady.app.data.repository.NotificationRepositoryImpl
import com.steady.app.domain.repository.MealScanRepository
import com.steady.app.data.repository.TrackingRepositoryImpl
import com.steady.app.domain.repository.NotificationRepository
import com.steady.app.domain.repository.TrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingModule {
    @Binds
    abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository

    @Binds
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindMealScanRepository(impl: MealScanRepositoryImpl): MealScanRepository

    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
