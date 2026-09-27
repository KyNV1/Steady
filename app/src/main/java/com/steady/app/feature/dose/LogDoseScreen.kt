package com.steady.app.feature.dose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun LogDoseScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.log_dose_title), onBack = onBack) {
        ComingSoonContent(stringResource(R.string.log_dose_coming_soon))
    }
}
