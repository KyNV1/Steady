package com.steady.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ListSettingsRow
import com.steady.app.ui.components.MacroGridCard
import com.steady.app.ui.components.MacroStat
import com.steady.app.ui.components.StatProgressCard
import com.steady.app.ui.theme.LocalAppSpacing

@Composable
fun HomeScreen(onLogDose: () -> Unit, onLogSideEffect: () -> Unit, onScanMeal: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScanMeal,
                text = { Text(stringResource(R.string.home_scan_meal)) },
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LocalAppSpacing.current.screenPadding, vertical = LocalAppSpacing.current.medium),
            verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
        ) {
            Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium)

            StatProgressCard(
                icon = Icons.Default.CalendarMonth,
                label = stringResource(R.string.home_next_dose_label),
                currentValue = stringResource(R.string.home_no_data_placeholder),
                goalSuffix = null,
                progress = 0f,
            )

            Text(stringResource(R.string.home_today_totals), style = MaterialTheme.typography.titleMedium)
            MacroGridCard(
                listOf(
                    MacroStat(stringResource(R.string.macro_calories), "0"),
                    MacroStat(stringResource(R.string.macro_protein), "0g"),
                    MacroStat(stringResource(R.string.macro_fiber), "0g"),
                    MacroStat(stringResource(R.string.macro_carbs), "0g"),
                ),
            )

            Text(stringResource(R.string.home_log_a_moment), style = MaterialTheme.typography.titleMedium)
            ListSettingsRow(
                icon = Icons.Default.Medication,
                label = stringResource(R.string.home_log_dose_action),
                onClick = onLogDose,
                modifier = Modifier.fillMaxWidth(),
            )
            ListSettingsRow(
                icon = Icons.Default.HealthAndSafety,
                label = stringResource(R.string.home_log_side_effect_action),
                onClick = onLogSideEffect,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
