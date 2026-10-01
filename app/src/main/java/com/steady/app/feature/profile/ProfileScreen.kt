package com.steady.app.feature.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.core.storage.ThemeMode
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.components.ListSettingsRow
import com.steady.app.ui.components.LoadingDialog
import com.steady.app.ui.components.PillToggleGroup
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    onOpenDailyGoals: () -> Unit,
    onOpenMedicationDose: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenHelp: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(state.loggedOut) { if (state.loggedOut) onLoggedOut() }

    ScreenScaffold {
        Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.headlineMedium)

        ListSettingsRow(Icons.Default.Medication, stringResource(R.string.nav_medication_dose), onOpenMedicationDose, Modifier.fillMaxWidth())
        ListSettingsRow(Icons.Default.Flag, stringResource(R.string.nav_daily_goals), onOpenDailyGoals, Modifier.fillMaxWidth())
        ListSettingsRow(Icons.Default.Notifications, stringResource(R.string.nav_notifications), onOpenNotifications, Modifier.fillMaxWidth())
        ListSettingsRow(Icons.Default.AccountCircle, stringResource(R.string.nav_account), onOpenAccount, Modifier.fillMaxWidth())
        ListSettingsRow(Icons.AutoMirrored.Filled.HelpOutline, stringResource(R.string.nav_help), onOpenHelp, Modifier.fillMaxWidth())

        Text(stringResource(R.string.theme_title), style = MaterialTheme.typography.titleMedium)
        PillToggleGroup(
            options = ThemeMode.entries,
            selected = themeMode,
            onSelect = viewModel::setThemeMode,
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

        AppButton(text = stringResource(R.string.logout_action), onClick = viewModel::signOut, style = AppButtonStyle.TEXT)
    }
    LoadingDialog(state.isLoggingOut)
}
