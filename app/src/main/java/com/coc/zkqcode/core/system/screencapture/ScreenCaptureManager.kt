package com.coc.zkqcode.core.system.screencapture

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
import android.os.*
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.graphics.createBitmap
import com.coc.zkqcode.core.system.accessibility.MyAccessibilityService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber


object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    
    // Cache for static screen strategy (Optional, for now just use timeout/retry)
    // private var cachedBitmap: Bitmap? = null

    // --- New: Dedicated background thread for handling screenshots ---
    private var handlerThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0
    private var appContext: Context? = null

    private var cachedResultCode: Int? = null
    private var cachedIntentData: Intent? = null

    private val captureMutex = Mutex()

    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            cleanupProjectionResources()
        }
    }

    /**
     * Initialize the background thread. ImageReader can only work when HandlerThread is ready.
     */
    private fun ensureHandlerThread() {
        if (handlerThread == null || !handlerThread!!.isAlive) {
            handlerThread = HandlerThread("ScreenCapBackground").apply { start() }
            backgroundHandler = Handler(handlerThread!!.looper)
        }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        mediaProjectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        updateMetrics()
        ensureHandlerThread() // Start thread during initialization
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
            @Suppress("DEPRECATION") windowManager.defaultDisplay.getRealMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
            screenDensity = metrics.densityDpi
        }
    }

    private fun ensureProjection(): MediaProjection? {
        if (mediaProjection == null) {
            val code = cachedResultCode
            val data = cachedIntentData
            if (code != null && data != null) {
                ensureHandlerThread()
                mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)?.also {
                    // Key: Register callback also using backgroundHandler
                    it.registerCallback(projectionCallback, backgroundHandler)
                }
            }
        }
        return mediaProjection
    }

    /**
     * Request permission or attempt to take screenshot directly.
     */
    fun requestPermission(launcher: ActivityResultLauncher<Intent>) {
        if (cachedResultCode != null && cachedIntentData != null) {
            // If permission is already cached, try to take a screenshot directly
            if (takeScreenshot()) return
            reset()
        }

        // Keep the accessibility logic here
        AutoGrantTool.forceEnableAccessibility() // 确保这个工具类在你的项目中
        MyAccessibilityService.isDetectionEnabled = true

        mediaProjectionManager?.let {
            launcher.launch(it.createScreenCaptureIntent())
        }
    }

    /**
     * Complete reset - Clear all content including cached credentials.
     * Use this when you want to force a new permission request dialog.
     */
    private fun reset() {
        try {
            // Stop the current projection session
            mediaProjection?.stop()
        } catch (e: Exception) {
            Timber.e(e, "reset: Error stopping mediaProjection")
        }

        // Clean up resources like VirtualDisplay and ImageReader
        cleanupProjectionResources()

        // Clear cached permission credentials
        cachedResultCode = null
        cachedIntentData = null
    }

    private fun ensureVirtualDisplay(projection: MediaProjection) {
        if (virtualDisplay == null) {
             // If ImageReader is needed, ensure it's ready
             if (imageReader == null) prepareImageReader()

             try {
                 virtualDisplay = projection.createVirtualDisplay(
                    "ScreenCapture",
                    screenWidth,
                    screenHeight,
                    screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader?.surface,
                    null,
                    backgroundHandler
                )
             } catch (e: Exception) {
                 Timber.e(e, "ensureVirtualDisplay: Error creating VirtualDisplay")
                 cleanupProjectionResources()
             }
        } else {
            // If VirtualDisplay already exists, check if size has changed
            // Note: resize is not supported in all versions, simple handling here: if size changed, destroy and recreate
            // But usually updateMetrics has been called in capture(), so we can assume metrics are new
             // Simple check if imageReader matches
             if (imageReader?.width != screenWidth || imageReader?.height != screenHeight) {
                 Timber.d("ensureVirtualDisplay: Size changed, recreating resources")
                 cleanupProjectionResources()
                 prepareImageReader()
                 try {
                     virtualDisplay = projection.createVirtualDisplay(
                        "ScreenCapture",
                        screenWidth,
                        screenHeight,
                        screenDensity,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader?.surface,
                        null,
                        backgroundHandler
                    )
                 } catch (e: Exception) {
                     Timber.e(e, "ensureVirtualDisplay: Error recreating VirtualDisplay")
                 }
             }
        }
    }

    /**
     * Synchronous style screenshot method (non-coroutine version).
     * Update: Use backgroundHandler instead of mainHandler.
     */
    fun takeScreenshot(): Boolean {
        updateMetrics()
        val projection = ensureProjection() ?: run {
            Timber.e("takeScreenshot: Failed to ensure projection")
            return false
        }
        ensureHandlerThread() // 确保后台线程已启动

        return try {
            ensureVirtualDisplay(projection)
            virtualDisplay != null
        } catch (e: Exception) {
            Timber.e(e, "takeScreenshot: Error during capture setup")
            cleanupProjectionResources()
            false
        }
    }

    private fun prepareImageReader() {
        if (imageReader == null || imageReader?.width != screenWidth || imageReader?.height != screenHeight) {
            imageReader?.close()
             // Use RGBA_8888 format, maxImages set to 2 is sufficient
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        }
    }

    fun onPermissionGranted(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            cachedResultCode = resultCode
            cachedIntentData = data
            ensureHandlerThread()
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)?.also {
                it.registerCallback(projectionCallback, backgroundHandler)
            }
        } else {
            Timber.e("onPermissionGranted: Permission denied (resultCode=$resultCode)")
        }
    }

    private fun cleanupProjectionResources() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection = null
    }

    fun releaseAll() {
        runCatching { mediaProjection?.stop() }
        cleanupProjectionResources()
        cachedResultCode = null
        cachedIntentData = null
        // Stop background thread
        handlerThread?.quitSafely()
        handlerThread = null
        backgroundHandler = null
    }

    data class CaptureResult(
        val buffer: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixelStride: Int,
        val rowStride: Int
    )

    /**
     * Improved coroutine screenshot method
     */
    suspend fun capture(asBitmap: Boolean = true): Any? = captureMutex.withLock {
        ensureHandlerThread()

        // Internal function: Attempt a complete screenshot process (reuse or create new)
        suspend fun attemptCapture(): Any? {
            return suspendCancellableCoroutine { cont ->
                updateMetrics()
                val projection = ensureProjection()

                if (projection == null) {
                    Timber.e("capture: Projection is null")
                    if (cont.isActive) cont.resume(null)
                    return@suspendCancellableCoroutine
                }

                ensureVirtualDisplay(projection)
                val reader = imageReader
                if (reader == null) {
                    if (cont.isActive) cont.resume(null)
                    return@suspendCancellableCoroutine
                }
                
                // Helper to process image
                fun processImage(image: android.media.Image) {
                    try {
                        val plane = image.planes[0]
                        val buffer: ByteBuffer = plane.buffer
                        val pixelStride = plane.pixelStride
                        val rowStride = plane.rowStride
                        val rowPadding = rowStride - pixelStride * screenWidth

                        if (asBitmap) {
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
                        } else {
                            val capacity = buffer.capacity()
                            val directCopy = ByteBuffer.allocateDirect(capacity)
                            buffer.rewind()
                            directCopy.put(buffer)
                            directCopy.flip()

                            val result = CaptureResult(
                                buffer = directCopy,
                                width = screenWidth,
                                height = screenHeight,
                                pixelStride = pixelStride,
                                rowStride = rowStride
                            )
                            if (cont.isActive) cont.resume(result)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "capture: Error processing image")
                        if (cont.isActive) cont.resume(null)
                    } finally {
                        image.close()
                    }
                }

                // 1. Try to get existing latest image immediately
                try {
                    val latestImage = reader.acquireLatestImage()
                    if (latestImage != null) {
                         processImage(latestImage)
                         return@suspendCancellableCoroutine
                    }
                } catch (e: Exception) {
                    // ignore
                }

                // 2. No image available, wait for new one
                reader.setOnImageAvailableListener({ _reader ->
                    _reader.setOnImageAvailableListener(null, null)
                    val image = try { _reader.acquireLatestImage() } catch (e: Exception) { null }
                    if (image != null) {
                        processImage(image)
                    } else {
                         if (cont.isActive) cont.resume(null)
                    }
                }, backgroundHandler)

                cont.invokeOnCancellation {
                    reader.setOnImageAvailableListener(null, null)
                }
            }
        }

        // First attempt: Set timeout to 50ms
        // If screen is static, reused VirtualDisplay may not produce new frames, causing suspend to hang
        val result = withTimeoutOrNull(50) {
            attemptCapture()
        }

        if (result != null) {
            return@withLock result
        } else {
            // Timeout: VirtualDisplay is likely not producing frames due to static screen, or ImageReader has issues
            // Force reset resources (will destroy VirtualDisplay)
            Timber.d("capture: Timeout waiting for image, recreating VirtualDisplay to force update")
            cleanupProjectionResources()
            
            // Second attempt: Since resources have been cleaned up, attemptCapture will rebuild VirtualDisplay internally
            // Rebuilding VirtualDisplay usually sends the first frame immediately without delay, so no long timeout needed here
            // But for safety, still give it some time
             return@withLock withTimeoutOrNull(200) {
                attemptCapture()
            }
        }
    }
}
