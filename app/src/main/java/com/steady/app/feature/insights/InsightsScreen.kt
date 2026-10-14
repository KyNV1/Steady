package com.steady.app.feature.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCameraBack
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.DailyIntake
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.PremiumBadge
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val ChartHeight = 132.dp
private val BarWidth = 28.dp
private val MinBarHeight = 10.dp

@Composable
fun InsightsScreen(
    onOpenJourney: () -> Unit,
    onOpenNotifications: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel(),
) {
    val summary by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshDay()
        onPauseOrDispose { }
    }
    InsightsContent(summary = summary, onOpenJourney = onOpenJourney, onOpenNotifications = onOpenNotifications)
}

/** Stateless layout, so it renders in previews without Hilt. */
@Composable
fun InsightsContent(summary: InsightsSummary?, onOpenJourney: () -> Unit, onOpenNotifications: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_12, bottom = Dimens.dp_12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.insights_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            IconTile(
                icon = Icons.Outlined.Notifications,
                size = Dimens.dp_38,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                iconSize = Dimens.dp_20,
                border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                contentDescription = stringResource(R.string.notifications_title),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onOpenNotifications),
            )
        }
        if (summary == null) return@Column

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = Dimens.dp_24, end = Dimens.dp_24, bottom = Dimens.dp_24),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp_16),
        ) {
            ProteinWeekCard(summary)
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_12)) {
                StreakCard(summary.streakDays)
                AverageCard(summary.averageGrams, summary.averageDeltaGrams)
            }
            JourneyTeaser(onOpenJourney)
            GuidanceCard(summary)
        }
    }
}

@Composable
private fun ProteinWeekCard(summary: InsightsSummary) {
    val extended = LocalSteadyExtendedColors.current
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.dp_20)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.insights_protein_week).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = extended.onSurfaceMuted,
                    modifier = Modifier.weight(1f),
                )
                StatusPill(summary.isOnTrack)
            }
            Row(
                modifier = Modifier.padding(top = Dimens.dp_4, bottom = Dimens.dp_20),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    "${summary.todayGrams}g",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = Dimens.sp_28, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    stringResource(R.string.insights_today),
                    style = MaterialTheme.typography.bodySmall,
                    color = extended.onSurfaceMuted,
                    modifier = Modifier
                        .padding(start = Dimens.dp_6, bottom = Dimens.dp_4)
                        .weight(1f),
                )
                Text(
                    stringResource(R.string.insights_target, summary.goalGrams),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Dimens.dp_4),
                )
            }
            ProteinBars(summary)
        }
    }
}

