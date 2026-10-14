package com.steady.app.feature.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.GoalStepperCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors

@Composable
fun DailyGoalsScreen(onBack: () -> Unit, viewModel: DailyGoalsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DailyGoalsLayout(
        title = stringResource(R.string.daily_goals_title),
        onBack = onBack,
        ctaText = stringResource(R.string.daily_goals_save),
        state = state,
        actions = viewModel.goalActions(),
        onCta = { viewModel.save(onBack) },
    )
}

data class GoalActions(
    val onFiber: (increase: Boolean) -> Unit = {},
    val onProtein: (increase: Boolean) -> Unit = {},
    val onWater: (increase: Boolean) -> Unit = {},
    val onReset: () -> Unit = {},
)

internal fun DailyGoalsViewModel.goalActions() = GoalActions(
    onFiber = { if (it) incrementFiber() else decrementFiber() },
    onProtein = { if (it) incrementProtein() else decrementProtein() },
    onWater = { if (it) incrementWater() else decrementWater() },
    onReset = ::resetToRecommended,
)

/** Shared by the settings screen and first-run setup: top bar, scrolling cards, pinned CTA. */
@Composable
internal fun DailyGoalsLayout(
    title: String,
    onBack: (() -> Unit)?,
    ctaText: String,
    state: DailyGoalsUiState,
    actions: GoalActions,
    onCta: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = title, onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 24.dp),
        ) {
            Text(
                stringResource(R.string.daily_goals_subtitle),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            if (!state.isLoading) {
                DailyGoalsCards(state, actions)
                TextButton(
                    onClick = actions.onReset,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                ) {
                    Text(
                        stringResource(R.string.daily_goals_reset_defaults),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, textDecoration = TextDecoration.Underline),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Button(
            onClick = onCta,
            enabled = !state.isLoading && !state.isSaving,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(ctaText, style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp))
        }
    }
}

@Composable
private fun DailyGoalsCards(state: DailyGoalsUiState, actions: GoalActions) {
    val extended = LocalSteadyExtendedColors.current
    val tertiary = MaterialTheme.colorScheme.tertiary
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GoalCard(
            icon = Icons.Outlined.Spa,
            tint = MaterialTheme.colorScheme.primary,
            container = extended.surfaceContainerGreen,
            label = stringResource(R.string.daily_goals_fiber_label),
            caption = stringResource(R.string.daily_goals_fiber_caption),
            valueText = stringResource(R.string.daily_goals_grams, state.fiberGrams),
            value = state.fiberGrams,
            range = FIBER_RANGE,
            onChange = actions.onFiber,
        )
        GoalCard(
            icon = Icons.Outlined.EggAlt,
            tint = MaterialTheme.colorScheme.secondary,
            container = extended.surfaceContainerOrange,
            label = stringResource(R.string.daily_goals_protein_label),
            caption = stringResource(R.string.daily_goals_protein_caption),
            valueText = stringResource(R.string.daily_goals_grams, state.proteinGrams),
            value = state.proteinGrams,
            range = PROTEIN_RANGE,
            onChange = actions.onProtein,
        )
        GoalCard(
            icon = Icons.Outlined.WaterDrop,
            tint = tertiary,
            container = tertiary.copy(alpha = 0.14f),
            label = stringResource(R.string.daily_goals_water_label),
            caption = stringResource(R.string.daily_goals_water_caption),
            valueText = stringResource(R.string.daily_goals_ounces, state.waterOunces),
            value = state.waterOunces,
            range = WATER_OZ_RANGE,
            onChange = actions.onWater,
        )
    }
}

@Composable
private fun GoalCard(
    icon: ImageVector,
    tint: Color,
    container: Color,
    label: String,
    caption: String,
    valueText: String,
    value: Int,
    range: GoalRange,
    onChange: (increase: Boolean) -> Unit,
) {
    GoalStepperCard(
        icon = icon,
        iconTint = tint,
        iconContainer = container,
        label = label,
        valueText = valueText,
        caption = caption,
        onDecrement = { onChange(false) },
        onIncrement = { onChange(true) },
        canDecrement = range.canDecrement(value),
        canIncrement = range.canIncrement(value),
        decrementDescription = stringResource(R.string.daily_goals_decrease, label),
        incrementDescription = stringResource(R.string.daily_goals_increase, label),
    )
}

@Preview(name = "Daily goals", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DailyGoalsPreview() {
    AppTheme(darkTheme = false) {
        DailyGoalsLayout(
            title = "Daily goals",
            onBack = {},
            ctaText = "Save",
            state = DailyGoalsUiState(isLoading = false),
            actions = GoalActions(),
            onCta = {},
        )
    }
}
