package com.steady.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing

/** Onboarding-pager style page indicator: solid primary dot for the current page, muted otherwise. */
@Composable
fun PageDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.small)) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Row(
                modifier = Modifier
                    .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                    .background(
                        color = if (isSelected) activeColor else inactiveColor,
                        shape = CircleShape,
                    ),
            ) {}
        }
    }
}

@Preview
@Composable
private fun PageDotsIndicatorPreview() {
    AppTheme { PageDotsIndicator(pageCount = 3, currentPage = 1) }
}
