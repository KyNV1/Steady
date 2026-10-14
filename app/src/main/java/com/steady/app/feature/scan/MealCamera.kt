package com.steady.app.feature.scan

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import timber.log.Timber

/** Holds the CameraX controller bound to the composition's lifecycle; capture/flash go through here. */
class MealCameraState(private val context: Context) {
    internal val controller = LifecycleCameraController(context).apply {
        setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
        imageCaptureFlashMode = ImageCapture.FLASH_MODE_OFF
    }

    fun setFlash(on: Boolean) {
        controller.imageCaptureFlashMode = if (on) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    /** Captures to app cache (never the gallery) and reports the file's Uri, or null on failure. */
    fun capture(onResult: (Uri?) -> Unit) {
        val dir = File(context.cacheDir, SCAN_CACHE_DIR).apply { mkdirs() }
        val file = File(dir, "meal_${System.currentTimeMillis()}.jpg")
        controller.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) = onResult(Uri.fromFile(file))
                override fun onError(exception: ImageCaptureException) {
                    Timber.w(exception, "Meal photo capture failed")
                    file.delete()
                    onResult(null)
                }
            },
        )
    }

    companion object {
        const val SCAN_CACHE_DIR = "meal_scans"
    }
}

@Composable
fun rememberMealCameraState(): MealCameraState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { MealCameraState(context) }
    DisposableEffect(lifecycleOwner) {
        state.controller.bindToLifecycle(lifecycleOwner)
        onDispose { state.controller.unbind() }
    }
    return state
}

@Composable
fun MealCameraPreview(state: MealCameraState, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                controller = state.controller
            }
        },
    )
}
