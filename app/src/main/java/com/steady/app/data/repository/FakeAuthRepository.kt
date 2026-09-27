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

@Singleton
class FakeAuthRepository @Inject constructor(private val appDataStore: AppDataStore) : AuthRepository {
    override val isAuthenticated: Flow<Boolean> = appDataStore.isAuthenticated

    override suspend fun login(googleIdToken: String): Result<AuthSession> {
        delay(DEMO_DELAY_MILLIS)
        return saveDemoSession("demo@example.com", "Demo User")
    }

    override suspend fun refreshToken(): Result<String> = runCatching {
        val token = "demo-token-refreshed"
        appDataStore.saveSession(token, "demo-refresh-token", "demo-user")
        token
    }

    override suspend fun logout() = appDataStore.clearSession()

    private suspend fun saveDemoSession(email: String, displayName: String): Result<AuthSession> = runCatching {
        val token = AuthToken("demo-token", "demo-refresh-token")
        val user = User("demo-user", email, displayName)
        appDataStore.saveSession(token.accessToken, token.refreshToken, user.id)
        AuthSession(user, token)
    }

    companion object {
        private const val DEMO_DELAY_MILLIS = 350L
    }
}
