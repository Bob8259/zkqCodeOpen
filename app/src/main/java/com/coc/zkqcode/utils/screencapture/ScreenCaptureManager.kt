package com.coc.zkqcode.utils.screencapture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.graphics.createBitmap
import com.coc.zkqcode.utils.accessibility.MyAccessibilityService
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import kotlin.coroutines.resume

object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0
    private var appContext: Context? = null

    private var cachedResultCode: Int? = null
    private var cachedIntentData: Intent? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Callback to handle MediaProjection session termination.
     * When session is stopped by the system or user, clean up resources.
     */
    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            println("MediaProjection session stopped by system/user")
            cleanupProjectionResources()
        }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        updateMetrics()
    }

    private fun updateMetrics() {
        val context = appContext ?: return
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

    /**
     * Internal helper to ensure MediaProjection is active using cached credentials.
     * Registers callback on new projection creation.
     */
    private fun ensureProjection(): MediaProjection? {
        if (mediaProjection == null) {
            val code = cachedResultCode
            val data = cachedIntentData
            if (code != null && data != null) {
                mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)?.also {
                    it.registerCallback(projectionCallback, mainHandler)
                }
            }
        }
        return mediaProjection
    }

    /**
     * Always create a fresh ImageReader for each capture.
     * Reusing ImageReader can cause listener state issues and buffered image problems.
     */
    private fun prepareImageReader() {
        imageReader?.close()
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
    }

    fun requestPermission(launcher: ActivityResultLauncher<Intent>) {
        if (cachedResultCode != null && cachedIntentData != null) {
            if (takeScreenshot()) return
            reset()
        }
        
        AutoGrantTool.forceEnableAccessibility()
        MyAccessibilityService.isDetectionEnabled = true
        
        mediaProjectionManager?.let { 
            launcher.launch(it.createScreenCaptureIntent()) 
        }
    }

    fun onPermissionGranted(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            cachedResultCode = resultCode
            cachedIntentData = data
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)?.also {
                it.registerCallback(projectionCallback, mainHandler)
            }
        }
    }

    /**
     * Clean up projection-related resources (called when session stops).
     * Does NOT clear cached credentials - allows session recreation.
     */
    private fun cleanupProjectionResources() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection = null
    }

    /**
     * Full reset - clears everything including cached credentials.
     * Use when you want to force a new permission prompt.
     */
    private fun reset() {
        runCatching { mediaProjection?.stop() }
        cleanupProjectionResources()
        cachedResultCode = null
        cachedIntentData = null
    }

    /**
     * Public method to fully release all resources.
     * Call this when the app is shutting down or no longer needs media projection.
     */
    fun releaseAll() {
        runCatching { mediaProjection?.stop() }
        cleanupProjectionResources()
        cachedResultCode = null
        cachedIntentData = null
    }

    fun takeScreenshot(): Boolean {
        updateMetrics()
        val projection = ensureProjection() ?: return false
        prepareImageReader()

        return try {
            // Release existing VirtualDisplay to force a fresh frame
            virtualDisplay?.release()
            virtualDisplay = null
            
            println("Creating VirtualDisplay for screenshot")
            virtualDisplay = projection.createVirtualDisplay(
                "ScreenCapture",
                screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null, mainHandler
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Projection likely invalid, clean up
            cleanupProjectionResources()
            false
        }
    }

    suspend fun captureBitmap(): Bitmap? = suspendCancellableCoroutine { cont ->
        updateMetrics()
        val projection = ensureProjection()
        
        if (projection == null) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        prepareImageReader()

        imageReader?.setOnImageAvailableListener({ reader ->
            // Clear listener immediately to prevent multiple triggers
            reader.setOnImageAvailableListener(null, null)
            
            val image = reader.acquireLatestImage() ?: run {
                if (cont.isActive) cont.resume(null)
                return@setOnImageAvailableListener
            }

            try {
                val plane = image.planes[0]
                val buffer: ByteBuffer = plane.buffer
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * screenWidth

                // Standardized bitmap creation with padding handling
                val bitmap = createBitmap(screenWidth + rowPadding / pixelStride, screenHeight)
                bitmap.copyPixelsFromBuffer(buffer)

                val finalBitmap = if (rowPadding == 0) {
                    bitmap
                } else {
                    Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight).also {
                        if (it != bitmap) bitmap.recycle()
                    }
                }

                if (cont.isActive) cont.resume(finalBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                if (cont.isActive) cont.resume(null)
            } finally {
                image.close()
                // NOTE: No longer calling stopCapture() here - keep VirtualDisplay alive
            }
        }, mainHandler)

        // Safety: ensure reader listener is cleared if coroutine is cancelled externally
        cont.invokeOnCancellation {
            imageReader?.setOnImageAvailableListener(null, null)
        }

        try {
            // Release existing VirtualDisplay to force a fresh frame
            virtualDisplay?.release()
            virtualDisplay = null
            
            println("Creating VirtualDisplay for capture")
            virtualDisplay = projection.createVirtualDisplay(
                "ScreenCapture",
                screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null, mainHandler
            )
        } catch (_: Exception) {
            cleanupProjectionResources() // Token likely dead
            if (cont.isActive) cont.resume(null)
        }
    }
}