package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.SteadyShapes

/** "Stat/progress card": icon, big current/goal number, label, thin progress track, +/- stepper. */
@Composable
fun StatProgressCard(
    icon: ImageVector,
    label: String,
    currentValue: String,
    goalSuffix: String?,
    progress: Float,
    modifier: Modifier = Modifier,
    onDecrement: (() -> Unit)? = null,
    onIncrement: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = SteadyShapes.card,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(Modifier.padding(LocalAppSpacing.current.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = LocalAppSpacing.current.small),
                )
            }
            Spacer(Modifier.height(LocalAppSpacing.current.small))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(currentValue, style = MaterialTheme.typography.displayLarge)
                if (goalSuffix != null) {
                    Text(
                        goalSuffix,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = LocalAppSpacing.current.extraSmall),
                    )
                }
            }
            Spacer(Modifier.height(LocalAppSpacing.current.small))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer,
            )
            if (onDecrement != null && onIncrement != null) {
                Spacer(Modifier.height(LocalAppSpacing.current.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    StepperButton(icon = Icons.Default.Remove, filled = false, onClick = onDecrement)
                    Spacer(Modifier.width(LocalAppSpacing.current.small))
                    StepperButton(icon = Icons.Default.Add, filled = true, onClick = onIncrement)
                }
            }
        }
    }
}

@Composable
private fun StepperButton(icon: ImageVector, filled: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = if (filled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.size(32.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (filled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview
@Composable
private fun StatProgressCardPreview() {
    AppTheme {
        StatProgressCard(
            icon = Icons.Default.Add,
            label = "Protein",
            currentValue = "0g",
            goalSuffix = "/ 90g",
            progress = 0f,
            onDecrement = {},
            onIncrement = {},
        )
    }
}
