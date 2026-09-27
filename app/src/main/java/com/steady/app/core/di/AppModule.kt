package com.steady.app.core.di

import com.steady.app.BuildConfig
import com.steady.app.data.repository.AuthBackend
import com.steady.app.data.repository.AuthRepositoryImpl
import com.steady.app.data.repository.FakeAuthRepository
import com.steady.app.data.repository.FirebaseAuthBackend
import com.steady.app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindAuthBackend(impl: FirebaseAuthBackend): AuthBackend

    companion object {
        @Provides
        @Singleton
        fun provideAuthRepository(
            fakeAuthRepository: Provider<FakeAuthRepository>,
            authRepositoryImpl: Provider<AuthRepositoryImpl>,
        ): AuthRepository = if (BuildConfig.USE_FAKE_AUTH) fakeAuthRepository.get() else authRepositoryImpl.get()
    }
}
