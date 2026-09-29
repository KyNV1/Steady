package com.steady.app.feature.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppTextField
import com.steady.app.ui.components.InfoBanner
import com.steady.app.ui.components.PillToggleGroup
import com.steady.app.ui.components.ScreenScaffold
import com.steady.app.ui.theme.LocalAppSpacing

@Composable
fun MedicationDoseScreen(onBack: () -> Unit, viewModel: MedicationDoseViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ScreenScaffold(title = stringResource(R.string.medication_dose_title), onBack = onBack) {
        if (!state.isLoading) {
            MedicationDoseForm(
                state = state,
                onRouteChange = viewModel::setRoute,
                onNameChange = viewModel::setName,
                onDoseAmountChange = viewModel::setDoseAmount,
                onFrequencyChange = viewModel::setFrequency,
            )
            AppButton(
                text = stringResource(R.string.medication_dose_save),
                onClick = { viewModel.save(onBack) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal fun MedicationDoseForm(
    state: MedicationDoseUiState,
    onRouteChange: (MedicationRoute) -> Unit,
    onNameChange: (String) -> Unit,
    onDoseAmountChange: (String) -> Unit,
    onFrequencyChange: (MedicationFrequency) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap)) {
        Text(stringResource(R.string.medication_dose_how_taken), style = MaterialTheme.typography.labelMedium)
        PillToggleGroup(
            options = listOf(MedicationRoute.INJECTION, MedicationRoute.ORAL),
            selected = state.route,
            onSelect = onRouteChange,
            label = { route ->
                when (route) {
                    MedicationRoute.INJECTION -> stringResource(R.string.medication_dose_route_injection)
                    MedicationRoute.ORAL -> stringResource(R.string.medication_dose_route_oral)
                }
            },
        )
        if (state.route == MedicationRoute.ORAL) {
            Text(
                stringResource(R.string.medication_dose_route_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AppTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.medication_dose_name_label),
        )
        AppTextField(
            value = state.doseAmount,
            onValueChange = onDoseAmountChange,
            label = stringResource(R.string.medication_dose_amount_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Text(stringResource(R.string.medication_dose_frequency_label), style = MaterialTheme.typography.labelMedium)
        PillToggleGroup(
            options = listOf(MedicationFrequency.WEEKLY, MedicationFrequency.DAILY),
            selected = state.frequency,
            onSelect = onFrequencyChange,
            label = { frequency ->
                when (frequency) {
                    MedicationFrequency.WEEKLY -> stringResource(R.string.medication_dose_frequency_weekly)
                    MedicationFrequency.DAILY -> stringResource(R.string.medication_dose_frequency_daily)
                }
            },
        )

        InfoBanner(stringResource(R.string.medication_dose_tip))
    }
}
