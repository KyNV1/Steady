package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.SteadyShapes

/**
 * Steady's hairline-bordered card shell: a [Surface] with the design system's 1dp outline
 * border. Reused across stat/goal cards, hero panels, and bottom-sheet-style screens — pass
 * [shape]/[color] for variants (hero, chip, circle); the hairline border stays the house style
 * unless explicitly overridden.
 */
@Composable
fun SteadyCard(
    modifier: Modifier = Modifier,
    shape: Shape = SteadyShapes.card,
    color: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = border,
        content = content,
    )
}
