package com.steady.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing

/**
 * Compact 3-across stat tile (tinted icon badge + eyebrow, big value with a lighter unit suffix,
 * thin progress bar) sized for a hero card, e.g. onboarding's dashboard mockup. [StatProgressCard]
 * is the full-width Home equivalent with a +/- stepper; this is the read-only, 1/3-width variant.
 */
@Composable
fun MiniStatCard(
    icon: ImageVector,
    accentColor: Color,
    accentTint: Color,
    progressColor: Color,
    trackColor: Color,
    label: String,
    valueNumber: String,
    valueUnit: String,
    badgeText: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    SteadyCard(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(
                    icon = icon,
                    size = 24.dp,
                    shape = RoundedCornerShape(6.dp),
                    containerColor = accentTint,
                    iconTint = accentColor,
                    iconSize = 14.dp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        lineHeight = 14.sp,
                    ),
                    color = accentColor,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    valueNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    valueUnit,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 1.dp),
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = progressColor,
                trackColor = trackColor,
            )
        }
    }
}

@Preview
@Composable
private fun MiniStatCardPreview() {
    AppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.small)) {
            MiniStatCard(
                icon = Icons.Default.FitnessCenter,
                accentColor = MaterialTheme.colorScheme.secondary,
                accentTint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                progressColor = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer,
                label = "Protein",
                valueNumber = "68",
                valueUnit = "g",
                badgeText = "+4g",
                progress = 0.7f,
                modifier = Modifier.weight(1f),
            )
            MiniStatCard(
                icon = Icons.Default.WaterDrop,
                accentColor = MaterialTheme.colorScheme.tertiary,
                accentTint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f),
                progressColor = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer,
                label = "Water",
                valueNumber = "1.8",
                valueUnit = "L",
                badgeText = "Goal",
                progress = 0.4f,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
