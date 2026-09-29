package com.steady.app.feature.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.theme.LocalAppSpacing

/** First-run setup wrapper: same form as [MedicationDoseScreen]'s settings screen, but no back
 * chevron and the CTA continues into goals setup instead of saving-and-popping. */
@Composable
fun MedicationSetupScreen(onContinue: () -> Unit, viewModel: MedicationDoseViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LocalAppSpacing.current.screenPadding, vertical = LocalAppSpacing.current.medium),
            verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
        ) {
            Text(stringResource(R.string.medication_dose_title), style = MaterialTheme.typography.headlineMedium)
            if (!state.isLoading) {
                MedicationDoseForm(
                    state = state,
                    onRouteChange = viewModel::setRoute,
                    onNameChange = viewModel::setName,
                    onDoseAmountChange = viewModel::setDoseAmount,
                    onFrequencyChange = viewModel::setFrequency,
                )
                AppButton(
                    text = stringResource(R.string.medication_setup_continue),
                    onClick = { viewModel.save(onContinue) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
