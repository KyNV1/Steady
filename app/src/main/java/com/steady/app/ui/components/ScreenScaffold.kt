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

/**
 * Common shell for detail/tab screens: optional top bar + scrollable, screen-padded content
 * column. Pass [title] to get the standard [AppTopBar]; leave it null for root tab screens that
 * want no top bar (e.g. Home, Profile). [floatingActionButton] forwards straight to [Scaffold].
 */
@Composable
fun ScreenScaffold(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = { title?.let { AppTopBar(title = it, onBack = onBack, trailing = trailing) } },
        floatingActionButton = floatingActionButton ?: {},
    ) { padding ->
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
