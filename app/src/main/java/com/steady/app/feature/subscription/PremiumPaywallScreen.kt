package com.steady.app.feature.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors

private val SparkleGold = Color(0xFFF9D68A)

@Composable
fun PremiumPaywallScreen(onBack: () -> Unit, viewModel: PremiumPaywallViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val message = state.message?.let { stringResource(it.textRes()) }
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHost.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }
    Box(Modifier.fillMaxSize()) {
        PremiumPaywallContent(
            state = state,
            actions = PaywallActions(
                onClose = onBack,
                onSelect = viewModel::select,
                onSubscribe = viewModel::onSubscribe,
                onRestore = viewModel::onRestore,
                onLegalLink = viewModel::onLegalLink,
                onLoadSamplePrices = viewModel::loadSamplePrices,
            ),
        )
        SnackbarHost(
            snackbarHost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding(),
        )
    }
}

data class PaywallActions(
    val onClose: () -> Unit = {},
    val onSelect: (PlanPeriod) -> Unit = {},
    val onSubscribe: () -> Unit = {},
    val onRestore: () -> Unit = {},
    val onLegalLink: () -> Unit = {},
    val onLoadSamplePrices: () -> Unit = {},
)

/** Stateless paywall layout, so it renders in previews without Hilt. */
@Composable
fun PremiumPaywallContent(state: PaywallUiState, actions: PaywallActions) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        IconTile(
            icon = Icons.Default.Close,
            size = 36.dp,
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.onSurface,
            iconSize = 20.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            contentDescription = stringResource(R.string.paywall_close),
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = actions.onClose),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Hero()
            FeatureList()
            PlanCards(state, actions.onSelect)
            if (state.offers.isEmpty()) {
                Text(
                    stringResource(R.string.paywall_prices_unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (state.canLoadSamples) {
                    TextButton(onClick = actions.onLoadSamplePrices, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            stringResource(R.string.paywall_load_sample_prices),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Footer(state.selectedOffer?.freeTrialDays, actions)
    }
}

@Composable
private fun Hero() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        IconTile(
            icon = Icons.Default.AutoAwesome,
            size = 52.dp,
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            iconTint = SparkleGold,
            iconSize = 28.dp,
        )
        Text(
            stringResource(R.string.premium_paywall_title),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
        )
        Text(
            stringResource(R.string.paywall_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 290.dp),
        )
    }
}

@Composable
private fun FeatureList() {
    val features = listOf(
        R.string.paywall_feature_scans,
        R.string.paywall_feature_journey,
        R.string.paywall_feature_trends,
        R.string.paywall_feature_side_effects,
    )
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(
                        icon = Icons.Default.Check,
                        size = 24.dp,
                        containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconSize = 16.dp,
                    )
                    Text(
                        stringResource(feature),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanCards(state: PaywallUiState, onSelect: (PlanPeriod) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PlanPeriod.entries.forEach { period ->
            PlanCard(
                period = period,
                offer = state.offers[period],
                selected = state.selected == period,
                onClick = { onSelect(period) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlanCard(period: PlanPeriod, offer: PlanOffer?, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val container = if (selected) onSurface else MaterialTheme.colorScheme.surface
    val titleColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val priceColor = if (selected) Color.White else onSurface
    val captionColor = if (selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
    val title = stringResource(if (period == PlanPeriod.WEEKLY) R.string.paywall_plan_weekly else R.string.paywall_plan_yearly)
    val caption = when {
        offer == null -> null
        period == PlanPeriod.WEEKLY -> stringResource(R.string.paywall_per_week)
        else -> stringResource(R.string.paywall_per_year_weekly, weeklyEquivalent(offer))
    }

    Box(modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = container,
            border = if (selected) null else BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = if (selected) 4.dp else 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = if (period == PlanPeriod.YEARLY) 4.dp else 0.dp)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = titleColor,
                        modifier = Modifier.weight(1f),
                    )
                    RadioDot(selected)
                }
                Column {
                    Text(
                        offer?.formattedPrice ?: "—",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 21.sp, fontWeight = FontWeight.SemiBold),
                        color = priceColor,
                    )
                    if (caption != null) {
                        Text(caption, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = captionColor)
                    }
                }
            }
        }
        if (period == PlanPeriod.YEARLY) {
            Text(
                stringResource(R.string.paywall_best_value).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = Color.White,
                modifier = Modifier
                    .offset(x = 12.dp, y = (-9).dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun RadioDot(selected: Boolean) {
    if (selected) {
        Box(
            Modifier
                .size(16.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(12.dp))
        }
    } else {
        Box(
            Modifier
                .size(16.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}

@Composable
private fun Footer(freeTrialDays: Int?, actions: PaywallActions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = actions.onSubscribe,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                if (freeTrialDays != null) {
                    stringResource(R.string.paywall_cta_trial, freeTrialDays)
                } else {
                    stringResource(R.string.paywall_cta_subscribe)
                },
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
            )
            Spacer(Modifier.size(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Text(
            stringResource(if (freeTrialDays != null) R.string.paywall_fine_print_trial else R.string.paywall_fine_print),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            LegalLink(stringResource(R.string.paywall_restore), actions.onRestore)
            Dot()
            LegalLink(stringResource(R.string.sign_in_terms_link), actions.onLegalLink)
            Dot()
            LegalLink(stringResource(R.string.paywall_privacy), actions.onLegalLink)
        }
    }
}

@Composable
private fun LegalLink(text: String, onClick: () -> Unit) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = LocalSteadyExtendedColors.current.onSurfaceMuted,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
    )
}

@Composable
private fun Dot() {
    Text("•", fontSize = 9.sp, color = LocalSteadyExtendedColors.current.onSurfaceMuted)
}

private fun PaywallMessage.textRes(): Int = when (this) {
    PaywallMessage.PURCHASE_UNAVAILABLE -> R.string.paywall_purchase_unavailable
    PaywallMessage.RESTORE_UNAVAILABLE -> R.string.paywall_restore_unavailable
    PaywallMessage.LEGAL_UNAVAILABLE -> R.string.paywall_legal_unavailable
}

@Preview(name = "Paywall", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PremiumPaywallPreview() {
    AppTheme(darkTheme = false) {
        PremiumPaywallContent(state = PaywallUiState(offers = debugSampleOffers()), actions = PaywallActions())
    }
}

@Preview(name = "Paywall - no prices", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PremiumPaywallNoPricesPreview() {
    AppTheme(darkTheme = false) {
        PremiumPaywallContent(state = PaywallUiState(canLoadSamples = true), actions = PaywallActions())
    }
}
