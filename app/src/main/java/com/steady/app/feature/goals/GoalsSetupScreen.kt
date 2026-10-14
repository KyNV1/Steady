package com.steady.app.feature.goals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R

/** First-run setup wrapper: same layout as [DailyGoalsScreen], pre-filled with recommended defaults,
 * no back chevron, and the CTA finishes setup instead of popping back. */
@Composable
fun GoalsSetupScreen(onFinished: () -> Unit, viewModel: DailyGoalsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DailyGoalsLayout(
        title = stringResource(R.string.daily_goals_title),
        onBack = null,
        ctaText = stringResource(R.string.daily_goals_save),
        state = state,
        actions = viewModel.goalActions(),
        onCta = { viewModel.save(onFinished) },
    )
}
