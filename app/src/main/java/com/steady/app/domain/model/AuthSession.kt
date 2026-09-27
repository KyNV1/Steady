package com.steady.app.domain.model

data class AuthSession(
    val user: User,
    val token: AuthToken,
)
