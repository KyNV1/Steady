package com.steady.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.navigation.BottomTab
import com.steady.app.navigation.label
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors

/** Five-tab bar per Stitch: cream bar with a hairline top border, no selection pill, filled icon when active. */
@Composable
fun SteadyBottomBar(selected: BottomTab, onSelect: (BottomTab) -> Unit) {
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
            BottomTab.entries.forEach { tab ->
                val label = tab.label()
                val isSelected = tab == selected
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelect(tab) },
                    icon = { Icon(if (isSelected) tab.selectedIcon else tab.icon, contentDescription = label) },
                    label = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = MaterialTheme.typography.labelMedium.letterSpacing,
                            ),
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = muted,
                        unselectedTextColor = muted,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun SteadyBottomBarPreview() {
    AppTheme { SteadyBottomBar(selected = BottomTab.HOME, onSelect = {}) }
}
