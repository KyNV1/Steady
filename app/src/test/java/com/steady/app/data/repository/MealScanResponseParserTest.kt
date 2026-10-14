package com.steady.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MealScanResponseParserTest {
    private fun response(
        schema: Any? = 1,
        name: Any? = "Grilled chicken salad bowl",
        calories: Any? = 420,
        protein: Any? = 34.0,
        confidence: Any? = 96,
    ) = mapOf(
        "schemaVersion" to schema,
        "model" to "gemini-x",
        "mealName" to name,
        "portionDescription" to "Mixed greens",
        "confidencePercent" to confidence,
        "nutrition" to mapOf("calories" to calories, "proteinGrams" to protein, "fiberGrams" to 6, "carbsGrams" to 18.5),
        "glp1Tip" to "  ",
    )

    @Test
    fun `valid response maps every field and drops a blank tip`() {
        val result = parseMealScanResponse(response())!!
        assertEquals("Grilled chicken salad bowl", result.mealName)
        assertEquals(420, result.nutrition.calories)
        assertEquals(6.0, result.nutrition.fiberGrams, 0.0)
        assertEquals(96, result.confidencePercent)
        assertNull(result.glp1Tip)
    }

    @Test
    fun `wrong schema version is rejected`() = assertNull(parseMealScanResponse(response(schema = 2)))

    @Test
    fun `missing or blank name is rejected`() {
        assertNull(parseMealScanResponse(response(name = null)))
        assertNull(parseMealScanResponse(response(name = "   ")))
    }

    @Test
    fun `out of range values are rejected`() {
        assertNull(parseMealScanResponse(response(calories = -1)))
        assertNull(parseMealScanResponse(response(calories = 99_999)))
        assertNull(parseMealScanResponse(response(protein = 900.0)))
        assertNull(parseMealScanResponse(response(confidence = 140)))
    }

    @Test
    fun `non-map payload is rejected`() {
        assertNull(parseMealScanResponse(null))
        assertNull(parseMealScanResponse("not json"))
    }
}
