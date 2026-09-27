package com.steady.app.domain.model

enum class EntitlementStatus { NONE, PENDING, ACTIVE, GRACE_PERIOD, ON_HOLD, EXPIRED }

data class PremiumEntitlement(
    val status: EntitlementStatus,
    val productId: String?,
    val expiresAtEpochMillis: Long?,
    val verifiedAtEpochMillis: Long?,
)
