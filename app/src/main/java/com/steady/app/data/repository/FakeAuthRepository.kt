package com.steady.app.data.repository

import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.model.AuthToken
import com.steady.app.domain.model.User
import com.steady.app.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FakeAuthRepository @Inject constructor(private val appDataStore: AppDataStore) : AuthRepository {
    override val isAuthenticated: Flow<Boolean> = appDataStore.isAuthenticated

    override val currentUser: Flow<User?>
        get() = appDataStore.isAuthenticated.map { signedIn -> if (signedIn) DEMO_USER else null }

    override suspend fun login(googleIdToken: String): Result<AuthSession> {
        delay(DEMO_DELAY_MILLIS)
        return saveDemoSession()
    }

    override suspend fun refreshToken(): Result<String> = runCatching {
        val token = "demo-token-refreshed"
        appDataStore.saveSession(token, "demo-refresh-token", "demo-user")
        token
    }

    override suspend fun logout() = appDataStore.clearSession()

    private suspend fun saveDemoSession(): Result<AuthSession> = runCatching {
        val token = AuthToken("demo-token", "demo-refresh-token")
        appDataStore.saveSession(token.accessToken, token.refreshToken, DEMO_USER.id)
        AuthSession(DEMO_USER, token)
    }

    companion object {
        private const val DEMO_DELAY_MILLIS = 350L
        private val DEMO_USER = User("demo-user", "demo@example.com", "Demo User")
    }
}
