package com.steady.app.feature.medication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.feature.dose.formatDoseAmount
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.LocalSteadyExtendedColors

@Composable
fun MedicationDoseScreen(onBack: () -> Unit, viewModel: MedicationDoseViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MedicationDoseLayout(
        title = stringResource(R.string.medication_dose_title),
        onBack = onBack,
        ctaText = stringResource(R.string.medication_dose_save),
        state = state,
        actions = viewModel.formActions(),
        onCta = { viewModel.save(onBack) },
    )
}

data class MedicationFormActions(
    val onRouteChange: (MedicationRoute) -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onDoseAmountChange: (String) -> Unit = {},
    val onFrequencyChange: (MedicationFrequency) -> Unit = {},
)

internal fun MedicationDoseViewModel.formActions() = MedicationFormActions(
    onRouteChange = ::setRoute,
    onNameChange = ::setName,
    onDoseAmountChange = ::setDoseAmount,
    onFrequencyChange = ::setFrequency,
)

/** Shared by the settings screen and first-run setup: top bar, scrolling form, pinned CTA. */
@Composable
internal fun MedicationDoseLayout(
    title: String,
    onBack: (() -> Unit)?,
    ctaText: String,
    state: MedicationDoseUiState,
    actions: MedicationFormActions,
    onCta: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = title, onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
        ) {
            if (!state.isLoading) MedicationDoseForm(state, actions)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Button(
            onClick = onCta,
            enabled = state.canSave,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(ctaText, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

private enum class PickerSheet { MEDICATION, DOSE }

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MedicationDoseForm(state: MedicationDoseUiState, actions: MedicationFormActions) {
    var sheet by rememberSaveable { mutableStateOf<PickerSheet?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionLabel(stringResource(R.string.medication_dose_how_taken))
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteOption(
                    icon = Icons.Outlined.Vaccines,
                    label = stringResource(R.string.medication_dose_route_injection),
                    selected = state.route == MedicationRoute.INJECTION,
                    onClick = { actions.onRouteChange(MedicationRoute.INJECTION) },
                    modifier = Modifier.weight(1f),
                )
                RouteOption(
                    icon = Icons.Outlined.Medication,
                    label = stringResource(R.string.medication_dose_route_oral),
                    selected = state.route == MedicationRoute.ORAL,
                    onClick = { actions.onRouteChange(MedicationRoute.ORAL) },
                    modifier = Modifier.weight(1f),
                )
            }
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(15.dp),
                )
                Text(
                    stringResource(R.string.medication_dose_route_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel(stringResource(R.string.medication_dose_name_label))
            PickerRow(
                value = state.name.ifBlank { stringResource(R.string.medication_dose_name_placeholder) },
                isPlaceholder = state.name.isBlank(),
                onClick = { sheet = PickerSheet.MEDICATION },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel(stringResource(R.string.medication_dose_amount_label))
            val dose = parseDoseAmount(state.doseAmount)
            PickerRow(
                value = dose?.let { "${formatDoseAmount(it)} ${state.doseUnit}" }
                    ?: stringResource(R.string.medication_dose_amount_placeholder),
                isPlaceholder = dose == null,
                onClick = { sheet = PickerSheet.DOSE },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionLabel(stringResource(R.string.medication_dose_frequency_label))
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FrequencyPill(
                    stringResource(R.string.medication_dose_frequency_weekly),
                    state.frequency == MedicationFrequency.WEEKLY,
                ) { actions.onFrequencyChange(MedicationFrequency.WEEKLY) }
                FrequencyPill(
                    stringResource(R.string.medication_dose_frequency_daily),
                    state.frequency == MedicationFrequency.DAILY,
                ) { actions.onFrequencyChange(MedicationFrequency.DAILY) }
            }
        }

        TipBanner()
    }

    when (sheet) {
        PickerSheet.MEDICATION -> MedicationSheet(
            current = state.name,
            onSelect = {
                actions.onNameChange(it)
                sheet = null
            },
            onDismiss = { sheet = null },
        )
        PickerSheet.DOSE -> DoseSheet(
            strengths = commonStrengths(state.name, state.route),
            current = state.doseAmount,
            unit = state.doseUnit,
            onSelect = {
                actions.onDoseAmountChange(it)
                sheet = null
            },
            onDismiss = { sheet = null },
        )
        null -> Unit
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun RouteOption(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val dark = LocalSteadyExtendedColors.current.inverseSurface
    val content = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) dark else MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) dark else MaterialTheme.colorScheme.outline),
        modifier = modifier
            .height(76.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) content else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
            Text(
                label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium),
                color = content,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun PickerRow(value: String, isPlaceholder: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = if (isPlaceholder) LocalSteadyExtendedColors.current.onSurfaceMuted else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LocalSteadyExtendedColors.current.onSurfaceMuted,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun FrequencyPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val dark = LocalSteadyExtendedColors.current.inverseSurface
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) dark else MaterialTheme.colorScheme.surface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium),
            color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun TipBanner() {
    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LocalSteadyExtendedColors.current.surfaceContainerGreen,
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            IconTile(
                icon = Icons.Outlined.Spa,
                size = 28.dp,
                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = 17.dp,
            )
            Text(
                stringResource(R.string.medication_dose_tip),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationSheet(current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val isCustom = current.isNotBlank() && commonMedications.none { it.equals(current.trim(), ignoreCase = true) }
    var other by rememberSaveable { mutableStateOf(if (isCustom) current else "") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        ) {
            Text(
                stringResource(R.string.medication_dose_name_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            commonMedications.forEach { name ->
                val selected = name.equals(current.trim(), ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(selected = selected, role = Role.RadioButton, onClick = { onSelect(name) })
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    if (selected) Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
            OutlinedTextField(
                value = other,
                onValueChange = { other = it },
                label = { Text(stringResource(R.string.medication_dose_other_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Button(
                onClick = { onSelect(other.trim()) },
                enabled = other.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
            ) {
                Text(stringResource(R.string.medication_dose_use_this))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DoseSheet(strengths: List<Double>, current: String, unit: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    var typed by rememberSaveable { mutableStateOf(current) }
    val parsed = parseDoseAmount(typed)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.medication_dose_amount_label), style = MaterialTheme.typography.headlineSmall)
            if (strengths.isNotEmpty()) {
                Text(
                    stringResource(R.string.medication_dose_common_strengths),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    strengths.forEach { strength ->
                        FrequencyPill(
                            label = "${formatDoseAmount(strength)} $unit",
                            selected = parsed == strength,
                            onClick = { onSelect(formatDoseAmount(strength)) },
                        )
                    }
                }
            }
            OutlinedTextField(
                value = typed,
                onValueChange = { typed = it },
                label = { Text(stringResource(R.string.medication_dose_amount_label)) },
                suffix = { Text(unit) },
                singleLine = true,
                isError = typed.isNotBlank() && parsed == null,
                supportingText = if (typed.isNotBlank() && parsed == null) {
                    { Text(stringResource(R.string.medication_dose_amount_invalid)) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { parsed?.let { onSelect(formatDoseAmount(it)) } },
                enabled = parsed != null,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(stringResource(R.string.medication_dose_use_this))
            }
        }
    }
}

@Preview(name = "Medication & dose", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MedicationDosePreview() {
    AppTheme(darkTheme = false) {
        MedicationDoseLayout(
            title = "Medication & dose",
            onBack = {},
            ctaText = "Save",
            state = MedicationDoseUiState(name = "Tirzepatide", doseAmount = "5", isLoading = false),
            actions = MedicationFormActions(),
            onCta = {},
        )
    }
}
