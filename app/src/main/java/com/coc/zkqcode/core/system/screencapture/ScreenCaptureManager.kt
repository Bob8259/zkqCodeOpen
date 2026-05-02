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

    // Android 14+ enforces single-use MediaProjection tokens.
    // Track whether the cached resultData has already been consumed by
    // getMediaProjection() so we never attempt to reuse a spent token.
    private var projectionTokenUsed = false

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
                // On Android 14+ the resultData token is single-use.
                // If we already consumed it once, don't attempt again — it will throw.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && projectionTokenUsed) {
                    LogHelper.showDebugInfo("ensureProjection: token already consumed on API 34+, skipping re-creation")
                    return null
                }

                ensureHandlerThread()
                // getMediaProjection() requires a running foreground service with
                // FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION on Android 14+.
                LogHelper.showDebugInfo("ensureProjection: re-creating MediaProjection (API=${Build.VERSION.SDK_INT})")
                try {
                    mediaProjection = mediaProjectionManager?.getMediaProjection(code, data)?.also {
                        it.registerCallback(projectionCallback, backgroundHandler)
                    }
                    // Mark the token as consumed so we never try to reuse it on API 34+
                    projectionTokenUsed = true
                } catch (e: SecurityException) {
                    LogHelper.showDebugInfo("ensureProjection: getMediaProjection() failed — " +
                            "foreground service with MEDIA_PROJECTION type may not be running: ${e.message}")
                    projectionTokenUsed = true
                } catch (e: Exception) {
                    LogHelper.showDebugInfo("ensureProjection: getMediaProjection() failed: ${e.message}")
                    projectionTokenUsed = true
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
                // Force whole-screen capture on Android 14+, skipping the app-picker dialogue
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

        // Clear cached permission credentials and token tracking
        cachedResultCode = null
        cachedIntentData = null
        projectionTokenUsed = false
    }

    /**
     * Check whether it is safe to recreate a VirtualDisplay on the current projection.
     * On Android 14+ (API 34), createVirtualDisplay() can only be called once per
     * MediaProjection instance, so recreation is not possible.
     */
    private fun canRecreateVirtualDisplay(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
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
            if (imageReader?.width != screenWidth || imageReader?.height != screenHeight) {
                // On Android 14+, we cannot call createVirtualDisplay() again on the same
                // projection instance. Fall back to shell instead of crashing.
                if (!canRecreateVirtualDisplay()) {
                    LogHelper.showDebugInfo("ensureVirtualDisplay: Size changed but API 34+ prevents recreation, will use shell fallback")
                    return
                }

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
                    // Clean up dangling imageReader to prevent Surface/native buffer leak
                    cleanupDisplayResources()
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
        ensureHandlerThread() // Ensure background thread is started

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
            // Remove listener before closing to prevent race condition where the
            // background handler receives a callback on a closing/closed reader.
            imageReader?.setOnImageAvailableListener(null, null)
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
            // Fresh token — mark as not yet consumed
            projectionTokenUsed = false
            ensureHandlerThread()
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)?.also {
                it.registerCallback(projectionCallback, backgroundHandler)
            }
            // The token has now been consumed by getMediaProjection()
            projectionTokenUsed = true
        } else {
            LogHelper.showDebugInfo("onPermissionGranted: Permission denied (resultCode=$resultCode)")
        }
    }

    private fun cleanupDisplayResources() {
        // Remove the image listener BEFORE closing the reader to prevent the
        // background handler from receiving a callback on a closing/closed reader.
        // This fixes the "Image is already closed" race condition.
        imageReader?.setOnImageAvailableListener(null, null)

        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null

        // Recycle cached bitmap to free native pixel memory before clearing
        cachedBitmap?.recycle()
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
        projectionTokenUsed = false
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
     *
     * Strategy:
     * 1. Try to acquire a fresh frame with a short timeout.
     * 2. If no new frame arrives (screen is static), return the cached frame as
     *    long as the VirtualDisplay is still alive — the cache is a valid
     *    representation of the current screen regardless of its age.
     * 3. Only fall back to shell when the MediaProjection pipeline is actually
     *    broken (projection/virtualDisplay/imageReader is null).
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
                        // Track in-flight bitmap so it can be recycled if an exception
                        // occurs before it is safely stored in the cache.
                        var pendingBitmap: Bitmap? = null
                        try {
                            val plane = image.planes[0]
                            val buffer: ByteBuffer = plane.buffer
                            val pixelStride = plane.pixelStride
                            val rowStride = plane.rowStride
                            val rowPadding = rowStride - pixelStride * screenWidth

                            if (asBitmap) {
                                val bitmap = createBitmap(screenWidth + rowPadding / pixelStride, screenHeight)
                                pendingBitmap = bitmap
                                bitmap.copyPixelsFromBuffer(buffer)
                                val finalBitmap = if (rowPadding == 0) {
                                    bitmap
                                } else {
                                    Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight).also {
                                        if (it != bitmap) bitmap.recycle()
                                        pendingBitmap = it
                                    }
                                }
                                // Recycle the previous cached bitmap to free native memory
                                val oldBitmap = cachedBitmap
                                cachedBitmap = finalBitmap
                                if (oldBitmap != null && oldBitmap != finalBitmap) {
                                    oldBitmap.recycle()
                                }
                                // Successfully cached; clear pending tracker
                                pendingBitmap = null
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
                            // Recycle any bitmap allocated but not yet cached to prevent native memory leak
                            if (pendingBitmap != null && !pendingBitmap!!.isRecycled) {
                                pendingBitmap!!.recycle()
                            }
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
                    reader.setOnImageAvailableListener({ availableReader ->
                        availableReader.setOnImageAvailableListener(null, null)
                        val image = try {
                            availableReader.acquireLatestImage()
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

            // 1. Try to get a new frame (increased timeout for slower devices)
            val result = withTimeoutOrNull(300) {
                attemptCapture()
            }

            if (result != null) {
                result
            } else {
                // 2. No new frame — the screen is likely static.
                //    Return the cached frame as long as the pipeline is alive.
                //    A static screen produces no new ImageReader frames, but the
                //    last captured frame is still a valid representation.
                val pipelineAlive = virtualDisplay != null && imageReader != null && mediaProjection != null

                if (pipelineAlive) {
                    val cached = if (asBitmap) cachedBitmap else cachedCaptureResult
                    if (cached != null) {
                        cached
                    } else {
                        // Pipeline is alive but we never got a single frame yet.
                        // This can happen on very first capture if the device is slow.
                        // Give it one more try with a longer timeout.
                        LogHelper.showDebugInfo("capture: Pipeline alive but no cached frame, retrying with longer timeout")
                        withTimeoutOrNull(500) {
                            attemptCapture()
                        }
                    }
                } else {
                    // Pipeline is broken — resources are null.
                    // On pre-API 34 we can try to recreate; on 34+ go straight to shell.
                    if (canRecreateVirtualDisplay()) {
                        LogHelper.showDebugInfo("capture: Pipeline broken, recreating resources (pre-API 34)")
                        cleanupDisplayResources()
                        withTimeoutOrNull(500) {
                            attemptCapture()
                        }
                    } else {
                        LogHelper.showDebugInfo("capture: Pipeline broken on API 34+, cannot recreate")
                        null
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
