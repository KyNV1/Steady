package com.steady.app.domain.repository

import com.steady.app.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>

    suspend fun login(googleIdToken: String): Result<AuthSession>
    suspend fun refreshToken(): Result<String>
    suspend fun logout()
}
