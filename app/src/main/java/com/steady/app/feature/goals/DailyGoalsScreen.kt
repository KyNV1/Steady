package com.steady.app.feature.goals

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun DailyGoalsScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.daily_goals_title), onBack = onBack) {
        ComingSoonContent(stringResource(R.string.daily_goals_coming_soon))
    }
}
