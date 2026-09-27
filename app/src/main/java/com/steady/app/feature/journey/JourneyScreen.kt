package com.steady.app.feature.journey

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.LockedPremiumOverlay

@Composable
fun JourneyScreen(onBack: () -> Unit, onUnlock: () -> Unit) {
    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.journey_title), onBack = onBack) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LockedPremiumOverlay(
                headline = stringResource(R.string.journey_locked_headline),
                body = stringResource(R.string.journey_locked_body),
                ctaLabel = stringResource(R.string.journey_unlock_cta),
                onUnlock = onUnlock,
            )
        }
    }
}
