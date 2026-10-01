package com.steady.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Round +/- stepper button shared by [GoalStepperCard] and [StatProgressCard]: filled (dark) for
 * increment, outlined (surfaceContainer) for decrement. */
@Composable
fun CircleStepperButton(icon: ImageVector, filled: Boolean, onClick: () -> Unit, size: Dp = 32.dp) {
    Surface(
        shape = CircleShape,
        color = if (filled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.size(size),
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
