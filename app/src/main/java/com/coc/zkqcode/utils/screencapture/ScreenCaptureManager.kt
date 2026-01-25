package com.coc.zkqcode.utils.screencapture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.graphics.createBitmap
import com.coc.zkqcode.utils.accessibility.MyAccessibilityService
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0

    // Cache the intent data and result code to reuse for subsequent captures
    private var cachedResultCode: Int? = null
    private var cachedIntentData: Intent? = null

    fun init(context: Context) {
        mediaProjectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            screenWidth = metrics.bounds.width()
            screenHeight = metrics.bounds.height()
            screenDensity = context.resources.configuration.densityDpi
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
            screenDensity = metrics.densityDpi
        }
    }

    fun requestPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
        if (cachedResultCode != null && cachedIntentData != null) {
            if (takeScreenshot(context)) {
                return
            }
            // If taking screenshot failed (e.g. invalid token), reset and request again
            reset()
        }
        // Try to force enable accessibility service via Root
        AutoGrantTool.forceEnableAccessibility()
        // Enable detection flag, only effective for this request
        MyAccessibilityService.isDetectionEnabled = true
        mediaProjectionManager?.let { manager ->
            launcher.launch(manager.createScreenCaptureIntent())
        }
    }

    fun onPermissionGranted(resultCode: Int, data: Intent, context: Context) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        // Cache the data for future use
        cachedResultCode = resultCode
        cachedIntentData = data

        if (mediaProjection == null) {
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
        }
        takeScreenshot(context)
    }

    private fun reset() {
        try {
            mediaProjection?.stop()
        } catch (_: Exception) {
        }
        mediaProjection = null
        cachedResultCode = null
        cachedIntentData = null
        stopCapture()
    }

    fun takeScreenshot(context: Context): Boolean {
        if (mediaProjection == null) {
            val code = cachedResultCode
            val data = cachedIntentData
            if (code != null && data != null) {
                mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)
            }
        }

        if (mediaProjection == null) {
            // If we don't have it, we might need to request it again, 
            // but usually this happens if the cached data is invalid or we never got it.
            return false
        }

        // Ensure imageReader is refreshed if size changed or it was closed
        if (imageReader == null) {
            imageReader =
                ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        }

        // Wait a bit for the virtual display to render the first frame
        val handler = Handler(Looper.getMainLooper())

        try {
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCapture",
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                handler
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Usually SecurityException: Invalid media projection
            // This means our token is dead.
            return false
        }

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                saveImage(image, context)
                image.close()
                stopCapture() // Stop after one capture
            }
        }, handler)

        return true
    }

    suspend fun captureBitmap(): Bitmap? = suspendCancellableCoroutine { cont ->
        if (mediaProjection == null) {
            val code = cachedResultCode
            val data = cachedIntentData
            if (code != null && data != null) {
                mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)
            }
        }

        if (mediaProjection == null) {
            if (cont.isActive) cont.resume(null)
            return@suspendCancellableCoroutine
        }

        if (imageReader == null) {
            imageReader =
                ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        }

        val handler = Handler(Looper.getMainLooper())

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                try {
                    val planes = image.planes
                    val buffer: ByteBuffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * screenWidth

                    // Create bitmap
                    val bitmap = Bitmap.createBitmap(
                        screenWidth + rowPadding / pixelStride,
                        screenHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.copyPixelsFromBuffer(buffer)

                    val finalBitmap = if (rowPadding == 0) {
                        bitmap
                    } else {
                        val cropped = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
                        if (cropped != bitmap) {
                            bitmap.recycle()
                        }
                        cropped
                    }

                    if (cont.isActive) cont.resume(finalBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (cont.isActive) cont.resume(null)
                } finally {
                    image.close()
                    stopCapture()
                }
            }
        }, handler)

        try {
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCapture",
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                handler
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Important: If we get an error (like Invalid media projection), 
            // the token is likely dead. Reset it so we try to recreate it next time.
            mediaProjection = null
            if (cont.isActive) cont.resume(null)
        }
    }

    private fun saveImage(image: Image, context: Context) {
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth

        // Create bitmap
        val bitmap = createBitmap(screenWidth + rowPadding / pixelStride, screenHeight)
        bitmap.copyPixelsFromBuffer(buffer)

        // Crop bitmap to actual screen size if there is padding
        val finalBitmap = if (rowPadding == 0) {
            bitmap
        } else {
            Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
        }

        try {
            val file = File(context.filesDir, "screenshot.png")
            val fos = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            // Optional: Show toast or feedback
            println("截图已保存: ${file.name}")

        } catch (_: Exception) {
        } finally {
            if (finalBitmap != bitmap) {
                bitmap.recycle()
            }
            finalBitmap.recycle()
        }
    }

    private fun stopCapture() {
        try {
            virtualDisplay?.release()
            virtualDisplay = null
            imageReader?.close()
            imageReader = null
            // Do NOT stop mediaProjection here to allow reuse
        } catch (_: Exception) {

        }
    }
}
