package com.steady.app.feature.sideeffects

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.MedicalWarningPanel
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun LogSideEffectScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.log_side_effect_title), onBack = onBack) {
        MedicalWarningPanel(stringResource(R.string.side_effect_severe_warning))
        ComingSoonContent(stringResource(R.string.log_side_effect_coming_soon))
    }
}
