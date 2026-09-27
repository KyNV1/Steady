package com.steady.app.domain.repository

import com.steady.app.domain.model.MealScanFailure
import com.steady.app.domain.model.MealScanRequest
import com.steady.app.domain.model.MealScanResult

interface MealScanRepository {
    suspend fun analyze(request: MealScanRequest): MealScanOutcome
}

sealed interface MealScanOutcome {
    data class Success(val result: MealScanResult) : MealScanOutcome
    data class Failure(val reason: MealScanFailure) : MealScanOutcome
}
