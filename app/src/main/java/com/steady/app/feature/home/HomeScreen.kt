package com.steady.app.feature.home

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.FrauncesFamily
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes
import com.steady.app.ui.theme.SteadyTertiary
import com.steady.app.ui.theme.WelcomeDesignColors
import java.util.Date
import java.util.Locale

private val WaterTint = Color(0xFFE6F0F6)
private val TileShape = RoundedCornerShape(12.dp)

@Composable
fun HomeScreen(
    onScanMeal: () -> Unit,
    onLogDose: () -> Unit,
    onSetUpMedication: () -> Unit,
    onLogSideEffect: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenNotifications: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshDay()
        onPauseOrDispose { }
    }
    HomeContent(
        state = state,
        onScanMeal = onScanMeal,
        onLogDose = onLogDose,
        onSetUpMedication = onSetUpMedication,
        onLogSideEffect = onLogSideEffect,
        onOpenInsights = onOpenInsights,
        onOpenNotifications = onOpenNotifications,
        onQuickAdd = { metric, increase ->
            when (metric) {
                QuickAddMetric.FIBER -> viewModel.adjustFiber(increase)
                QuickAddMetric.PROTEIN -> viewModel.adjustProtein(increase)
                QuickAddMetric.WATER -> viewModel.adjustWater(increase)
            }
        },
    )
}

enum class QuickAddMetric { FIBER, PROTEIN, WATER }

/** Stateless Home layout, so it can render in previews and tests without Hilt. */
@Composable
fun HomeContent(
    state: HomeUiState,
    onScanMeal: () -> Unit,
    onLogDose: () -> Unit,
    onSetUpMedication: () -> Unit,
    onLogSideEffect: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenNotifications: () -> Unit,
    onQuickAdd: (QuickAddMetric, increase: Boolean) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        HomeHeader(streakDays = state.streakDays, onOpenNotifications = onOpenNotifications)
        if (state.isLoading) return@Column

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.dp_24)
                .padding(top = Dimens.dp_8, bottom = Dimens.dp_24),
        ) {
            Greeting(state)
            Spacer(Modifier.height(Dimens.dp_20))
            ScanMealCard(onClick = onScanMeal)
            Spacer(Modifier.height(Dimens.dp_24))
            TodaysProgress(state, onQuickAdd)
            Spacer(Modifier.height(Dimens.dp_20))
            MedicationCard(state.medication, onLogDose = onLogDose, onSetUp = onSetUpMedication)
            Spacer(Modifier.height(Dimens.dp_12))
            ActionRowCard(
                icon = Icons.Outlined.Healing,
                iconTint = MaterialTheme.colorScheme.secondary,
                tileColor = LocalSteadyExtendedColors.current.surfaceContainerOrange,
                title = stringResource(R.string.home_side_effects_title),
                subtitle = stringResource(R.string.home_side_effects_subtitle),
                onClick = onLogSideEffect,
                trailing = { PillButton(stringResource(R.string.home_log), dark = false, onClick = onLogSideEffect) },
            )
            Spacer(Modifier.height(Dimens.dp_12))
            ActionRowCard(
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                iconTint = MaterialTheme.colorScheme.primary,
                tileColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                title = stringResource(R.string.home_insights_title),
                subtitle = stringResource(R.string.home_insights_subtitle),
                onClick = onOpenInsights,
                trailing = {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    )
                },
            )
        }
    }
}

@Composable
private fun HomeHeader(streakDays: Int, onOpenNotifications: () -> Unit) {
    val extended = LocalSteadyExtendedColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_16, bottom = Dimens.dp_8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = Dimens.sp_24, fontWeight = FontWeight.SemiBold),
            color = WelcomeDesignColors.Primary,
            modifier = Modifier.weight(1f),
        )
        if (streakDays > 0) {
            val streakLabel = stringResource(R.string.home_streak_cd, streakDays)
            Surface(
                shape = SteadyShapes.chip,
                color = extended.surfaceContainerOrange,
                border = BorderStroke(Dimens.dp_1, extended.surfaceContainerOrangeAlt),
                modifier = Modifier.semantics { contentDescription = streakLabel },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_4),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = extended.premiumText,
                        modifier = Modifier.size(Dimens.dp_16),
                    )
                    Spacer(Modifier.width(Dimens.dp_4))
                    Text(
                        streakDays.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = extended.premiumText,
                    )
                }
            }
            Spacer(Modifier.width(Dimens.dp_10))
        }
        IconTile(
            icon = Icons.Outlined.Notifications,
            size = Dimens.dp_36,
            containerColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            iconSize = Dimens.dp_20,
            border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
            contentDescription = stringResource(R.string.notifications_title),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onOpenNotifications),
        )
    }
}

