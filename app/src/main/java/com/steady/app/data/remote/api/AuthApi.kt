package com.steady.app.data.remote.api

import com.steady.app.core.network.AuthInterceptor
import com.steady.app.data.remote.dto.AuthResponseDto
import com.steady.app.data.remote.dto.LoginRequestDto
import com.steady.app.data.remote.dto.RefreshTokenRequestDto
import com.steady.app.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("${AuthInterceptor.SKIP_AUTH_HEADER}: true")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @Headers("${AuthInterceptor.SKIP_AUTH_HEADER}: true")
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @Headers("${AuthInterceptor.SKIP_AUTH_HEADER}: true")
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): AuthResponseDto
}
