package com.steady.app.feature.mealresult

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.steady.app.R
import com.steady.app.domain.model.MealScanFailure
import com.steady.app.domain.model.MealScanResult
import com.steady.app.domain.model.NutritionEstimate
import com.steady.app.ui.components.AppTextField
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.InfoBanner
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyInverseSurface
import com.steady.app.ui.theme.SteadyShapes

@Composable
fun MealDetectedScreen(
    onBack: () -> Unit,
    onRetake: () -> Unit,
    onLogged: () -> Unit,
    viewModel: MealDetectedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MealDetectedContent(
        state = state,
        onBack = onBack,
        onRetake = onRetake,
        onRetry = viewModel::analyze,
        onEdit = viewModel::startEditing,
        onDoneEditing = viewModel::finishEditing,
        onDraftChange = viewModel::updateDraft,
        onUseSample = viewModel::useSampleResult,
        onAddToLog = { viewModel.save(onSaved = onLogged) },
    )
}

/** Stateless layout, so it renders in previews without Hilt. */
@Composable
fun MealDetectedContent(
    state: MealDetectedUiState,
    onBack: () -> Unit,
    onRetake: () -> Unit,
    onRetry: () -> Unit,
    onEdit: () -> Unit,
    onDoneEditing: () -> Unit,
    onDraftChange: ((MealDraft) -> MealDraft) -> Unit,
    onUseSample: () -> Unit,
    onAddToLog: () -> Unit,
) {
    val analysis = state.analysis
    val draft = state.draft
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = stringResource(R.string.meal_detected_title), onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_8),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp_20),
        ) {
            MealPhoto(
                photoUri = state.photoUri,
                isAnalyzing = analysis is MealAnalysis.Analyzing,
                confidencePercent = state.confidencePercent,
            )

            when {
                state.isEditing && draft != null -> MealEditor(draft, onDraftChange)
                draft != null -> {
                    MealSummary(draft)
                    Text(
                        stringResource(R.string.meal_estimate_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    )
                    (analysis as? MealAnalysis.Detected)?.result?.glp1Tip?.let { InfoBanner(it) }
                }
                analysis is MealAnalysis.Analyzing -> Text(
                    stringResource(R.string.meal_analyzing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                analysis is MealAnalysis.Failed -> AnalysisFailed(
                    reason = analysis.reason,
                    canUseSample = state.canUseSample,
                    onEnterManually = onEdit,
                    onRetry = onRetry,
                    onUseSample = onUseSample,
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Column(
            modifier = Modifier.padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_14),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onAddToLog,
                enabled = state.canSave,
                shape = SteadyShapes.button,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(Dimens.dp_8))
                Text(stringResource(R.string.meal_add_to_log), style = MaterialTheme.typography.labelLarge)
            }
            Row(
                modifier = Modifier.padding(top = Dimens.dp_4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (draft != null) {
                    SecondaryAction(
                        icon = Icons.Outlined.Tune,
                        label = stringResource(if (state.isEditing) R.string.meal_done_editing else R.string.meal_edit_details),
                        onClick = if (state.isEditing) onDoneEditing else onEdit,
                    )
                    Text("•", color = MaterialTheme.colorScheme.outlineVariant, fontSize = 12.sp)
                }
                SecondaryAction(icon = Icons.Outlined.PhotoCamera, label = stringResource(R.string.meal_retake), onClick = onRetake)
            }
        }
    }
}

@Composable
private fun MealPhoto(photoUri: String, isAnalyzing: Boolean, confidencePercent: Int?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(Dimens.dp_20))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(Dimens.dp_1, MaterialTheme.colorScheme.outline, RoundedCornerShape(Dimens.dp_20)),
    ) {
        if (!LocalInspectionMode.current) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (isAnalyzing) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            }
        }
        if (confidencePercent != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Dimens.dp_12)
                    .clip(CircleShape)
                    .background(SteadyInverseSurface.copy(alpha = 0.8f))
                    .border(Dimens.dp_1, Color.White.copy(alpha = 0.1f), CircleShape)
                    .padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFA3D4C3), modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(Dimens.dp_6))
                Text(
                    stringResource(R.string.meal_match_percent, confidencePercent),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun MealSummary(draft: MealDraft) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_4)) {
        Text(
            draft.name.ifBlank { stringResource(R.string.meal_unnamed) },
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = Dimens.sp_24, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (draft.portion.isNotBlank()) {
            Text(
                draft.portion,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
            MacroTile(draft.calories, stringResource(R.string.macro_calories), emphasized = false)
            MacroTile("${draft.protein}g", stringResource(R.string.macro_protein), emphasized = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
            MacroTile("${draft.fiber}g", stringResource(R.string.macro_fiber), emphasized = false)
            MacroTile("${draft.carbs}g", stringResource(R.string.macro_carbs), emphasized = false)
        }
    }
}

/** Protein is tinted primary: it's the macro GLP-1 users are coached to protect. */
@Composable
private fun RowScope.MacroTile(value: String, label: String, emphasized: Boolean) {
    SteadyCard(modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(Dimens.dp_14)) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
                color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.dp_4),
            )
        }
    }
}

