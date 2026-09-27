package com.steady.app.domain.usecase

import com.steady.app.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<String> = repository.refreshToken()
}