@Composable
private fun Greeting(state: HomeUiState) {
    val name = state.displayName
    val greeting = when (state.greeting) {
        Greeting.MORNING ->
            if (name != null) stringResource(R.string.home_greeting_morning_named, name) else stringResource(R.string.home_greeting_morning)
        Greeting.AFTERNOON ->
            if (name != null) stringResource(R.string.home_greeting_afternoon_named, name) else stringResource(R.string.home_greeting_afternoon)
        Greeting.EVENING ->
            if (name != null) stringResource(R.string.home_greeting_evening_named, name) else stringResource(R.string.home_greeting_evening)
    }
    Text(
        greeting,
        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        formatToday(state.todayMillis),
        style = MaterialTheme.typography.bodyMedium,
        color = LocalSteadyExtendedColors.current.onSurfaceMuted,
        modifier = Modifier.padding(top = Dimens.dp_4),
    )
}

private fun formatToday(millis: Long): String {
    val locale = Locale.getDefault()
    val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEMMMd")
    return java.text.SimpleDateFormat(pattern, locale).format(Date(millis))
}

@Composable
private fun ScanMealCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = SteadyShapes.card,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.dp_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconTile(
                icon = Icons.Outlined.PhotoCamera,
                size = Dimens.dp_44,
                shape = TileShape,
                containerColor = Color.White.copy(alpha = 0.15f),
                iconTint = Color.White,
                iconSize = Dimens.dp_22,
                border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.2f)),
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = Dimens.dp_14),
            ) {
                Text(
                    stringResource(R.string.home_scan_meal),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 17.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                )
                Text(
                    stringResource(R.string.home_scan_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_12),
                    color = LocalSteadyExtendedColors.current.surfaceContainerGreen.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = Dimens.dp_2),
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
private fun TodaysProgress(state: HomeUiState, onQuickAdd: (QuickAddMetric, Boolean) -> Unit) {
    val extended = LocalSteadyExtendedColors.current
    Text(
        stringResource(R.string.home_todays_progress).uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(Dimens.dp_10))
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
        MetricCard(
            label = stringResource(R.string.macro_fiber),
            icon = Icons.Outlined.Eco,
            iconTint = MaterialTheme.colorScheme.primary,
            tileColor = extended.surfaceContainerGreen,
            progress = state.fiber,
            unit = "g",
            stepLabel = "${FIBER_QUICK_ADD_GRAMS}g",
            onDecrement = { onQuickAdd(QuickAddMetric.FIBER, false) },
            onIncrement = { onQuickAdd(QuickAddMetric.FIBER, true) },
        )
        MetricCard(
            label = stringResource(R.string.macro_protein),
            icon = Icons.Outlined.EggAlt,
            iconTint = extended.premiumText,
            tileColor = extended.surfaceContainerOrange,
            progress = state.protein,
            unit = "g",
            stepLabel = "${PROTEIN_QUICK_ADD_GRAMS}g",
            onDecrement = { onQuickAdd(QuickAddMetric.PROTEIN, false) },
            onIncrement = { onQuickAdd(QuickAddMetric.PROTEIN, true) },
        )
        MetricCard(
            label = stringResource(R.string.daily_goals_water_label),
            icon = Icons.Outlined.WaterDrop,
            iconTint = SteadyTertiary,
            tileColor = WaterTint,
            progress = state.waterOunces,
            unit = "oz",
            stepLabel = "${WATER_QUICK_ADD_OZ}oz",
            onDecrement = { onQuickAdd(QuickAddMetric.WATER, false) },
            onIncrement = { onQuickAdd(QuickAddMetric.WATER, true) },
        )
    }
    Text(
        stringResource(R.string.home_quick_add_hint),
        style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11),
        color = extended.onSurfaceMuted,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.dp_10),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

@Composable
private fun RowScope.MetricCard(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    tileColor: Color,
    progress: MetricProgress,
    unit: String,
    stepLabel: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    SteadyCard(modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(Dimens.dp_12)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(
                    icon = icon,
                    size = Dimens.dp_28,
                    shape = RoundedCornerShape(Dimens.dp_8),
                    containerColor = tileColor,
                    iconTint = iconTint,
                    iconSize = Dimens.dp_16,
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(Dimens.dp_8))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    progress.current.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "/${progress.goal}$unit",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_12),
                    color = muted,
                    maxLines = 1,
                    modifier = Modifier.padding(start = Dimens.dp_2, bottom = Dimens.dp_3),
                )
            }
            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.dp_8)
                    .height(Dimens.dp_6),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = Dimens.dp_12, bottom = Dimens.dp_8),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuickAddButton(
                    icon = Icons.Default.Remove,
                    dark = false,
                    contentDescription = stringResource(R.string.home_decrease_cd, label),
                    onClick = onDecrement,
                )
                Text(
                    stepLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = Dimens.sp_10, fontWeight = FontWeight.Medium),
                    color = muted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                QuickAddButton(
                    icon = Icons.Default.Add,
                    dark = true,
                    contentDescription = stringResource(R.string.home_increase_cd, label),
                    onClick = onIncrement,
                )
            }
        }
    }
}

