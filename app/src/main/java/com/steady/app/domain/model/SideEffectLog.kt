package com.steady.app.domain.model

enum class SideEffectSeverity { MILD, MODERATE, SEVERE }

data class SideEffectLog(
    val id: String,
    val userId: String,
    val localDate: String,
    val recordedAtEpochMillis: Long,
    val symptoms: Set<String>,
    val severity: SideEffectSeverity,
    val note: String?,
)
