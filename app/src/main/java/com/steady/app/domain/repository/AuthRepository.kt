package com.steady.app.domain.repository

import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>

    /** The signed-in user's cached profile, or null when signed out. */
    val currentUser: Flow<User?>

    suspend fun login(googleIdToken: String): Result<AuthSession>
    suspend fun refreshToken(): Result<String>
    suspend fun logout()
}
