package com.steady.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes

@Composable
fun PremiumBadge(text: String = "PREMIUM") {
    val extended = LocalSteadyExtendedColors.current
    Surface(shape = SteadyShapes.chip, color = extended.premiumBg) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                textAlign = TextAlign.Center,
            ),
            color = extended.premiumText,
            modifier = Modifier.padding(
                horizontal = LocalAppSpacing.current.small,
                vertical = LocalAppSpacing.current.extraSmall,
            ),
        )
    }
}

@Preview
@Composable
private fun PremiumBadgePreview() {
    AppTheme { PremiumBadge() }
}
