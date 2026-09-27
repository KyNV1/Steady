package com.steady.app.feature.subscription

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.PremiumBadge
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun PremiumPaywallScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.premium_paywall_title), onBack = onBack) {
        PremiumBadge()
        ComingSoonContent(stringResource(R.string.premium_paywall_coming_soon))
    }
}
