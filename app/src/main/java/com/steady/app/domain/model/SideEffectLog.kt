package com.steady.app.domain.model

enum class SideEffectSeverity { MILD, MODERATE, SEVERE }

/** Symptoms offered on the log screen. [key] is the stable value persisted in [SideEffectLog.symptoms]. */
enum class SideEffectSymptom(val key: String) {
    NAUSEA("nausea"),
    CONSTIPATION("constipation"),
    FATIGUE("fatigue"),
    HEADACHE("headache"),
    BLOATING("bloating"),
    INJECTION_SITE("injection_site"),
    DIZZINESS("dizziness"),
    APPETITE_LOSS("appetite_loss"),
}

data class SideEffectLog(
    val id: String,
    val userId: String,
    /** Local calendar day, same convention as [DailyIntake.epochDay]. */
    val epochDay: Long,
    val recordedAtEpochMillis: Long,
    val symptoms: Set<String>,
    val severity: SideEffectSeverity,
    val note: String?,
)
