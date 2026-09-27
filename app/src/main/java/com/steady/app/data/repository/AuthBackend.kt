package com.steady.app.data.repository

data class BackendAuthSession(
    val userId: String,
    val email: String,
    val displayName: String,
    val accessToken: String,
    val refreshToken: String = "",
)

interface AuthBackend {
    suspend fun login(googleIdToken: String): BackendAuthSession
    suspend fun refresh(): BackendAuthSession
    fun logout()
}
