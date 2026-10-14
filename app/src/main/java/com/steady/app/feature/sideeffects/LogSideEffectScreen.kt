package com.steady.app.feature.sideeffects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.SideEffectSeverity
import com.steady.app.domain.model.SideEffectSymptom
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.ChipSelector
import com.steady.app.ui.components.InfoBanner
import com.steady.app.ui.components.MedicalWarningPanel
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes

@Composable
fun LogSideEffectScreen(onBack: () -> Unit, viewModel: LogSideEffectViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LogSideEffectContent(
        state = state,
        onBack = onBack,
        onToggle = viewModel::toggle,
        onSeverity = viewModel::setSeverity,
        onNote = viewModel::setNote,
        onSave = { viewModel.save(onSaved = onBack) },
    )
}

/** Stateless form, so it renders in previews without Hilt. */
@Composable
fun LogSideEffectContent(
    state: LogSideEffectUiState,
    onBack: () -> Unit,
    onToggle: (SideEffectSymptom) -> Unit,
    onSeverity: (SideEffectSeverity) -> Unit,
    onNote: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = stringResource(R.string.log_side_effect_title), onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_8),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp_24),
        ) {
            Text(
                stringResource(R.string.side_effect_intro),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 23.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Section(stringResource(R.string.side_effect_symptoms_label)) {
                ChipSelector(
                    options = state.symptoms,
                    selected = state.selected,
                    onToggle = onToggle,
                    label = { stringResource(it.labelRes()) },
                )
            }

            Section(stringResource(R.string.side_effect_severity_label)) {
                SeveritySegments(selected = state.severity, onSelect = onSeverity)
            }

            Section(
                title = stringResource(R.string.side_effect_notes_label),
                trailing = stringResource(R.string.side_effect_notes_counter, state.note.length, SIDE_EFFECT_NOTE_MAX_LENGTH),
            ) {
                NotesField(value = state.note, onValueChange = onNote)
            }

            if (state.showSevereWarning) {
                MedicalWarningPanel(stringResource(R.string.side_effect_severe_warning))
            }
            InfoBanner(stringResource(R.string.side_effect_tip))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Button(
            onClick = onSave,
            enabled = state.canSave,
            shape = SteadyShapes.button,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_14)
                .height(52.dp),
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(Dimens.dp_20))
            Spacer(Modifier.width(Dimens.dp_8))
            Text(stringResource(R.string.side_effect_save), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Section(title: String, trailing: String? = null, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_12)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                Text(
                    trailing,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                )
            }
        }
        content()
    }
}

/** Equal-width 3-way radio group in a tinted tray, per the Stitch severity control. */
@Composable
private fun SeveritySegments(selected: SideEffectSeverity?, onSelect: (SideEffectSeverity) -> Unit) {
    Surface(
        shape = SteadyShapes.card,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.dp_6)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8),
        ) {
            SideEffectSeverity.entries.forEach { severity ->
                val isSelected = severity == selected
                Surface(
                    shape = RoundedCornerShape(Dimens.dp_12),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.dp_44)
                        .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(severity) }),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(severity.labelRes()),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesField(value: String, onValueChange: (String) -> Unit) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
    Surface(
        shape = SteadyShapes.card,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
                .padding(Dimens.dp_14),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        stringResource(R.string.side_effect_notes_placeholder),
                        style = textStyle,
                        color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    )
                }
                inner()
            },
        )
    }
}

private fun SideEffectSymptom.labelRes(): Int = when (this) {
    SideEffectSymptom.NAUSEA -> R.string.symptom_nausea
    SideEffectSymptom.CONSTIPATION -> R.string.symptom_constipation
    SideEffectSymptom.FATIGUE -> R.string.symptom_fatigue
    SideEffectSymptom.HEADACHE -> R.string.symptom_headache
    SideEffectSymptom.BLOATING -> R.string.symptom_bloating
    SideEffectSymptom.INJECTION_SITE -> R.string.symptom_injection_site
    SideEffectSymptom.DIZZINESS -> R.string.symptom_dizziness
    SideEffectSymptom.APPETITE_LOSS -> R.string.symptom_appetite_loss
}

private fun SideEffectSeverity.labelRes(): Int = when (this) {
    SideEffectSeverity.MILD -> R.string.severity_mild
    SideEffectSeverity.MODERATE -> R.string.severity_moderate
    SideEffectSeverity.SEVERE -> R.string.severity_severe
}

@Preview(name = "Log side effects", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun LogSideEffectScreenPreview() {
    AppTheme(darkTheme = false) {
        LogSideEffectContent(
            state = LogSideEffectUiState(
                selected = setOf(SideEffectSymptom.NAUSEA, SideEffectSymptom.CONSTIPATION),
                severity = SideEffectSeverity.MODERATE,
            ),
            onBack = {},
            onToggle = {},
            onSeverity = {},
            onNote = {},
            onSave = {},
        )
    }
}

@Preview(name = "Log side effects - severe", showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
private fun LogSideEffectSeverePreview() {
    AppTheme(darkTheme = false) {
        LogSideEffectContent(
            state = LogSideEffectUiState(
                selected = setOf(SideEffectSymptom.DIZZINESS),
                severity = SideEffectSeverity.SEVERE,
                note = "Started after lunch",
            ),
            onBack = {},
            onToggle = {},
            onSeverity = {},
            onNote = {},
            onSave = {},
        )
    }
}
