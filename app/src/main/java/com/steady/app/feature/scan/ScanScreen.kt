package com.steady.app.feature.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.steady.app.R
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.SteadyInverseSurface
import com.steady.app.ui.theme.SteadyPrimary
import com.steady.app.ui.theme.SteadySecondary
import kotlinx.coroutines.launch

private val ReadyGreen = Color(0xFF34D399)
private val GlassFill = Color.White.copy(alpha = 0.10f)
private val GlassBorder = Color.White.copy(alpha = 0.15f)

@Composable
fun ScanScreen(onBack: () -> Unit, onMealCaptured: (photoUri: String) -> Unit) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var hasPermission by remember {
        mutableStateOf(isPreview || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var deniedOnce by rememberSaveable { mutableStateOf(false) }
    // Re-check on resume: the user may have granted access in system Settings and come back.
    LifecycleResumeEffect(Unit) {
        if (!isPreview) {
            hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }
        onPauseOrDispose { }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
        deniedOnce = !granted
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let { onMealCaptured(it.toString()) }
    }

    DarkSystemBars()

    ScanContent(
        hasPermission = hasPermission,
        deniedOnce = deniedOnce,
        onBack = onBack,
        onRequestPermission = {
            if (deniedOnce) {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                )
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onOpenGallery = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onMealCaptured = onMealCaptured,
    )
}

/** The camera screen is always dark, so system bar icons must be light while it's shown. */
@Composable
private fun DarkSystemBars() {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose { }
        val controller = WindowCompat.getInsetsController(window, view)
        val previousStatus = controller.isAppearanceLightStatusBars
        val previousNav = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = previousStatus
            controller.isAppearanceLightNavigationBars = previousNav
        }
    }
}

@Composable
private fun ScanContent(
    hasPermission: Boolean,
    deniedOnce: Boolean,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenGallery: () -> Unit,
    onMealCaptured: (String) -> Unit,
) {
    val isPreview = LocalInspectionMode.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val comingSoon = stringResource(R.string.scan_mode_coming_soon)
    val captureFailed = stringResource(R.string.scan_capture_failed)
    val showMessage: (String) -> Unit = { message -> scope.launch { snackbarHost.showSnackbar(message) } }

    var flashOn by rememberSaveable { mutableStateOf(false) }
    var capturing by remember { mutableStateOf(false) }
    val camera = if (hasPermission && !isPreview) rememberMealCameraState() else null

    Box(
        Modifier
            .fillMaxSize()
            .background(SteadyInverseSurface),
    ) {
        if (camera != null) {
            MealCameraPreview(camera, Modifier.fillMaxSize())
            // Soft scrim so white UI stays legible over bright food photos.
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))
        }

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassButton(Icons.Default.ChevronLeft, stringResource(R.string.common_back), onClick = onBack)
                Text(
                    stringResource(R.string.scan_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                GlassButton(
                    icon = if (flashOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                    contentDescription = stringResource(R.string.scan_toggle_flash),
                    enabled = hasPermission,
                    onClick = {
                        flashOn = !flashOn
                        camera?.setFlash(flashOn)
                    },
                )
            }
            Surface(
                shape = CircleShape,
                color = GlassFill,
                border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.10f)),
                modifier = Modifier.padding(top = Dimens.dp_10),
            ) {
                Text(
                    "✨ " + stringResource(R.string.scan_ai_active),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_4),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (hasPermission) {
                    Viewfinder(ready = camera != null || isPreview)
                    Text(
                        stringResource(R.string.scan_center_plate),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = Dimens.dp_20),
                    )
                } else {
                    PermissionPrompt(deniedOnce = deniedOnce, onRequest = onRequestPermission)
                }
            }

            ModeSwitcher(onUnavailableMode = { showMessage(comingSoon) })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_32, vertical = Dimens.dp_24),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassButton(
                    Icons.Outlined.PhotoLibrary,
                    stringResource(R.string.scan_open_gallery),
                    size = Dimens.dp_48,
                    onClick = onOpenGallery,
                )
                ShutterButton(
                    enabled = camera != null && !capturing,
                    onClick = {
                        val cam = camera ?: return@ShutterButton
                        capturing = true
                        cam.capture { uri ->
                            capturing = false
                            if (uri != null) onMealCaptured(uri.toString()) else showMessage(captureFailed)
                        }
                    },
                )
                GlassButton(
                    Icons.AutoMirrored.Outlined.Notes,
                    stringResource(R.string.scan_manual_entry),
                    size = Dimens.dp_48,
                    onClick = { showMessage(comingSoon) },
                )
            }
            Text(
                stringResource(R.string.scan_tip),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = Dimens.dp_16),
            )
        }

        SnackbarHost(
            snackbarHost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 140.dp),
        )
    }
}

@Composable
private fun GlassButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = Dimens.dp_38,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(GlassFill)
            .border(Dimens.dp_1, GlassBorder, CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = if (enabled) 0.9f else 0.4f), modifier = Modifier.size(Dimens.dp_22))
    }
}

