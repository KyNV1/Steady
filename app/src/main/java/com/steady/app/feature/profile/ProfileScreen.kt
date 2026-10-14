package com.steady.app.feature.profile

import android.Manifest
import android.os.Build
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.BuildConfig
import com.steady.app.R
import com.steady.app.core.storage.ThemeMode
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.feature.dose.formatDoseAmount
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.ListSettingsRow
import com.steady.app.ui.components.LoadingDialog
import com.steady.app.ui.components.PillToggleGroup
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    onOpenDailyGoals: () -> Unit,
    onOpenMedicationDose: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenSubscription: () -> Unit,
    onLogDose: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    var showPreferences by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.loggedOut) { if (state.loggedOut) onLoggedOut() }

    ProfileContent(
        state = state,
        actions = ProfileActions(
            onOpenPreferences = { showPreferences = true },
            onEditProfile = onOpenAccount,
            onDoseBanner = {
                if (state.doseSchedule is DoseSchedule.NoMedication) onOpenMedicationDose() else onLogDose()
            },
            onOpenAccount = onOpenAccount,
            onOpenDailyGoals = onOpenDailyGoals,
            onOpenMedicationDose = onOpenMedicationDose,
            onOpenNotifications = onOpenNotifications,
            onOpenSubscription = onOpenSubscription,
            onOpenHelp = onOpenHelp,
            onSignOut = viewModel::signOut,
        ),
    )
    if (showPreferences) {
        PreferencesSheet(themeMode = themeMode, onThemeMode = viewModel::setThemeMode, onDismiss = { showPreferences = false })
    }
    LoadingDialog(state.isLoggingOut)
}

data class ProfileActions(
    val onOpenPreferences: () -> Unit = {},
    val onEditProfile: () -> Unit = {},
    val onDoseBanner: () -> Unit = {},
    val onOpenAccount: () -> Unit = {},
    val onOpenDailyGoals: () -> Unit = {},
    val onOpenMedicationDose: () -> Unit = {},
    val onOpenNotifications: () -> Unit = {},
    val onOpenSubscription: () -> Unit = {},
    val onOpenHelp: () -> Unit = {},
    val onSignOut: () -> Unit = {},
)

/** Stateless Profile layout, so it renders in previews without Hilt. */
@Composable
fun ProfileContent(state: ProfileUiState, actions: ProfileActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            IconTile(
                icon = Icons.Outlined.Tune,
                size = 36.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                iconSize = 20.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentDescription = stringResource(R.string.profile_preferences),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = actions.onOpenPreferences),
            )
        }

        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            IdentityCard(state.displayName, state.medication, actions.onEditProfile)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StreakCard(state.streakDays, Modifier.weight(1f))
                MemberSinceCard(state.memberSinceEpochDay, Modifier.weight(1f))
            }
            DoseBanner(state.doseSchedule, actions.onDoseBanner)
            SettingsSection(state.medication, actions)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(onClick = actions.onSignOut) {
                    Text(
                        stringResource(R.string.logout_action),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Text(
                    stringResource(R.string.profile_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                )
            }
        }
    }
}

