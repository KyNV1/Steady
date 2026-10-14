package com.steady.app.feature.sideeffects

import com.steady.app.domain.model.SideEffectSeverity
import com.steady.app.domain.model.SideEffectSymptom
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogSideEffectUiStateTest {
    @Test
    fun `save needs at least one symptom and a severity`() {
        assertFalse(LogSideEffectUiState().canSave)
        assertFalse(LogSideEffectUiState(selected = setOf(SideEffectSymptom.NAUSEA)).canSave)
        assertFalse(LogSideEffectUiState(severity = SideEffectSeverity.MILD).canSave)
        assertTrue(LogSideEffectUiState(selected = setOf(SideEffectSymptom.NAUSEA), severity = SideEffectSeverity.MILD).canSave)
    }

    @Test
    fun `cannot double-save while a save is in flight`() {
        val ready = LogSideEffectUiState(selected = setOf(SideEffectSymptom.NAUSEA), severity = SideEffectSeverity.MILD)
        assertFalse(ready.copy(isSaving = true).canSave)
    }

    @Test
    fun `only severe shows the care-escalation warning`() {
        assertFalse(LogSideEffectUiState(severity = SideEffectSeverity.MODERATE).showSevereWarning)
        assertTrue(LogSideEffectUiState(severity = SideEffectSeverity.SEVERE).showSevereWarning)
    }
}
