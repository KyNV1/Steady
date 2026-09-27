package com.steady.app.data.repository

import com.steady.app.core.storage.AppDataStore
import com.steady.app.data.local.dao.UserDao
import com.steady.app.data.local.entity.CachedUserEntity
import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.model.AuthToken
import com.steady.app.domain.model.User
import com.steady.app.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val backend: AuthBackend,
    private val appDataStore: AppDataStore,
    private val userDao: UserDao,
) : AuthRepository {
    private val refreshMutex = Mutex()
    override val isAuthenticated: Flow<Boolean> = appDataStore.isAuthenticated

    override suspend fun login(googleIdToken: String): Result<AuthSession> = runCatching {
        persist(backend.login(googleIdToken))
    }

    override suspend fun refreshToken(): Result<String> = refreshMutex.withLock {
        runCatching { persist(backend.refresh()).token.accessToken }
    }

    override suspend fun logout() {
        backend.logout()
        appDataStore.clearSession()
        userDao.clear()
    }

    private suspend fun persist(remote: BackendAuthSession): AuthSession {
        val user = User(
            id = remote.userId,
            email = remote.email,
            displayName = remote.displayName.ifBlank { remote.email.substringBefore('@') },
        )
        userDao.upsert(
            CachedUserEntity(
                id = user.id,
                email = user.email,
                displayName = user.displayName,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        appDataStore.saveSession(remote.accessToken, remote.refreshToken, remote.userId)
        return AuthSession(user, AuthToken(remote.accessToken, remote.refreshToken))
    }
}
