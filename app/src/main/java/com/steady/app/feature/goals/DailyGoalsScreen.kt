package com.steady.app.feature.goals

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.components.GoalStepperCard
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun DailyGoalsScreen(onBack: () -> Unit, viewModel: DailyGoalsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ScreenScaffold(title = stringResource(R.string.daily_goals_title), onBack = onBack) {
        if (!state.isLoading) {
            Text(
                stringResource(R.string.daily_goals_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            DailyGoalsCards(state = state, viewModel = viewModel)
            AppButton(
                text = stringResource(R.string.daily_goals_reset_defaults),
                onClick = viewModel::resetToRecommended,
                style = AppButtonStyle.TEXT,
            )
            AppButton(text = stringResource(R.string.daily_goals_save), onClick = { viewModel.save(onBack) })
        }
    }
}

@Composable
internal fun DailyGoalsCards(state: DailyGoalsUiState, viewModel: DailyGoalsViewModel) {
    GoalStepperCard(
        icon = Icons.Default.Spa,
        label = stringResource(R.string.daily_goals_fiber_label),
        valueText = "${state.fiberGrams} g",
        caption = stringResource(R.string.daily_goals_fiber_caption),
        onDecrement = viewModel::decrementFiber,
        onIncrement = viewModel::incrementFiber,
    )
    GoalStepperCard(
        icon = Icons.Default.FitnessCenter,
        label = stringResource(R.string.daily_goals_protein_label),
        valueText = "${state.proteinGrams} g",
        caption = stringResource(R.string.daily_goals_protein_caption),
        onDecrement = viewModel::decrementProtein,
        onIncrement = viewModel::incrementProtein,
    )
    GoalStepperCard(
        icon = Icons.Default.WaterDrop,
        label = stringResource(R.string.daily_goals_water_label),
        valueText = "${state.waterOunces} oz",
        caption = stringResource(R.string.daily_goals_water_caption),
        onDecrement = viewModel::decrementWater,
        onIncrement = viewModel::incrementWater,
    )
}
