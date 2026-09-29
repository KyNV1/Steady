package com.steady.app.core.di

import com.steady.app.data.repository.TrackingRepositoryImpl
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
}
