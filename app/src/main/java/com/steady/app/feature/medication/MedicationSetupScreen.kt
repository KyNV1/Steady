package com.steady.app.feature.medication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R

/** First-run setup wrapper: same layout as [MedicationDoseScreen], but no back chevron and the CTA
 * continues into goals setup instead of saving-and-popping. */
@Composable
fun MedicationSetupScreen(onContinue: () -> Unit, viewModel: MedicationDoseViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MedicationDoseLayout(
        title = stringResource(R.string.medication_dose_title),
        onBack = null,
        ctaText = stringResource(R.string.medication_setup_continue),
        state = state,
        actions = viewModel.formActions(),
        onCta = { viewModel.save(onContinue) },
    )
}