@Composable
private fun IdentityCard(displayName: String, medication: MedicationProfile?, onEdit: () -> Unit) {
    val name = displayName.ifBlank { stringResource(R.string.profile_name_fallback) }
    SteadyCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    name.first().uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (medication != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                        )
                        Text(
                            "${medication.name} · ${doseText(medication)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }
            }
            IconTile(
                icon = Icons.Outlined.Edit,
                size = 32.dp,
                containerColor = MaterialTheme.colorScheme.background,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                iconSize = 16.dp,
                contentDescription = stringResource(R.string.profile_edit),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onEdit),
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier, badge: @Composable () -> Unit) {
    SteadyCard(modifier = modifier.height(112.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold, lineHeight = 30.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                badge()
            }
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StreakCard(days: Int, modifier: Modifier) {
    val extended = LocalSteadyExtendedColors.current
    StatCard(days.toString(), stringResource(R.string.profile_streak_label), modifier) {
        if (days > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(extended.surfaceContainerOrange)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    stringResource(R.string.profile_streak_active),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun MemberSinceCard(epochDay: Long?, modifier: Modifier) {
    val value = epochDay?.let { formatMemberSince(it) } ?: "—"
    StatCard(value, stringResource(R.string.profile_member_since), modifier) {
        IconTile(
            icon = Icons.Default.Spa,
            size = 24.dp,
            containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
            iconTint = MaterialTheme.colorScheme.primary,
            iconSize = 14.dp,
        )
    }
}

@Composable
private fun DoseBanner(schedule: DoseSchedule, onClick: () -> Unit) {
    val (title, subtitle) = when (schedule) {
        DoseSchedule.NoMedication ->
            stringResource(R.string.profile_no_medication_title) to stringResource(R.string.profile_no_medication_body)
        DoseSchedule.NoDoseLogged ->
            stringResource(R.string.profile_no_dose_title) to stringResource(R.string.profile_no_dose_body)
        is DoseSchedule.Next -> {
            val title = when {
                schedule.daysUntil < 0 -> stringResource(R.string.profile_dose_overdue, formatWeekday(schedule.dueEpochDay))
                schedule.daysUntil == 0L -> stringResource(R.string.profile_dose_due_today)
                schedule.daysUntil == 1L -> stringResource(R.string.profile_next_dose_tomorrow)
                else -> stringResource(R.string.profile_next_dose_day, formatWeekday(schedule.dueEpochDay))
            }
            title to stringResource(R.string.profile_last_dose, formatShortDate(schedule.lastTakenMillis))
        }
    }
    val extended = LocalSteadyExtendedColors.current
    SteadyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = extended.surfaceContainerGreen,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(
                icon = Icons.Outlined.CalendarMonth,
                size = 32.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = 18.dp,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SettingsSection(medication: MedicationProfile?, actions: ProfileActions) {
    val extended = LocalSteadyExtendedColors.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.profile_settings).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        SteadyCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingsRow(Icons.Outlined.Person, stringResource(R.string.nav_account), actions.onOpenAccount)
                SettingsRow(Icons.Outlined.TrackChanges, stringResource(R.string.nav_daily_goals), actions.onOpenDailyGoals)
                SettingsRow(
                    Icons.Outlined.Vaccines,
                    stringResource(R.string.nav_medication_dose),
                    actions.onOpenMedicationDose,
                    trailingText = medication?.let { doseText(it) },
                    tileColor = extended.surfaceContainerGreen,
                    iconTint = MaterialTheme.colorScheme.primary,
                )
                SettingsRow(Icons.Outlined.Notifications, stringResource(R.string.nav_notifications), actions.onOpenNotifications)
                SettingsRow(
                    Icons.Outlined.WorkspacePremium,
                    stringResource(R.string.profile_subscription),
                    actions.onOpenSubscription,
                    tileColor = extended.surfaceContainerOrange,
                    iconTint = MaterialTheme.colorScheme.secondary,
                )
                SettingsRow(Icons.AutoMirrored.Outlined.HelpOutline, stringResource(R.string.nav_help), actions.onOpenHelp, showDivider = false)
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    trailingText: String? = null,
    tileColor: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    showDivider: Boolean = true,
) {
    ListSettingsRow(icon, label, onClick, trailingText = trailingText, tileColor = tileColor, iconTint = iconTint)
    if (showDivider) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

/** Theme and notification permission live behind the header's "tune" button, as in the design. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencesSheet(themeMode: ThemeMode, onThemeMode: (ThemeMode) -> Unit, onDismiss: () -> Unit) {
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.profile_preferences), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.theme_title), style = MaterialTheme.typography.titleMedium)
            PillToggleGroup(
                options = ThemeMode.entries,
                selected = themeMode,
                onSelect = onThemeMode,
                label = { mode ->
                    when (mode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                        ThemeMode.DARK -> stringResource(R.string.theme_dark)
                    }
                },
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                AppButton(
                    text = stringResource(R.string.enable_notifications),
                    onClick = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                    style = AppButtonStyle.OUTLINED,
                )
            }
        }
    }
}

private fun doseText(medication: MedicationProfile) = "${formatDoseAmount(medication.doseAmount)}${medication.doseUnit}"

/** Epoch days are local calendar days, so format them as UTC midnight to avoid shifting the date. */
private fun formatEpochDay(epochDay: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(TimeUnit.DAYS.toMillis(epochDay)))

private fun formatMemberSince(epochDay: Long) = formatEpochDay(epochDay, "MMM ''yy")

private fun formatWeekday(epochDay: Long) = formatEpochDay(epochDay, "EEEE")

private fun formatShortDate(millis: Long): String =
    SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMd"), Locale.getDefault()).format(Date(millis))

@Preview(name = "Profile", showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
private fun ProfileScreenPreview() {
    val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
    AppTheme(darkTheme = false) {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Alex Nguyen",
                medication = MedicationProfile(
                    userId = "preview",
                    route = MedicationRoute.INJECTION,
                    name = "Tirzepatide",
                    doseAmount = 5.0,
                    doseUnit = "mg",
                    frequency = MedicationFrequency.WEEKLY,
                    updatedAtEpochMillis = 0L,
                ),
                streakDays = 12,
                memberSinceEpochDay = today - 200,
                doseSchedule = DoseSchedule.Next(today + 3, 3, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(4)),
            ),
            actions = ProfileActions(),
        )
    }
}

@Preview(name = "Profile - new user", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ProfileNewUserPreview() {
    AppTheme(darkTheme = false) {
        ProfileContent(state = ProfileUiState(), actions = ProfileActions())
    }
}
