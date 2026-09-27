package com.steady.app.feature.medication

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun MedicationDoseScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.medication_dose_title), onBack = onBack) {
        ComingSoonContent(stringResource(R.string.medication_dose_coming_soon))
    }
}
