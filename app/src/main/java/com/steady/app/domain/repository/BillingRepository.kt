package com.steady.app.domain.repository

import com.steady.app.domain.model.PremiumEntitlement
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val entitlement: Flow<PremiumEntitlement>

    suspend fun refreshEntitlement(): Result<PremiumEntitlement>
    suspend fun verifyPurchase(productId: String, purchaseToken: String): Result<PremiumEntitlement>
}
