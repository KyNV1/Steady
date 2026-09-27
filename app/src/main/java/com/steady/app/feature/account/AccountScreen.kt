package com.steady.app.feature.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun AccountScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.account_title), onBack = onBack) {
        ComingSoonContent(stringResource(R.string.account_coming_soon))
    }
}
