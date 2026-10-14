package com.steady.app.feature.mealresult

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MAX_EDGE_PX = 1280
private const val JPEG_QUALITY = 80

/**
 * Turns a camera/picker photo into an upload-ready JPEG: upright, at most [MAX_EDGE_PX] on the
 * long edge, re-encoded so EXIF (GPS, device info) is stripped before it leaves the phone.
 */
@Singleton
class MealPhotoEncoder @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun encode(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val longEdge = maxOf(bounds.outWidth, bounds.outHeight)
            if (longEdge <= 0) return@runCatching null

            var sample = 1
            while (longEdge / (sample * 2) >= MAX_EDGE_PX) sample *= 2
            val decoded = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
            } ?: return@runCatching null

            val upright = rotateUpright(decoded, readRotation(uri))
            val scale = MAX_EDGE_PX.toFloat() / maxOf(upright.width, upright.height)
            val sized = if (scale < 1f) {
                upright.scale((upright.width * scale).toInt(), (upright.height * scale).toInt())
            } else {
                upright
            }
            ByteArrayOutputStream().use { out ->
                sized.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.toByteArray()
            }
        }.getOrNull()
    }

    /** Deletes a capture this app wrote to its own cache; picker (content://) photos are left alone. */
    fun discardIfOwned(uri: Uri) {
        if (uri.scheme != "file") return
        val file = uri.path?.let(::File) ?: return
        if (file.canonicalPath.startsWith(context.cacheDir.canonicalPath)) file.delete()
    }

    private fun readRotation(uri: Uri): Int = runCatching {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            when (ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0
    }.getOrDefault(0)

    private fun rotateUpright(bitmap: Bitmap, degrees: Int): Bitmap =
        if (degrees == 0) {
            bitmap
        } else {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(degrees.toFloat()) }, true)
        }
}
