package com.steady.app.core.network

interface TokenProvider {
    fun currentAccessToken(): String?
}