/** 24dp visual circle per the design, with a slightly larger hit area for fingers. */
@Composable
private fun QuickAddButton(icon: ImageVector, dark: Boolean, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(Dimens.dp_28)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.dp_24)
                .background(
                    if (dark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceContainer,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (dark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.dp_14),
            )
        }
    }
}

@Composable
private fun MedicationCard(medication: MedicationProfile?, onLogDose: () -> Unit, onSetUp: () -> Unit) {
    val title: String
    val subtitle: String
    val action: String
    val onAction: () -> Unit
    if (medication != null) {
        title = stringResource(R.string.home_next_dose_title, medication.name)
        subtitle = stringResource(medication.scheduleLabelRes())
        action = stringResource(R.string.home_log)
        onAction = onLogDose
    } else {
        title = stringResource(R.string.home_med_setup_title)
        subtitle = stringResource(R.string.home_med_setup_subtitle)
        action = stringResource(R.string.home_med_setup_action)
        onAction = onSetUp
    }
    ActionRowCard(
        icon = Icons.Outlined.Vaccines,
        iconTint = MaterialTheme.colorScheme.primary,
        tileColor = MaterialTheme.colorScheme.surface,
        tileBorder = true,
        cardColor = MaterialTheme.colorScheme.surfaceContainer,
        title = title,
        titleWeight = FontWeight.SemiBold,
        subtitle = subtitle,
        onClick = onAction,
        trailing = { PillButton(action, dark = true, onClick = onAction) },
    )
}

private fun MedicationProfile.scheduleLabelRes(): Int = when (frequency) {
    MedicationFrequency.WEEKLY ->
        if (route == MedicationRoute.INJECTION) R.string.home_med_weekly_injection else R.string.home_med_weekly_oral
    MedicationFrequency.DAILY ->
        if (route == MedicationRoute.INJECTION) R.string.home_med_daily_injection else R.string.home_med_daily_oral
}

@Composable
private fun ActionRowCard(
    icon: ImageVector,
    iconTint: Color,
    tileColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.surface,
    tileBorder: Boolean = false,
    titleWeight: FontWeight = FontWeight.Medium,
) {
    SteadyCard(
        color = cardColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(SteadyShapes.card)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.dp_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconTile(
                icon = icon,
                size = Dimens.dp_40,
                shape = TileShape,
                containerColor = tileColor,
                iconTint = iconTint,
                iconSize = Dimens.dp_20,
                border = if (tileBorder) BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline) else null,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.dp_12),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = titleWeight),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_12),
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                )
            }
            trailing()
        }
    }
}

@Composable
private fun PillButton(text: String, dark: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = SteadyShapes.chip,
        color = if (dark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
        border = if (dark) null else BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (dark) FontWeight.SemiBold else FontWeight.Medium),
            color = if (dark) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_6),
        )
    }
}

// Fixed timestamp so the preview's date line doesn't change between renders.
private const val PREVIEW_MILLIS = 1_790_053_200_000L

private val previewActiveState = HomeUiState(
    isLoading = false,
    displayName = "Alex",
    greeting = Greeting.MORNING,
    todayMillis = PREVIEW_MILLIS,
    streakDays = 12,
    fiber = MetricProgress(current = 14, goal = 25),
    protein = MetricProgress(current = 58, goal = 90),
    waterOunces = MetricProgress(current = 38, goal = 64),
    medication = MedicationProfile(
        userId = "preview",
        route = MedicationRoute.INJECTION,
        name = "Tirzepatide",
        doseAmount = 5.0,
        doseUnit = "mg",
        frequency = MedicationFrequency.WEEKLY,
        updatedAtEpochMillis = PREVIEW_MILLIS,
    ),
)

@Composable
private fun HomeContentPreviewHost(state: HomeUiState) {
    AppTheme(darkTheme = false) {
        HomeContent(
            state = state,
            onScanMeal = {},
            onLogDose = {},
            onSetUpMedication = {},
            onLogSideEffect = {},
            onOpenInsights = {},
            onOpenNotifications = {},
            onQuickAdd = { _, _ -> },
        )
    }
}

@Preview(name = "Home - active user", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    HomeContentPreviewHost(previewActiveState)
}

@Preview(name = "Home - new user", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun HomeScreenNewUserPreview() {
    HomeContentPreviewHost(
        previewActiveState.copy(
            displayName = null,
            greeting = Greeting.EVENING,
            streakDays = 0,
            fiber = MetricProgress(current = 0, goal = 25),
            protein = MetricProgress(current = 0, goal = 70),
            waterOunces = MetricProgress(current = 0, goal = 64),
            medication = null,
        ),
    )
}
