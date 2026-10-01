package com.steady.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
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
import com.steady.app.ui.theme.SteadyShapes

/** Settings/list row: tinted leading icon tile, label, trailing chevron. Used in cards with dividers. */
@Composable
fun ListSettingsRow(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = LocalAppSpacing.current.medium, vertical = LocalAppSpacing.current.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(icon = icon, size = 34.dp, shape = SteadyShapes.iconTile)
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = LocalAppSpacing.current.medium),
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun ListSettingsRowPreview() {
    AppTheme { ListSettingsRow(Icons.Default.Notifications, "Notifications", {}) }
}
