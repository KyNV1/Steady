package com.steady.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.steady.app.ui.theme.LocalAppSpacing

/** Common shell for detail/tab screens: top bar + scrollable, screen-padded content column. */
@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Scaffold(topBar = { AppTopBar(title = title, onBack = onBack, trailing = trailing) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LocalAppSpacing.current.screenPadding, vertical = LocalAppSpacing.current.medium),
            verticalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap),
            content = content,
        )
    }
}