@Composable
private fun StatusPill(onTrack: Boolean) {
    val extended = LocalSteadyExtendedColors.current
    Surface(
        shape = CircleShape,
        color = if (onTrack) extended.surfaceContainerGreen else MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.dp_8, vertical = Dimens.dp_2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(Dimens.dp_6)
                    .background(if (onTrack) MaterialTheme.colorScheme.primary else extended.onSurfaceMuted, CircleShape),
            )
            Spacer(Modifier.width(Dimens.dp_4))
            Text(
                stringResource(if (onTrack) R.string.insights_on_track else R.string.insights_building_up),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
                color = if (onTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Seven pill bars scaled to the larger of the goal and the best day, with a dashed goal line. */
@Composable
private fun ProteinBars(summary: InsightsSummary) {
    val scaleMax = maxOf(summary.goalGrams * 1.15f, summary.week.maxOf { it.grams }.toFloat(), 1f)
    val chipSpace = 22.dp
    val barArea = ChartHeight - chipSpace
    val goalFraction = (summary.goalGrams / scaleMax).coerceIn(0f, 1f)
    val lineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    val locale = Locale.getDefault()
    val weekdayFormat = remember(locale) {
        SimpleDateFormat("EEEEE", locale).apply { timeZone = TimeZone.getTimeZone("UTC") }
    }

    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(ChartHeight),
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val y = size.height - barArea.toPx() * goalFraction
                drawLine(
                    lineColor,
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                summary.week.forEach { day ->
                    val barHeight = maxOf(barArea * (day.grams / scaleMax), MinBarHeight)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (day.isToday) {
                            Text(
                                "${day.grams}g",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = Dimens.sp_10, letterSpacing = 0.sp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.dp_6))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = Dimens.dp_6, vertical = Dimens.dp_2),
                            )
                            Spacer(Modifier.height(Dimens.dp_4))
                        }
                        Box(
                            Modifier
                                .width(BarWidth)
                                .height(barHeight)
                                .clip(CircleShape)
                                .background(
                                    if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                ),
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.dp_8),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            summary.week.forEach { day ->
                Text(
                    weekdayFormat.format(Date(day.epochDay * 86_400_000L)),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium),
                    color = if (day.isToday) MaterialTheme.colorScheme.onSurface else LocalSteadyExtendedColors.current.onSurfaceMuted,
                    modifier = Modifier.width(BarWidth),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    tileColor: androidx.compose.ui.graphics.Color,
    label: String,
    content: @Composable () -> Unit,
) {
    SteadyCard(modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(Dimens.dp_16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(
                    icon = icon,
                    size = Dimens.dp_36,
                    shape = RoundedCornerShape(Dimens.dp_10),
                    containerColor = tileColor,
                    iconTint = iconTint,
                    iconSize = Dimens.dp_20,
                )
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(Dimens.dp_12))
            content()
        }
    }
}

@Composable
private fun RowScope.StreakCard(streakDays: Int) {
    StatCard(
        icon = Icons.Filled.LocalFireDepartment,
        iconTint = MaterialTheme.colorScheme.secondary,
        tileColor = LocalSteadyExtendedColors.current.surfaceContainerOrange,
        label = stringResource(R.string.insights_streak_label),
    ) {
        Text(
            pluralStringResource(R.plurals.insights_streak_days, streakDays, streakDays),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            stringResource(if (streakDays > 0) R.string.insights_streak_caption else R.string.insights_streak_caption_empty),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.dp_2),
        )
    }
}

@Composable
private fun RowScope.AverageCard(averageGrams: Int?, deltaGrams: Int?) {
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    StatCard(
        icon = Icons.Outlined.EggAlt,
        iconTint = MaterialTheme.colorScheme.primary,
        tileColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
        label = stringResource(R.string.insights_average_label),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                averageGrams?.let { "${it}g" } ?: "—",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (averageGrams != null) {
                Text(
                    stringResource(R.string.insights_per_day),
                    style = MaterialTheme.typography.bodySmall,
                    color = muted,
                    modifier = Modifier.padding(bottom = Dimens.dp_2),
                )
            }
        }
        when {
            deltaGrams != null -> Row(
                modifier = Modifier.padding(top = Dimens.dp_2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val up = deltaGrams >= 0
                Icon(
                    if (up) Icons.AutoMirrored.Outlined.TrendingUp else Icons.AutoMirrored.Outlined.TrendingDown,
                    contentDescription = null,
                    tint = if (up) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(Dimens.dp_2))
                Text(
                    stringResource(R.string.insights_vs_last_week, if (up) "+${deltaGrams}" else deltaGrams.toString()),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11, fontWeight = FontWeight.Medium),
                    color = if (up) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> Text(
                stringResource(if (averageGrams == null) R.string.insights_average_empty else R.string.insights_average_first_week),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.dp_2),
            )
        }
    }
}

@Composable
private fun JourneyTeaser(onClick: () -> Unit) {
    SteadyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp_16))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.dp_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconTile(
                icon = Icons.Outlined.PhotoCameraBack,
                size = Dimens.dp_44,
                shape = RoundedCornerShape(Dimens.dp_12),
                containerColor = LocalSteadyExtendedColors.current.surfaceContainerOrangeAlt,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconSize = Dimens.dp_22,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.dp_14),
            ) {
                Text(
                    stringResource(R.string.insights_journey_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    stringResource(R.string.insights_journey_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            PremiumBadge()
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LocalSteadyExtendedColors.current.onSurfaceMuted,
                modifier = Modifier.padding(start = Dimens.dp_4),
            )
        }
    }
}

@Composable
private fun GuidanceCard(summary: InsightsSummary) {
    val body = if (summary.gramsToGoal > 0) {
        stringResource(R.string.insights_guidance_remaining, summary.gramsToGoal)
    } else {
        stringResource(R.string.insights_guidance_met)
    }
    Surface(
        shape = RoundedCornerShape(Dimens.dp_16),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(Dimens.dp_16), verticalAlignment = Alignment.Top) {
            IconTile(
                icon = Icons.Outlined.Spa,
                size = Dimens.dp_32,
                containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = Dimens.dp_18,
            )
            Column(Modifier.padding(start = Dimens.dp_14)) {
                Text(
                    stringResource(R.string.insights_guidance_title),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    body,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.dp_2),
                )
            }
        }
    }
}

@Preview(name = "Insights", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun InsightsScreenPreview() {
    val today = 20_000L
    val grams = listOf(58, 74, 50, 62, 70, 82, 72)
    val intakes = grams.mapIndexed { i, g -> DailyIntake(epochDay = today - 6 + i, proteinGrams = g) } +
        (0 until 7).map { DailyIntake(epochDay = today - 13 + it, proteinGrams = 56) }
    AppTheme(darkTheme = false) {
        InsightsContent(
            summary = summarizeProtein(intakes, today, goalGrams = 80, activeDays = intakes.map { it.epochDay }.toSet()),
            onOpenJourney = {},
            onOpenNotifications = {},
        )
    }
}

@Preview(name = "Insights - new user", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun InsightsEmptyPreview() {
    AppTheme(darkTheme = false) {
        InsightsContent(
            summary = summarizeProtein(emptyList(), today = 20_000L, goalGrams = 70, activeDays = emptySet()),
            onOpenJourney = {},
            onOpenNotifications = {},
        )
    }
}
