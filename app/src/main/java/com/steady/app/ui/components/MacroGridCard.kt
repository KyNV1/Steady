package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

data class MacroStat(val label: String, val value: String)

/**
 * 2-column macro grid: bold value + muted label per Stitch's macro grid card. Plain rows (not
 * LazyVerticalGrid) so this stays safe inside an already-scrollable screen column.
 */
@Composable
fun MacroGridCard(stats: List<MacroStat>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
    ) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
            ) {
                rowStats.forEach { stat ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = SteadyShapes.card,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        Column(Modifier.padding(LocalAppSpacing.current.medium)) {
                            Text(stat.value, style = MaterialTheme.typography.titleLarge)
                            Text(
                                stat.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                if (rowStats.size == 1) {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun MacroGridCardPreview() {
    AppTheme {
        MacroGridCard(
            listOf(
                MacroStat("Calories", "0"),
                MacroStat("Protein", "0g"),
                MacroStat("Fiber", "0g"),
                MacroStat("Carbs", "0g"),
            ),
        )
    }
}
