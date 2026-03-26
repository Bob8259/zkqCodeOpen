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
import android.media.projection.MediaProjectionConfig
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
import com.coc.zkqcode.core.util.fileactions.LogHelper


object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    // Cache for static screen strategy
    private var cachedBitmap: Bitmap? = null
    private var cachedCaptureResult: CaptureResult? = null
    private var lastFrameTimestamp: Long = 0

    // Skip MediaProjection and go straight to shell screencap after first failure
    private var useShellFallback = false

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
            cleanupDisplayResources()
            mediaProjection = null
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

    fun getContext(): Context? = appContext

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
                // getMediaProjection() requires a running foreground service with
                // FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION on Android 14+.
                LogHelper.showDebugInfo("ensureProjection: re-creating MediaProjection (API=${Build.VERSION.SDK_INT})")
                try {
                    mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)?.also {
                        it.registerCallback(projectionCallback, backgroundHandler)
                    }
                } catch (e: SecurityException) {
                    LogHelper.showDebugInfo("ensureProjection: getMediaProjection() failed — " +
                            "foreground service with MEDIA_PROJECTION type may not be running: ${e.message}")
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
        AutoGrantTool.forceEnableAccessibility() 
        MyAccessibilityService.isDetectionEnabled = true

        mediaProjectionManager?.let {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Force whole-screen capture on Android 14+, skipping the app-picker dialog
                it.createScreenCaptureIntent(
                    MediaProjectionConfig.createConfigForDefaultDisplay()
                )
            } else {
                it.createScreenCaptureIntent()
            }
            launcher.launch(intent)
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
            LogHelper.showDebugInfo("reset: Error stopping mediaProjection: ${e.message}")
        }

        // Clean up resources like VirtualDisplay and ImageReader
        cleanupDisplayResources()
        mediaProjection = null

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
                LogHelper.showDebugInfo("ensureVirtualDisplay: Error creating VirtualDisplay: ${e.message}")
                cleanupDisplayResources()
            }
        } else {
            // If VirtualDisplay already exists, check if size has changed
            // Note: resize is not supported in all versions, simple handling here: if size changed, destroy and recreate
            // But usually updateMetrics has been called in capture(), so we can assume metrics are new
            // Simple check if imageReader matches
            if (imageReader?.width != screenWidth || imageReader?.height != screenHeight) {
                LogHelper.showDebugInfo("ensureVirtualDisplay: Size changed, recreating resources")
                cleanupDisplayResources()
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
                    LogHelper.showDebugInfo("ensureVirtualDisplay: Error recreating VirtualDisplay: ${e.message}")
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
            LogHelper.showDebugInfo("takeScreenshot: Failed to ensure projection")
            return false
        }
        ensureHandlerThread() // 确保后台线程已启动

        return try {
            ensureVirtualDisplay(projection)
            virtualDisplay != null
        } catch (e: Exception) {
            LogHelper.showDebugInfo("takeScreenshot: Error during capture setup: ${e.message}")
            cleanupDisplayResources()
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
            // Re-enable MediaProjection path since new permission was granted
            useShellFallback = false
            ensureHandlerThread()
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)?.also {
                it.registerCallback(projectionCallback, backgroundHandler)
            }
        } else {
            LogHelper.showDebugInfo("onPermissionGranted: Permission denied (resultCode=$resultCode)")
        }
    }

    private fun cleanupDisplayResources() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null

        // Clear caches so we don't return stale data after a reset
        cachedBitmap = null
        cachedCaptureResult = null
        lastFrameTimestamp = 0

        // Do NOT set mediaProjection to null here, so we can reuse it
    }

    fun releaseAll() {
        runCatching { mediaProjection?.stop() }
        cleanupDisplayResources()
        mediaProjection = null
        cachedResultCode = null
        cachedIntentData = null
        // Stop background thread
        handlerThread?.quitSafely()
        handlerThread = null
        backgroundHandler = null
        // Release persistent shell connection
        ShellScreenCapture.release()
    }

    data class CaptureResult(
        val buffer: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixelStride: Int,
        val rowStride: Int
    )

    /**
     * Improved coroutine screenshot method.
     * Tries MediaProjection first; falls back to shell screencap -p on failure.
     */
    suspend fun capture(asBitmap: Boolean = true): Any? = captureMutex.withLock {
        // If MediaProjection already failed, skip directly to shell
        if (useShellFallback) {
            return@withLock captureViaShell(asBitmap)
        }

        // Try MediaProjection first
        val projectionResult = try {
            ensureHandlerThread()

            // Internal function: Attempt a complete screenshot process (reuse or create new)
            suspend fun attemptCapture(): Any? {
                return suspendCancellableCoroutine { cont ->
                    updateMetrics()
                    val projection = ensureProjection()

                    if (projection == null) {
                        LogHelper.showDebugInfo("capture: Projection is null")
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
                                // Update cache
                                cachedBitmap = finalBitmap
                                lastFrameTimestamp = System.currentTimeMillis()

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

                                // Update cache
                                cachedCaptureResult = result
                                lastFrameTimestamp = System.currentTimeMillis()

                                if (cont.isActive) cont.resume(result)
                            }
                        } catch (e: Exception) {
                            LogHelper.showDebugInfo("capture: Error processing image: ${e.message}")
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
                    } catch (_: Exception) {
                        // ignore
                    }

                    // 2. No image available, wait for new one
                    reader.setOnImageAvailableListener({ _reader ->
                        _reader.setOnImageAvailableListener(null, null)
                        val image = try {
                            _reader.acquireLatestImage()
                        } catch (_: Exception) {
                            null
                        }
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

            // 1. Try to get a new frame (Short timeout)
            val result = withTimeoutOrNull(100) {
                attemptCapture()
            }

            if (result != null) {
                result
            } else {
                // 2. Timeout: Check if we can reuse cached frame
                val now = System.currentTimeMillis()
                val isCacheValid = (now - lastFrameTimestamp) < 3000 // 3 seconds valid window

                if (isCacheValid) {
                    if (asBitmap && cachedBitmap != null) {
                        cachedBitmap
                    } else if (!asBitmap && cachedCaptureResult != null) {
                        cachedCaptureResult
                    } else {
                        null
                    }
                } else {
                    // 3. Cache expired or not available -> Force reset
                    LogHelper.showDebugInfo("capture: No new frame for >3s, recreating resources")
                    cleanupDisplayResources()

                    // 4. Retry after reset (longer timeout to allow setup)
                    withTimeoutOrNull(200) {
                        attemptCapture()
                    }
                }
            }
        } catch (e: Exception) {
            LogHelper.showDebugInfo("capture: MediaProjection capture failed: ${e.message}")
            null
        }

        if (projectionResult != null) {
            return@withLock projectionResult
        }

        // Fallback to shell screencap when MediaProjection fails or returns null
        useShellFallback = true
        LogHelper.showDebugInfo("capture: Falling back to shell screencap -p (will skip MediaProjection next time)")
        return@withLock captureViaShell(asBitmap)
    }

    /**
     * Fallback capture method delegating to ShellScreenCapture which maintains a
     * persistent root shell and reads screencap output directly from stdout.
     */
    private suspend fun captureViaShell(asBitmap: Boolean): Any? {
        return ShellScreenCapture.capture(asBitmap)
    }
}

