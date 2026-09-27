package com.steady.app.domain.usecase

import com.steady.app.domain.model.AuthSession
import com.steady.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(googleIdToken: String): Result<AuthSession> = repository.login(googleIdToken)
}
