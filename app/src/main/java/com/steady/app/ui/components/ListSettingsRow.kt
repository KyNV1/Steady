package com.steady.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors

/**
 * Settings/list row: rounded leading icon tile, label, optional trailing value, chevron. Meant to be
 * stacked inside a card with dividers. Pass [tileColor]/[iconTint] to accent a row (e.g. medication).
 */
@Composable
fun ListSettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    tileColor: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(
            icon = icon,
            size = 32.dp,
            shape = RoundedCornerShape(10.dp),
            containerColor = tileColor,
            iconTint = iconTint,
            iconSize = 18.dp,
            border = BorderStroke(1.dp, LocalSteadyExtendedColors.current.outlineSubtle),
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
        )
        if (trailingText != null) {
            Text(
                trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LocalSteadyExtendedColors.current.onSurfaceMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview
@Composable
private fun ListSettingsRowPreview() {
    AppTheme { ListSettingsRow(Icons.Outlined.Notifications, "Notifications", {}, trailingText = "On") }
}
