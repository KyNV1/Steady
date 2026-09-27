package com.steady.app.domain.model

data class ProgressEntry(
    val id: String,
    val userId: String,
    val localDate: String,
    val recordedAtEpochMillis: Long,
    val weightKilograms: Double?,
    val privatePhotoPath: String?,
)