@Composable
private fun MealEditor(draft: MealDraft, onDraftChange: ((MealDraft) -> MealDraft) -> Unit) {
    val number = KeyboardOptions(keyboardType = KeyboardType.Number)
    val decimal = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
        AppTextField(value = draft.name, onValueChange = { v -> onDraftChange { it.copy(name = v) } }, label = stringResource(R.string.meal_field_name))
        AppTextField(value = draft.portion, onValueChange = { v -> onDraftChange { it.copy(portion = v) } }, label = stringResource(R.string.meal_field_portion))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
            AppTextField(
                value = draft.calories,
                onValueChange = { v -> onDraftChange { it.copy(calories = v) } },
                label = stringResource(R.string.macro_calories),
                keyboardOptions = number,
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                value = draft.protein,
                onValueChange = { v -> onDraftChange { it.copy(protein = v) } },
                label = stringResource(R.string.meal_field_grams, stringResource(R.string.macro_protein)),
                keyboardOptions = decimal,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
            AppTextField(
                value = draft.fiber,
                onValueChange = { v -> onDraftChange { it.copy(fiber = v) } },
                label = stringResource(R.string.meal_field_grams, stringResource(R.string.macro_fiber)),
                keyboardOptions = decimal,
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                value = draft.carbs,
                onValueChange = { v -> onDraftChange { it.copy(carbs = v) } },
                label = stringResource(R.string.meal_field_grams, stringResource(R.string.macro_carbs)),
                keyboardOptions = decimal,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AnalysisFailed(
    reason: MealScanFailure,
    canUseSample: Boolean,
    onEnterManually: () -> Unit,
    onRetry: () -> Unit,
    onUseSample: () -> Unit,
) {
    val message = when (reason) {
        MealScanFailure.NetworkUnavailable -> R.string.meal_error_network
        MealScanFailure.QuotaExceeded -> R.string.meal_error_quota
        MealScanFailure.InvalidImage -> R.string.meal_error_image
        MealScanFailure.NoFoodDetected -> R.string.meal_error_no_food
        MealScanFailure.NotAuthenticated -> R.string.meal_error_auth
        MealScanFailure.InvalidResponse, MealScanFailure.ServiceUnavailable -> R.string.meal_error_unavailable
    }
    val retryable = reason == MealScanFailure.NetworkUnavailable || reason == MealScanFailure.InvalidResponse ||
        reason == MealScanFailure.ServiceUnavailable
    Surface(
        shape = SteadyShapes.card,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
    ) {
        Column(Modifier.padding(Dimens.dp_16)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Text(
                    stringResource(message),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = Dimens.dp_10),
                )
            }
            Row(
                modifier = Modifier.padding(top = Dimens.dp_8),
                horizontalArrangement = Arrangement.spacedBy(Dimens.dp_4),
            ) {
                TextButton(onClick = onEnterManually) { Text(stringResource(R.string.meal_enter_manually)) }
                if (retryable) TextButton(onClick = onRetry) { Text(stringResource(R.string.meal_try_again)) }
            }
            if (canUseSample) {
                TextButton(onClick = onUseSample) {
                    Text(
                        stringResource(R.string.meal_use_sample),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SecondaryAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.dp_16))
        Spacer(Modifier.width(Dimens.dp_4))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private val previewResult = MealScanResult(
    schemaVersion = 1,
    model = "preview",
    mealName = "Grilled chicken salad bowl",
    portionDescription = "Mixed greens, grilled chicken, avocado, vinaigrette",
    confidencePercent = 96,
    nutrition = NutritionEstimate(calories = 420, proteinGrams = 34.0, fiberGrams = 6.0, carbsGrams = 18.0),
    glp1Tip = "High protein, gentle on digestion — a solid pick while on tirzepatide.",
)

@Composable
private fun PreviewHost(state: MealDetectedUiState) {
    AppTheme(darkTheme = false) {
        MealDetectedContent(state, {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(name = "Meal detected", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun MealDetectedPreview() {
    PreviewHost(
        MealDetectedUiState(
            photoUri = "",
            analysis = MealAnalysis.Detected(previewResult),
            draft = MealDraft.from(previewResult),
        ),
    )
}

@Preview(name = "Meal detected - analysis unavailable", showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun MealDetectedFailedPreview() {
    PreviewHost(
        MealDetectedUiState(photoUri = "", analysis = MealAnalysis.Failed(MealScanFailure.ServiceUnavailable), canUseSample = true),
    )
}
