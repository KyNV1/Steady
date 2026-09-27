package com.steady.app.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.steady.app.R
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.theme.LocalAppSpacing
import kotlinx.coroutines.launch

private data class OnboardingPage(val icon: ImageVector, val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnboardingPage(Icons.Default.Spa, R.string.onboarding_welcome_title, R.string.onboarding_welcome_body),
    OnboardingPage(Icons.Default.CameraAlt, R.string.onboarding_scan_title, R.string.onboarding_scan_body),
    OnboardingPage(Icons.AutoMirrored.Filled.TrendingUp, R.string.onboarding_progress_title, R.string.onboarding_progress_body),
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(LocalAppSpacing.current.screenPadding),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                AppButton(text = stringResource(R.string.onboarding_skip), onClick = onFinished, style = AppButtonStyle.TEXT)
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(page.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        stringResource(page.titleRes),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = LocalAppSpacing.current.medium),
                    )
                    Text(
                        stringResource(page.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = LocalAppSpacing.current.small),
                    )
                }
            }

            val isLastPage = pagerState.currentPage == pages.lastIndex
            AppButton(
                text = stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next),
                onClick = {
                    if (isLastPage) {
                        onFinished()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            )
        }
    }
}
