package com.steady.app.feature.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun ScanScreen() {
    ScreenScaffold(title = stringResource(R.string.scan_title)) {
        ComingSoonContent(stringResource(R.string.scan_coming_soon))
    }
}
