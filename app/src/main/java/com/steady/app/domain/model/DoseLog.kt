package com.steady.app.domain.model

enum class InjectionSite { ABDOMEN, THIGH, ARM }

data class DoseLog(
    val id: String,
    val userId: String,
    val medicationName: String,
    val doseAmount: Double,
    val doseUnit: String,
    val takenAtEpochMillis: Long,
    val injectionSite: InjectionSite?,
    val note: String?,
)
