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
import kotlin.time.Duration.Companion.milliseconds


object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    // Cache for static screen strategy
    private var cachedBitmap: Bitmap? = null
    private var cachedCaptureResult: CaptureResult? = null
    private var lastFrameTimestamp: Long = 0

    // Skip MediaProjection and go straight to shell screencap after repeated hard failures
    private var useShellFallback = false
    private var consecutiveProjectionFailures = 0
    private var lastCaptureSource: String = "none"
    private const val SHELL_FALLBACK_FAILURE_THRESHOLD = 3
    private const val MIN_VALID_OPAQUE_SAMPLE_RATIO = 0.20

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

    fun getLastCaptureSource(): String = lastCaptureSource

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
        consecutiveProjectionFailures = 0
        useShellFallback = false
    }

    /**
     * Check whether it is safe to recreate a VirtualDisplay on the current projection.
     * On Android 14+ (API 34), createVirtualDisplay() can only be called once per
     * MediaProjection instance, so recreation is not possible.
     */
    private fun canRecreateVirtualDisplay(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Resize an existing VirtualDisplay and rebind it to a new ImageReader.
     * This avoids calling createVirtualDisplay() again, which is restricted on API 34+.
     */
    private fun rebindVirtualDisplayToNewSize(): Boolean {
        val display = virtualDisplay ?: return false
        val oldReader = imageReader
        var newReader: ImageReader? = null
        return try {
            newReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
            display.resize(screenWidth, screenHeight, screenDensity)
            display.surface = newReader.surface

            oldReader?.setOnImageAvailableListener(null, null)
            oldReader?.close()
            imageReader = newReader

            // Old frames are the wrong size after rotation/rebind
            invalidateFrameCache()

            LogHelper.showDebugInfo(
                "rebindVirtualDisplayToNewSize: resized to ${screenWidth}x${screenHeight} dpi=$screenDensity"
            )
            true
        } catch (e: Exception) {
            LogHelper.showDebugInfo("rebindVirtualDisplayToNewSize failed: ${e.message}")
            try {
                newReader?.setOnImageAvailableListener(null, null)
                newReader?.close()
            } catch (_: Exception) {
                // ignore clean-up errors
            }
            false
        }
    }

    private fun createVirtualDisplay(projection: MediaProjection): Boolean {
        if (imageReader == null) prepareImageReader()
        return try {
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
            virtualDisplay != null
        } catch (e: Exception) {
            LogHelper.showDebugInfo("createVirtualDisplay: Error creating VirtualDisplay: ${e.message}")
            cleanupDisplayResources()
            false
        }
    }

    /**
     * Ensure VirtualDisplay + ImageReader match current metrics.
     * @return true when the pipeline is ready for capture at the current size.
     */
    private fun ensureVirtualDisplay(projection: MediaProjection): Boolean {
        virtualDisplay ?: return createVirtualDisplay(projection)

        val reader = imageReader
        val sizeChanged = reader == null || reader.width != screenWidth || reader.height != screenHeight
        if (!sizeChanged) {
            return true
        }

        LogHelper.showDebugInfo(
            "ensureVirtualDisplay: Size changed ${reader?.width}x${reader?.height} -> ${screenWidth}x${screenHeight}"
        )

        // Preferred path on all API levels: resize + rebind surface (no second createVirtualDisplay)
        if (rebindVirtualDisplayToNewSize()) {
            return true
        }

        // Resize failed — recreate only when the platform allows another createVirtualDisplay()
        if (canRecreateVirtualDisplay()) {
            LogHelper.showDebugInfo("ensureVirtualDisplay: resize failed, recreating resources (pre-API 34)")
            cleanupDisplayResources()
            return createVirtualDisplay(projection)
        }

        // API 34+: cannot recreate VirtualDisplay on the same projection instance.
        // Drop broken display resources so capture can temporarily use shell without
        // permanently abandoning a still-valid MediaProjection session.
        LogHelper.showDebugInfo(
            "ensureVirtualDisplay: resize failed and API 34+ prevents recreation; display resources released for temporary shell fallback"
        )
        cleanupDisplayResources()
        return false
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
            consecutiveProjectionFailures = 0
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

    private fun invalidateFrameCache() {
        cachedBitmap?.recycle()
        cachedBitmap = null
        cachedCaptureResult = null
        lastFrameTimestamp = 0
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

        invalidateFrameCache()

        // Do NOT set mediaProjection to null here, so we can reuse it
    }

    fun releaseAll() {
        runCatching { mediaProjection?.stop() }
        cleanupDisplayResources()
        mediaProjection = null
        cachedResultCode = null
        cachedIntentData = null
        projectionTokenUsed = false
        consecutiveProjectionFailures = 0
        useShellFallback = false
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

    private fun isFrameContentValid(buffer: ByteBuffer, width: Int, height: Int, pixelStride: Int, rowStride: Int): Boolean {
        if (width <= 0 || height <= 0 || pixelStride < 4 || rowStride < width * pixelStride) {
            return false
        }

        val capacity = buffer.capacity()
        var samples = 0
        var opaque = 0
        var nonZero = 0
        val stepX = (width / 32).coerceAtLeast(1)
        val stepY = (height / 32).coerceAtLeast(1)
        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val offset = y * rowStride + x * pixelStride
                if (offset + 3 < capacity) {
                    val r = buffer.get(offset).toInt() and 0xFF
                    val g = buffer.get(offset + 1).toInt() and 0xFF
                    val b = buffer.get(offset + 2).toInt() and 0xFF
                    val a = buffer.get(offset + 3).toInt() and 0xFF
                    if ((r or g or b or a) != 0) nonZero++
                    if (a == 255) opaque++
                    samples++
                }
                x += stepX
            }
            y += stepY
        }

        if (samples == 0) return false
        val opaqueRatio = opaque.toDouble() / samples.toDouble()
        val isValid = opaqueRatio >= MIN_VALID_OPAQUE_SAMPLE_RATIO
        if (!isValid) {
            LogHelper.showDebugInfo(
                "capture: rejected MediaProjection frame content " +
                        "${width}x${height} opaque=$opaque/$samples nonZero=$nonZero/$samples"
            )
        }
        return isValid
    }

    private fun isCacheGeometryValid(asBitmap: Boolean): Boolean {
        val reader = imageReader ?: return false
        return if (asBitmap) {
            val bitmap = cachedBitmap
            bitmap != null && !bitmap.isRecycled &&
                    bitmap.width == reader.width && bitmap.height == reader.height
        } else {
            val result = cachedCaptureResult
            result != null &&
                    result.width == reader.width && result.height == reader.height
        }
    }

    private fun noteProjectionSuccess() {
        consecutiveProjectionFailures = 0
        // A healthy MediaProjection frame means shell lockout can be cleared.
        useShellFallback = false
    }

    private fun noteProjectionFailure(reason: String): Boolean {
        consecutiveProjectionFailures++
        val permanent = consecutiveProjectionFailures >= SHELL_FALLBACK_FAILURE_THRESHOLD
        if (permanent) {
            useShellFallback = true
            LogHelper.showDebugInfo(
                "capture: $reason — permanent shell fallback after " +
                        "$consecutiveProjectionFailures consecutive MediaProjection failures"
            )
        } else {
            LogHelper.showDebugInfo(
                "capture: $reason — temporary shell fallback " +
                        "($consecutiveProjectionFailures/$SHELL_FALLBACK_FAILURE_THRESHOLD)"
            )
        }
        return permanent
    }

    /**
     * Captures the screen as the requested type and restarts the app if capture fails.
     */
    suspend inline fun <reified T> captureScreenOrRestart(
        asBitmap: Boolean = true,
        errorMessage: String = "screen capture failed"
    ): T = capture(asBitmap) as? T ?: LogHelper.logAndRestart(errorMessage)

    /**
     * Improved coroutine screenshot method.
     * Tries MediaProjection first; falls back to shell screencap -p on failure.
     *
     * Strategy:
     * 1. Try to acquire a fresh frame with a short timeout.
     * 2. If no new frame arrives (screen is static), return the cached frame as
     *    long as the VirtualDisplay is still alive and cache geometry matches.
     * 3. On size change, resize/rebind VirtualDisplay instead of abandoning the session.
     * 4. Use temporary shell fallback on isolated failures; permanent shell only after
     *    repeated hard MediaProjection failures.
     */
    suspend fun capture(asBitmap: Boolean = true): Any? = captureMutex.withLock {
        // If MediaProjection already failed repeatedly, skip directly to shell
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

                    if (!ensureVirtualDisplay(projection)) {
                        LogHelper.showDebugInfo("capture: VirtualDisplay pipeline not ready for ${screenWidth}x${screenHeight}")
                        if (cont.isActive) cont.resume(null)
                        return@suspendCancellableCoroutine
                    }

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
                            // Prefer actual image geometry over window metrics so rotation
                            // cannot label a buffer with the wrong width/height.
                            val imageWidth = image.width
                            val imageHeight = image.height
                            val rowPadding = rowStride - pixelStride * imageWidth

                            if (!isFrameContentValid(buffer, imageWidth, imageHeight, pixelStride, rowStride)) {
                                if (cont.isActive) cont.resume(null)
                                return
                            }

                            if (asBitmap) {
                                val bitmap = createBitmap(imageWidth + rowPadding / pixelStride, imageHeight)
                                pendingBitmap = bitmap
                                bitmap.copyPixelsFromBuffer(buffer)
                                val finalBitmap = if (rowPadding == 0) {
                                    bitmap
                                } else {
                                    Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight).also {
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
                                    width = imageWidth,
                                    height = imageHeight,
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
            val result = withTimeoutOrNull(300.milliseconds) {
                attemptCapture()
            }

            if (result != null) {
                result
            } else {
                // 2. No new frame — the screen is likely static.
                //    Return the cached frame as long as the pipeline is alive and
                //    cache geometry still matches the current ImageReader.
                val pipelineAlive = virtualDisplay != null && imageReader != null && mediaProjection != null

                if (pipelineAlive) {
                    if (isCacheGeometryValid(asBitmap)) {
                        if (asBitmap) cachedBitmap else cachedCaptureResult
                    } else {
                        // Pipeline is alive but we never got a single valid frame yet,
                        // or cache was invalidated after resize. Retry with longer timeout.
                        LogHelper.showDebugInfo("capture: Pipeline alive but no valid cached frame, retrying with longer timeout")
                        withTimeoutOrNull(500.milliseconds) {
                            attemptCapture()
                        }
                    }
                } else {
                    // Pipeline is broken — resources are null.
                    // On pre-API 34 we can try to recreate; on 34+ leave null for temporary shell.
                    if (canRecreateVirtualDisplay()) {
                        LogHelper.showDebugInfo("capture: Pipeline broken, recreating resources (pre-API 34)")
                        cleanupDisplayResources()
                        withTimeoutOrNull(500.milliseconds) {
                            attemptCapture()
                        }
                    } else {
                        LogHelper.showDebugInfo("capture: Pipeline broken on API 34+, cannot recreate VirtualDisplay")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            LogHelper.showDebugInfo("capture: MediaProjection capture failed: ${e.message}")
            null
        }

        if (projectionResult != null) {
            lastCaptureSource = "mediaprojection"
            noteProjectionSuccess()
            return@withLock projectionResult
        }

        // Fallback to shell screencap when MediaProjection fails or returns null.
        // Permanent shell lockout only after repeated consecutive hard failures.
        noteProjectionFailure("MediaProjection returned null or invalid frame")
        return@withLock captureViaShell(asBitmap)
    }

    /**
     * Fallback capture method delegating to ShellScreenCapture which maintains a
     * persistent root shell and reads screencap output directly from stdout.
     */
    private suspend fun captureViaShell(asBitmap: Boolean): Any? {
        val result = ShellScreenCapture.capture(asBitmap)
        if (result != null) {
            lastCaptureSource = "shell"
        }
        return result
    }
}
