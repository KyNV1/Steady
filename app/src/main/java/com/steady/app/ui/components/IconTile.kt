package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A tinted, centered-icon container: circular/rounded [Surface] wrapping one [Icon]. Covers the
 * small badge/avatar tiles sprinkled across onboarding, settings rows, and stat cards. Pass
 * [iconSize] to override the icon itself; it defaults to the Icon composable's own default size.
 */
@Composable
fun IconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    shape: Shape = CircleShape,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp? = null,
    border: BorderStroke? = null,
    contentDescription: String? = null,
) {
    Surface(
        shape = shape,
        color = containerColor,
        border = border,
        modifier = modifier.size(size),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = if (iconSize != null) Modifier.size(iconSize) else Modifier,
            )
        }
    }
}
