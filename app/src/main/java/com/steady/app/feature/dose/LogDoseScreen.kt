package com.steady.app.feature.dose

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.InjectionSite
import com.steady.app.domain.model.MedicationFrequency
import com.steady.app.domain.model.MedicationProfile
import com.steady.app.domain.model.MedicationRoute
import com.steady.app.domain.model.localEpochDay
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun LogDoseScreen(
    onBack: () -> Unit,
    onSetUpMedication: () -> Unit,
    viewModel: LogDoseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LogDoseContent(
        state = state,
        onBack = onBack,
        onSetUpMedication = onSetUpMedication,
        onSite = viewModel::setSite,
        onDate = viewModel::setDate,
        onTime = viewModel::setTime,
        onNote = viewModel::setNote,
        onTag = viewModel::appendTag,
        onSave = { viewModel.save(onSaved = onBack) },
    )
}

/** Stateless form, so it renders in previews without Hilt. */
@Composable
fun LogDoseContent(
    state: LogDoseUiState,
    onBack: () -> Unit,
    onSetUpMedication: () -> Unit,
    onSite: (InjectionSite) -> Unit,
    onDate: (Long) -> Unit,
    onTime: (Int, Int) -> Unit,
    onNote: (String) -> Unit,
    onTag: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = stringResource(R.string.log_dose_title), onBack = onBack)
        if (state.isLoading) return@Column
        val medication = state.medication
        if (medication == null) {
            NoMedication(onSetUpMedication)
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_8),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp_24),
        ) {
            MedicationSummary(medication)
            if (state.isInjection) {
                Section(stringResource(R.string.dose_injection_site_label)) {
                    InjectionSitePicker(selected = state.site, onSelect = onSite)
                }
            }
            Section(stringResource(R.string.dose_date_time_label)) {
                DateTimeRow(takenAtMillis = state.takenAtMillis, onDate = onDate, onTime = onTime)
            }
            Section(
                title = stringResource(R.string.dose_notes_label),
                trailing = stringResource(R.string.side_effect_notes_counter, state.note.length, DOSE_NOTE_MAX_LENGTH),
            ) {
                NotesWithTags(value = state.note, onValueChange = onNote, onTag = onTag)
            }
            if (state.isInjection) RotationTip()
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
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(Dimens.dp_8))
            Text(stringResource(R.string.dose_save), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun NoMedication(onSetUpMedication: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp_24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.home_med_setup_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Dimens.dp_16))
        AppButton(text = stringResource(R.string.home_med_setup_title), onClick = onSetUpMedication)
    }
}

@Composable
private fun MedicationSummary(medication: MedicationProfile) {
    val extended = LocalSteadyExtendedColors.current
    val isInjection = medication.route == MedicationRoute.INJECTION
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.dp_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconTile(
                icon = if (isInjection) Icons.Filled.Vaccines else Icons.Filled.Medication,
                size = Dimens.dp_44,
                shape = RoundedCornerShape(Dimens.dp_12),
                containerColor = extended.surfaceContainerGreen,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = Dimens.dp_22,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.dp_14),
            ) {
                Text(
                    medication.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 19.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                val doseText = "${formatDoseAmount(medication.doseAmount)}${medication.doseUnit}"
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.dose_current_dose_prefix))
                        append(' ')
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)) {
                            append(doseText)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_13),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                shape = SteadyShapes.chip,
                color = extended.surfaceContainerGreen,
                border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
            ) {
                Text(
                    stringResource(if (isInjection) R.string.medication_dose_route_injection else R.string.medication_dose_route_oral),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = Dimens.dp_10, vertical = Dimens.dp_4),
                )
            }
        }
    }
}

/** 5.0 → "5", 2.5 → "2.5": doses read naturally without a trailing ".0". */
internal fun formatDoseAmount(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()

@Composable
private fun Section(title: String, trailing: String? = null, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                Text(trailing, style = MaterialTheme.typography.labelSmall, color = LocalSteadyExtendedColors.current.onSurfaceMuted)
            }
        }
        content()
    }
}

