package com.steady.app.feature.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.theme.LocalAppSpacing

/** First-run setup wrapper: same stepper cards as [DailyGoalsScreen]'s settings screen, pre-filled
 * with recommended defaults, no back chevron, and the CTA finishes setup instead of popping back. */
@Composable
fun GoalsSetupScreen(onFinished: () -> Unit, viewModel: DailyGoalsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LocalAppSpacing.current.screenPadding, vertical = LocalAppSpacing.current.medium),
            verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
        ) {
            Text(stringResource(R.string.daily_goals_title), style = MaterialTheme.typography.headlineMedium)
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
                AppButton(
                    text = stringResource(R.string.daily_goals_save),
                    onClick = { viewModel.save(onFinished) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
