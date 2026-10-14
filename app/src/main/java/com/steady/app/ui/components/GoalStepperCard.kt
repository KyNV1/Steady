package com.steady.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steady.app.ui.theme.AppTheme

/**
 * Editable daily-goal card: tinted icon + label, "− value +" stepper, and a short "why" caption.
 * Steppers disable themselves at the bounds the caller computes.
 */
@Composable
fun GoalStepperCard(
    icon: ImageVector,
    iconTint: Color,
    iconContainer: Color,
    label: String,
    valueText: String,
    caption: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    canDecrement: Boolean = true,
    canIncrement: Boolean = true,
    decrementDescription: String? = null,
    incrementDescription: String? = null,
) {
    SteadyCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(
                    icon = icon,
                    size = 28.dp,
                    shape = RoundedCornerShape(8.dp),
                    containerColor = iconContainer,
                    iconTint = iconTint,
                    iconSize = 17.dp,
                )
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
            ) {
                CircleStepperButton(
                    icon = Icons.Default.Remove,
                    filled = false,
                    onClick = onDecrement,
                    size = 36.dp,
                    enabled = canDecrement,
                    contentDescription = decrementDescription,
                )
                Text(
                    valueText,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
                CircleStepperButton(
                    icon = Icons.Default.Add,
                    filled = true,
                    onClick = onIncrement,
                    size = 36.dp,
                    enabled = canIncrement,
                    contentDescription = incrementDescription,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Text(
                caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Preview
@Composable
private fun GoalStepperCardPreview() {
    AppTheme {
        GoalStepperCard(
            icon = Icons.Default.WaterDrop,
            iconTint = Color(0xFF5B8AA6),
            iconContainer = Color(0xFFE5EFF5),
            label = "Water",
            valueText = "64 oz",
            caption = "Crucial for hydration and managing medication side effects",
            onDecrement = {},
            onIncrement = {},
        )
    }
}
