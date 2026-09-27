package com.steady.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing

/** Scrim + lock/headline/CTA placed over a blurred/dimmed preview of premium-only content. */
@Composable
fun LockedPremiumOverlay(headline: String, body: String, ctaLabel: String, onUnlock: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(LocalAppSpacing.current.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
            Text(
                headline,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = LocalAppSpacing.current.small),
            )
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = LocalAppSpacing.current.extraSmall, bottom = LocalAppSpacing.current.medium),
            )
            AppButton(text = ctaLabel, onClick = onUnlock)
        }
    }
}

@Preview
@Composable
private fun LockedPremiumOverlayPreview() {
    AppTheme {
        LockedPremiumOverlay(
            headline = "Unlock your Journey",
            body = "See your full progress history and trends with Premium.",
            ctaLabel = "Unlock with Premium",
            onUnlock = {},
        )
    }
}
