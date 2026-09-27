package com.steady.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.LocalSteadyExtendedColors

/** Green-tint tip banner used for GLP-1 guidance copy under scan results and insights. */
@Composable
fun InfoBanner(text: String, modifier: Modifier = Modifier) {
    val extended = LocalSteadyExtendedColors.current
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = extended.surfaceContainerGreen) {
        Row(
            modifier = Modifier.padding(LocalAppSpacing.current.medium),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(Icons.Default.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = LocalAppSpacing.current.small),
            )
        }
    }
}

@Preview
@Composable
private fun InfoBannerPreview() {
    AppTheme { InfoBanner("High in fiber — great alongside a GLP-1 routine.") }
}
