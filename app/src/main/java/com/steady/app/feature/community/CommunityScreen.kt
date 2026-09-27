package com.steady.app.feature.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun CommunityScreen() {
    ScreenScaffold(title = stringResource(R.string.community_title)) {
        ComingSoonContent(stringResource(R.string.community_coming_soon))
    }
}
