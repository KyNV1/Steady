package com.steady.app.feature.mealresult

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MealDraftTest {
    private val filled = MealDraft(name = "Oats", calories = "300", protein = "12", fiber = "8,5", carbs = "40")

    @Test
    fun `parses numbers and accepts a comma decimal`() {
        val nutrition = filled.toNutrition()!!
        assertEquals(300, nutrition.calories)
        assertEquals(8.5, nutrition.fiberGrams, 0.0)
    }

    @Test
    fun `blank or negative numbers block saving`() {
        assertNull(filled.copy(protein = "").toNutrition())
        assertNull(filled.copy(calories = "-5").toNutrition())
        assertFalse(MealDetectedUiState(photoUri = "", draft = filled.copy(name = " ")).canSave)
        assertTrue(MealDetectedUiState(photoUri = "", draft = filled).canSave)
    }

    @Test
    fun `grams format without a trailing zero`() {
        assertEquals("34", formatGrams(34.0))
        assertEquals("6.5", formatGrams(6.5))
    }
}
