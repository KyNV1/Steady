package com.steady.app.feature.help

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.steady.app.BuildConfig
import com.steady.app.R
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import kotlinx.coroutines.launch

private data class FaqEntry(val questionRes: Int, val answerRes: Int)

private val faqEntries = listOf(
    FaqEntry(R.string.help_faq_scan_question, R.string.help_faq_scan_answer),
    FaqEntry(R.string.help_faq_medication_question, R.string.help_faq_medication_answer),
    FaqEntry(R.string.help_faq_goals_question, R.string.help_faq_goals_answer),
    FaqEntry(R.string.help_faq_subscription_question, R.string.help_faq_subscription_answer),
    FaqEntry(R.string.help_faq_medical_question, R.string.help_faq_medical_answer),
)

/** Case-insensitive match on any whitespace-separated term, across question and answer. */
internal fun faqMatches(question: String, answer: String, query: String): Boolean {
    val terms = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (terms.isEmpty()) return true
    val haystack = "$question $answer".lowercase()
    return terms.all { it in haystack }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val subject = stringResource(R.string.help_contact_email_subject)
    val unavailable = stringResource(R.string.help_contact_unavailable)

    Box(Modifier.fillMaxSize()) {
        HelpContent(
            onBack = onBack,
            onContact = {
                val opened = BuildConfig.SUPPORT_EMAIL.isNotBlank() && try {
                    val uri = "mailto:${BuildConfig.SUPPORT_EMAIL}".toUri()
                    context.startActivity(Intent(Intent.ACTION_SENDTO, uri).putExtra(Intent.EXTRA_SUBJECT, subject))
                    true
                } catch (_: ActivityNotFoundException) {
                    false
                }
                if (!opened) scope.launch { snackbarHost.showSnackbar(unavailable) }
            },
        )
        SnackbarHost(snackbarHost, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/** Stateless-from-the-outside Help layout (search and expansion are local UI state), previewable. */
@Composable
fun HelpContent(onBack: () -> Unit, onContact: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf<Int?>(null) }
    val entries = faqEntries.mapIndexed { index, entry ->
        Triple(index, stringResource(entry.questionRes), stringResource(entry.answerRes))
    }.filter { (_, question, answer) -> faqMatches(question, answer, query) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = stringResource(R.string.help_title), onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
        ) {
            SearchField(query, onQueryChange = { query = it })

            Text(
                stringResource(R.string.help_frequently_asked).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp),
            )
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                if (entries.isEmpty()) {
                    Text(
                        stringResource(R.string.help_no_results),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                } else {
                    Column {
                        entries.forEachIndexed { position, (index, question, answer) ->
                            FaqRow(
                                question = question,
                                answer = answer,
                                expanded = expanded == index,
                                onToggle = { expanded = if (expanded == index) null else index },
                            )
                            if (position != entries.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            ContactCard(onContact, Modifier.padding(top = 20.dp))
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = muted, modifier = Modifier.size(20.dp))
            Box(
                Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
            ) {
                if (query.isEmpty()) {
                    Text(stringResource(R.string.help_search_hint), style = MaterialTheme.typography.bodyMedium, color = muted)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FaqRow(question: String, answer: String, expanded: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(if (expanded) 90f else 0f, label = "faqChevron")
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                question,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LocalSteadyExtendedColors.current.onSurfaceMuted,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotation),
            )
        }
        AnimatedVisibility(expanded) {
            Text(
                answer,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ContactCard(onContact: () -> Unit, modifier: Modifier) {
    val extended = LocalSteadyExtendedColors.current
    SteadyCard(modifier = modifier.fillMaxWidth(), color = extended.surfaceContainerGreen) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(
                icon = Icons.AutoMirrored.Filled.Chat,
                size = 38.dp,
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = 20.dp,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(
                    stringResource(R.string.help_contact_title),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    stringResource(R.string.help_contact_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onContact) {
                Text(
                    stringResource(R.string.help_contact_action),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(name = "Help", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HelpScreenPreview() {
    AppTheme(darkTheme = false) { HelpContent(onBack = {}, onContact = {}) }
}
