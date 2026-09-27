package com.steady.app.feature.insights

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ListSettingsRow
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun InsightsScreen(onOpenJourney: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.insights_title)) {
        ListSettingsRow(Icons.Default.Timeline, stringResource(R.string.nav_journey), onOpenJourney, Modifier.fillMaxWidth())
        ComingSoonContent(stringResource(R.string.insights_coming_soon))
    }
}
