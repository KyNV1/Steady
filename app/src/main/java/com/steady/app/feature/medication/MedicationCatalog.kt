package com.steady.app.feature.medication

import com.steady.app.domain.model.MedicationRoute

/** Common GLP-1 medications offered as quick picks; any other name can be typed in. */
internal val commonMedications = listOf("Semaglutide", "Tirzepatide", "Liraglutide", "Dulaglutide")

/**
 * Marketed strengths (mg) per medication and route, shown only as shortcuts in the dose picker.
 * The user always confirms the dose their prescriber gave them; nothing here is a recommendation.
 */
internal fun commonStrengths(name: String, route: MedicationRoute): List<Double> =
    when (name.trim().lowercase()) {
        "semaglutide" -> if (route == MedicationRoute.ORAL) listOf(3.0, 7.0, 14.0) else listOf(0.25, 0.5, 1.0, 1.7, 2.0, 2.4)
        "tirzepatide" -> listOf(2.5, 5.0, 7.5, 10.0, 12.5, 15.0)
        "liraglutide" -> listOf(0.6, 1.2, 1.8, 2.4, 3.0)
        "dulaglutide" -> listOf(0.75, 1.5, 3.0, 4.5)
        else -> emptyList()
    }

private const val MAX_DOSE_AMOUNT = 1_000.0

/** Parses a typed dose, accepting "2,5" from comma-decimal keyboards; null unless 0 < dose ≤ 1000. */
internal fun parseDoseAmount(text: String): Double? =
    text.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 && it <= MAX_DOSE_AMOUNT }
