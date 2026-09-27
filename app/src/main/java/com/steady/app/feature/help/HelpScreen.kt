package com.steady.app.feature.help

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.steady.app.R
import com.steady.app.ui.components.ScreenScaffold
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.SteadyShapes

private data class FaqEntry(val questionRes: Int, val answerRes: Int)

private val faqEntries = listOf(
    FaqEntry(R.string.help_faq_scan_question, R.string.help_faq_scan_answer),
    FaqEntry(R.string.help_faq_data_question, R.string.help_faq_data_answer),
    FaqEntry(R.string.help_faq_medical_question, R.string.help_faq_medical_answer),
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.help_title), onBack = onBack) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = SteadyShapes.card,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column {
                faqEntries.forEachIndexed { index, entry ->
                    Column(Modifier.padding(LocalAppSpacing.current.medium)) {
                        Text(stringResource(entry.questionRes), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(entry.answerRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = LocalAppSpacing.current.extraSmall),
                        )
                    }
                    if (index != faqEntries.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