/** 290dp frame: white L-corners, dashed plate circle with crosshair, faint guide lines, "Ready" chip. */
@Composable
private fun Viewfinder(ready: Boolean) {
    Box(Modifier.size(290.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val arm = 24.dp.toPx()
            val stroke = 3.dp.toPx()
            val w = size.width
            val h = size.height
            val white = Color.White
            fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                drawLine(white, Offset(x, y), Offset(x + dx * arm, y), stroke, cap = StrokeCap.Square)
                drawLine(white, Offset(x, y), Offset(x, y + dy * arm), stroke, cap = StrokeCap.Square)
            }
            val half = stroke / 2
            corner(half, half, 1f, 1f)
            corner(w - half, half, -1f, 1f)
            corner(half, h - half, 1f, -1f)
            corner(w - half, h - half, -1f, -1f)

            val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
            val inset = 16.dp.toPx()
            drawCircle(
                Color.White.copy(alpha = 0.15f),
                radius = w / 2 - inset,
                style = Stroke(width = 1.dp.toPx(), pathEffect = dash),
            )
            val guide = 32.dp.toPx()
            listOf(guide, h - guide).forEach { y ->
                drawLine(Color.White.copy(alpha = 0.08f), Offset(guide, y), Offset(w - guide, y), 1.dp.toPx(), pathEffect = dash)
            }
            val cross = 5.dp.toPx()
            val c = Offset(w / 2, h / 2)
            drawLine(Color.White.copy(alpha = 0.2f), c.copy(x = c.x - cross), c.copy(x = c.x + cross), 1.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.2f), c.copy(y = c.y - cross), c.copy(y = c.y + cross), 1.dp.toPx())
        }
        if (ready) ReadyChip()
    }
}

@Composable
private fun BoxScope.ReadyChip() {
    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(Dimens.dp_24)
            .clip(RoundedCornerShape(Dimens.dp_4))
            .background(SteadyPrimary.copy(alpha = 0.8f))
            .border(Dimens.dp_1, Color(0xFFA3D4C3).copy(alpha = 0.3f), RoundedCornerShape(Dimens.dp_4))
            .padding(horizontal = Dimens.dp_10, vertical = Dimens.dp_4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(Dimens.dp_6)
                .background(ReadyGreen, CircleShape),
        )
        Spacer(Modifier.width(Dimens.dp_6))
        Text(
            stringResource(R.string.scan_ready),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

@Composable
private fun PermissionPrompt(deniedOnce: Boolean, onRequest: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = Dimens.dp_32),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.scan_permission_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(if (deniedOnce) R.string.scan_permission_denied_body else R.string.scan_permission_body),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.dp_8, bottom = Dimens.dp_20),
        )
        Button(
            onClick = onRequest,
            colors = ButtonDefaults.buttonColors(containerColor = SteadySecondary, contentColor = Color.White),
        ) {
            Text(stringResource(if (deniedOnce) R.string.scan_open_settings else R.string.scan_allow_camera))
        }
    }
}

@Composable
private fun ModeSwitcher(onUnavailableMode: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp_32),
        verticalAlignment = Alignment.Top,
    ) {
        ModeLabel(stringResource(R.string.scan_mode_barcode), active = false, onClick = onUnavailableMode)
        ModeLabel(stringResource(R.string.scan_mode_meal), active = true, onClick = {})
        ModeLabel(stringResource(R.string.scan_mode_quick_add), active = false, onClick = onUnavailableMode)
    }
}

@Composable
private fun ModeLabel(label: String, active: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 1.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
            ),
            color = if (active) Color.White else Color.White.copy(alpha = 0.5f),
        )
        Spacer(Modifier.height(Dimens.dp_6))
        Box(
            Modifier
                .size(Dimens.dp_6)
                .background(if (active) SteadySecondary else Color.Transparent, CircleShape),
        )
    }
}

/** 76dp white ring with a 56dp terracotta core, per the design's shutter. */
@Composable
private fun ShutterButton(enabled: Boolean, onClick: () -> Unit) {
    val label = stringResource(R.string.scan_capture)
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White.copy(alpha = if (enabled) 1f else 0.4f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .background(SteadySecondary.copy(alpha = if (enabled) 1f else 0.5f), CircleShape),
        )
    }
}

@Preview(name = "Scan a meal", widthDp = 390, heightDp = 844)
@Composable
private fun ScanScreenPreview() {
    AppTheme(darkTheme = false) {
        ScanContent(
            hasPermission = true,
            deniedOnce = false,
            onBack = {},
            onRequestPermission = {},
            onOpenGallery = {},
            onMealCaptured = {},
        )
    }
}

@Preview(name = "Scan a meal - no permission", widthDp = 390, heightDp = 844)
@Composable
private fun ScanPermissionPreview() {
    AppTheme(darkTheme = false) {
        ScanContent(
            hasPermission = false,
            deniedOnce = true,
            onBack = {},
            onRequestPermission = {},
            onOpenGallery = {},
            onMealCaptured = {},
        )
    }
}
