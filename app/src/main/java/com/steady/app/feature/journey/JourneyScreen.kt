package com.steady.app.feature.journey

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steady.app.R
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.PremiumBadge
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors

/**
 * Locked Journey teaser. Premium entitlement isn't wired yet, so every user sees the locked state
 * and the CTA leads to the paywall.
 */
@Composable
fun JourneyScreen(onBack: () -> Unit, onUnlock: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        JourneyTopBar(onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = Dimens.dp_24, end = Dimens.dp_24, bottom = Dimens.dp_24),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp_16),
        ) {
            Text(
                stringResource(R.string.journey_intro),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.dp_4),
            )
            LockedPreview(onUnlock)
            BenefitsCard()
        }
    }
}

@Composable
private fun JourneyTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_10),
    ) {
        Surface(
            shape = RoundedCornerShape(Dimens.dp_12),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.CenterStart),
        ) {
            val back = stringResource(R.string.common_back)
            IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = back }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(Dimens.dp_20))
            }
        }
        Text(
            stringResource(R.string.journey_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
        )
        Box(Modifier.align(Alignment.CenterEnd)) { PremiumBadge() }
    }
}

/** Blurred illustration of the unlocked Journey with the unlock panel floating on top. */
@Composable
private fun LockedPreview(onUnlock: () -> Unit) {
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Box {
            // Decorative only: hidden from accessibility and blurred (API 31+; faded below that).
            Column(
                modifier = Modifier
                    .padding(Dimens.dp_16)
                    .then(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Modifier.blur(4.5.dp) else Modifier)
                    .alpha(0.4f)
                    .clearAndSetSemantics { },
                verticalArrangement = Arrangement.spacedBy(Dimens.dp_14),
            ) {
                PreviewPhotoRow()
                PreviewStatRow()
                PreviewTrend()
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.4f))
                    .padding(Dimens.dp_16),
                contentAlignment = Alignment.Center,
            ) {
                UnlockPanel(onUnlock)
            }
        }
    }
}

@Composable
private fun UnlockPanel(onUnlock: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Dimens.dp_16),
        color = Color.White.copy(alpha = 0.95f),
        border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.8f)),
        shadowElevation = 6.dp,
        modifier = Modifier.widthIn(max = 316.dp),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.dp_20),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconTile(
                icon = Icons.Outlined.Lock,
                size = Dimens.dp_48,
                containerColor = MaterialTheme.colorScheme.primary,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                iconSize = Dimens.dp_22,
            )
            Text(
                stringResource(R.string.journey_locked_headline),
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 19.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.dp_12),
            )
            Text(
                stringResource(R.string.journey_locked_body),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 17.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.dp_6),
            )
            Button(
                onClick = onUnlock,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.dp_16)
                    .height(Dimens.dp_44),
            ) {
                Text(stringResource(R.string.journey_unlock_cta), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(Dimens.dp_8))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text(
                stringResource(R.string.journey_included_note),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal, letterSpacing = 0.sp),
                color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.dp_10),
            )
        }
    }
}

@Composable
private fun PreviewPhotoRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp_12))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(Dimens.dp_1, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Dimens.dp_12))
            .padding(Dimens.dp_12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(
            icon = Icons.Outlined.PhotoCamera,
            size = Dimens.dp_48,
            shape = RoundedCornerShape(Dimens.dp_8),
            containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
            iconSize = Dimens.dp_24,
        )
        Column(
            Modifier
                .weight(1f)
                .padding(start = Dimens.dp_12),
        ) {
            Text("Today", style = MaterialTheme.typography.labelSmall, color = LocalSteadyExtendedColors.current.onSurfaceMuted)
            Text("Take today's photo", style = MaterialTheme.typography.titleSmall)
        }
        IconTile(icon = Icons.Default.Add, size = Dimens.dp_32, containerColor = MaterialTheme.colorScheme.surface)
    }
}

@Composable
private fun PreviewStatRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp_12))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(vertical = Dimens.dp_12),
    ) {
        PreviewStat("Weight", "164 lb")
        PreviewStat("BMI", "24.7")
        PreviewStat("Lost", "−8 lb")
    }
}

@Composable
private fun RowScope.PreviewStat(label: String, value: String) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = LocalSteadyExtendedColors.current.onSurfaceMuted)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PreviewTrend() {
    val heights = listOf(56, 52, 48, 44, 40, 32, 28, 24)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(Dimens.dp_12))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Dimens.dp_12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        heights.forEachIndexed { index, h ->
            Box(
                Modifier
                    .width(Dimens.dp_10)
                    .height(minOf(h, 48).dp)
                    .background(
                        if (index >= 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    ),
            )
        }
    }
}

@Composable
private fun BenefitsCard() {
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.dp_16)) {
            Benefit(stringResource(R.string.journey_benefit_photos_title), stringResource(R.string.journey_benefit_photos_body))
            HorizontalDivider(
                color = LocalSteadyExtendedColors.current.outlineSubtle,
                modifier = Modifier.padding(vertical = Dimens.dp_14),
            )
            Benefit(stringResource(R.string.journey_benefit_smoothing_title), stringResource(R.string.journey_benefit_smoothing_body))
        }
    }
}

@Composable
private fun Benefit(title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        IconTile(
            icon = Icons.Default.Check,
            size = Dimens.dp_28,
            containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
            iconTint = MaterialTheme.colorScheme.primary,
            iconSize = 17.dp,
            modifier = Modifier.padding(top = Dimens.dp_2),
        )
        Column(Modifier.padding(start = Dimens.dp_12)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(
                body,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.dp_2),
            )
        }
    }
}

@Preview(name = "Journey (locked)", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun JourneyScreenPreview() {
    AppTheme(darkTheme = false) { JourneyScreen(onBack = {}, onUnlock = {}) }
}