@Composable
private fun InjectionSitePicker(selected: InjectionSite?, onSelect: (InjectionSite) -> Unit) {
    Row(
        modifier = Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8),
    ) {
        InjectionSite.entries.forEach { site ->
            val isSelected = site == selected
            Surface(
                shape = SteadyShapes.chip,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null else BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.dp_44)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(site) }),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(site.labelRes()),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = Dimens.sp_13, fontWeight = FontWeight.Medium),
                        color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private fun InjectionSite.labelRes(): Int = when (this) {
    InjectionSite.ABDOMEN -> R.string.dose_site_abdomen
    InjectionSite.THIGH -> R.string.dose_site_thigh
    InjectionSite.ARM -> R.string.dose_site_arm
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeRow(takenAtMillis: Long, onDate: (Long) -> Unit, onTime: (Int, Int) -> Unit) {
    val context = LocalContext.current
    var showDate by rememberSaveable { mutableStateOf(false) }
    var showTime by rememberSaveable { mutableStateOf(false) }
    val local = remember(takenAtMillis) { Calendar.getInstance().apply { timeInMillis = takenAtMillis } }

    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_12)) {
        PickerCard(
            icon = Icons.Outlined.CalendarToday,
            label = stringResource(R.string.dose_date_label),
            value = formatDoseDate(takenAtMillis, stringResource(R.string.dose_today_prefix)),
            onClick = { showDate = true },
        )
        PickerCard(
            icon = Icons.Outlined.Schedule,
            label = stringResource(R.string.dose_time_label),
            value = DateFormat.getTimeFormat(context).format(Date(takenAtMillis)),
            onClick = { showTime = true },
        )
    }

    if (showDate) {
        val todayUtcEnd = remember { localDayAsUtcMidnight(System.currentTimeMillis()) }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = localDayAsUtcMidnight(takenAtMillis),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayUtcEnd
            },
        )
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let(onDate)
                    showDate = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text(stringResource(android.R.string.cancel)) } },
        ) { DatePicker(state = pickerState) }
    }

    if (showTime) {
        val timeState = rememberTimePickerState(
            initialHour = local[Calendar.HOUR_OF_DAY],
            initialMinute = local[Calendar.MINUTE],
            is24Hour = DateFormat.is24HourFormat(context),
        )
        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    onTime(timeState.hour, timeState.minute)
                    showTime = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text(stringResource(android.R.string.cancel)) } },
            text = { TimePicker(state = timeState) },
        )
    }
}

/** Material's DatePicker speaks UTC midnights; map the local calendar day to that representation. */
private fun localDayAsUtcMidnight(millis: Long): Long = localEpochDay(millis) * 86_400_000L

private fun formatDoseDate(millis: Long, todayPrefix: String): String {
    val locale = Locale.getDefault()
    val monthDay = java.text.SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMMd"), locale).format(Date(millis))
    return if (localEpochDay(millis) == localEpochDay(System.currentTimeMillis())) "$todayPrefix, $monthDay" else monthDay
}

@Composable
private fun RowScope.PickerCard(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.dp_14),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
        modifier = Modifier.weight(1f),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.dp_12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.dp_20))
            Column(Modifier.padding(start = Dimens.dp_10)) {
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = Dimens.sp_10, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun NotesWithTags(value: String, onValueChange: (String) -> Unit, onTag: (String) -> Unit) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, lineHeight = 22.sp)
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    val tags = listOf(stringResource(R.string.dose_tag_no_nausea), stringResource(R.string.dose_tag_well_hydrated))
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.dp_14)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(stringResource(R.string.dose_notes_placeholder), style = textStyle, color = muted)
                    }
                    inner()
                },
            )
            HorizontalDivider(color = LocalSteadyExtendedColors.current.outlineSubtle, modifier = Modifier.padding(vertical = Dimens.dp_8))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8)) {
                tags.forEach { tag ->
                    Surface(
                        shape = SteadyShapes.chip,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { onTag(tag) },
                    ) {
                        Text(
                            tag,
                            style = MaterialTheme.typography.bodySmall,
                            color = muted,
                            modifier = Modifier.padding(horizontal = Dimens.dp_10, vertical = Dimens.dp_4),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RotationTip() {
    Surface(
        shape = RoundedCornerShape(Dimens.dp_12),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(Dimens.dp_1, LocalSteadyExtendedColors.current.outlineSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(Dimens.dp_12), verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Outlined.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = Dimens.dp_2)
                    .size(Dimens.dp_18),
            )
            Text(
                stringResource(R.string.dose_rotation_tip),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_12, lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Dimens.dp_10),
            )
        }
    }
}

private val previewMedication = MedicationProfile(
    userId = "preview",
    route = MedicationRoute.INJECTION,
    name = "Tirzepatide",
    doseAmount = 5.0,
    doseUnit = "mg",
    frequency = MedicationFrequency.WEEKLY,
    updatedAtEpochMillis = 0L,
)

@Preview(name = "Log dose - injection", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun LogDoseScreenPreview() {
    AppTheme(darkTheme = false) {
        LogDoseContent(
            state = LogDoseUiState(
                isLoading = false,
                medication = previewMedication,
                site = InjectionSite.ABDOMEN,
                takenAtMillis = System.currentTimeMillis(),
            ),
            onBack = {},
            onSetUpMedication = {},
            onSite = {},
            onDate = {},
            onTime = { _, _ -> },
            onNote = {},
            onTag = {},
            onSave = {},
        )
    }
}

@Preview(name = "Log dose - oral", showBackground = true, widthDp = 390, heightDp = 700)
@Composable
private fun LogDoseOralPreview() {
    AppTheme(darkTheme = false) {
        LogDoseContent(
            state = LogDoseUiState(
                isLoading = false,
                medication = previewMedication.copy(name = "Semaglutide", route = MedicationRoute.ORAL, doseAmount = 7.0, frequency = MedicationFrequency.DAILY),
                takenAtMillis = System.currentTimeMillis(),
            ),
            onBack = {},
            onSetUpMedication = {},
            onSite = {},
            onDate = {},
            onTime = { _, _ -> },
            onNote = {},
            onTag = {},
            onSave = {},
        )
    }
}
