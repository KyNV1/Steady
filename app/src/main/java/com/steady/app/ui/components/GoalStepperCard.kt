package com.steady.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing

/** Editable daily-goal target card: icon+label, big value, a short "why" caption, +/- stepper. */
@Composable
fun GoalStepperCard(
    icon: ImageVector,
    label: String,
    valueText: String,
    caption: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SteadyCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(LocalAppSpacing.current.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = LocalAppSpacing.current.small),
                )
            }
            Spacer(Modifier.height(LocalAppSpacing.current.small))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(valueText, style = MaterialTheme.typography.displayLarge, modifier = Modifier.weight(1f))
                CircleStepperButton(icon = Icons.Default.Remove, filled = false, onClick = onDecrement, size = 36.dp)
                Spacer(Modifier.width(LocalAppSpacing.current.small))
                CircleStepperButton(icon = Icons.Default.Add, filled = true, onClick = onIncrement, size = 36.dp)
            }
            Spacer(Modifier.height(LocalAppSpacing.current.extraSmall))
            Text(
                caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            label = "Water",
            valueText = "64 oz",
            caption = "Crucial for hydration and managing medication side effects",
            onDecrement = {},
            onIncrement = {},
        )
    }
}
