package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.SteadyShapes

/** 2-3 segment single-select toggle; selected segment is solid ink, others hairline-bordered. */
@Composable
fun <T> PillToggleGroup(options: List<T>, selected: T, onSelect: (T) -> Unit, label: @Composable (T) -> String) {
    Row(horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.small)) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                shape = SteadyShapes.chip,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.clickable { onSelect(option) },
            ) {
                Text(
                    label(option),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = LocalAppSpacing.current.medium, vertical = LocalAppSpacing.current.small),
                )
            }
        }
    }
}

/** Multi-select pill chips (e.g. symptom picker); each chip toggles independently. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipSelector(options: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.small),
        verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.small),
    ) {
        options.forEach { option ->
            val isSelected = option in selected
            Surface(
                shape = SteadyShapes.chip,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.clickable { onToggle(option) },
            ) {
                Text(
                    option,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = LocalAppSpacing.current.medium, vertical = LocalAppSpacing.current.small),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PillToggleGroupPreview() {
    AppTheme { PillToggleGroup(listOf("kg", "lbs"), "kg", {}, { it }) }
}

@Preview
@Composable
private fun ChipSelectorPreview() {
    AppTheme { ChipSelector(listOf("Nausea", "Fatigue", "Headache"), setOf("Nausea"), {}) }
}
