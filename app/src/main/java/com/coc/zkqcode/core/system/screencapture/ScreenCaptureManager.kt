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

    // --- 新增：专门处理截图的后台线程 ---
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
     * 初始化后台线程。只有 HandlerThread 准备好了，ImageReader 才能工作。
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
        ensureHandlerThread() // 初始化时启动线程
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
                    // 关键：注册回调也使用 backgroundHandler
                    it.registerCallback(projectionCallback, backgroundHandler)
                }
            }
        }
        return mediaProjection
    }

    /**
     * 请求权限或尝试直接截图。
     */
    fun requestPermission(launcher: ActivityResultLauncher<Intent>) {
        if (cachedResultCode != null && cachedIntentData != null) {
            // 如果已有缓存权限，直接尝试截一次图
            if (takeScreenshot()) return
            reset()
        }

        // 这里的辅助功能逻辑保留
        AutoGrantTool.forceEnableAccessibility() // 确保这个工具类在你的项目中
        MyAccessibilityService.isDetectionEnabled = true

        mediaProjectionManager?.let {
            launcher.launch(it.createScreenCaptureIntent())
        }
    }

    /**
     * 完全重置 - 清除包括缓存凭证在内的所有内容。
     * 当你想强制弹出新的权限申请窗口时使用。
     */
    private fun reset() {
        try {
            // 停止当前的投屏会话
            mediaProjection?.stop()
        } catch (e: Exception) {
            Timber.e(e, "reset: Error stopping mediaProjection")
        }

        // 清理 VirtualDisplay 和 ImageReader 等资源
        cleanupProjectionResources()

        // 清空缓存的权限凭证
        cachedResultCode = null
        cachedIntentData = null
    }

    private fun ensureVirtualDisplay(projection: MediaProjection) {
        if (virtualDisplay == null) {
             // 如果需要 ImageReader，确保它已准备好
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
            // 如果 VirtualDisplay 已经存在，检查尺寸是否发生变化
            // 注意：resize 不是所有版本都支持，这里简单处理：如果尺寸变了，就销毁重建
            // 但通常 capture() 里已经调过 updateMetrics，这里可以假设 metrics 是新的
             // 简单的检查 imageReader 是否匹配
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
     * 同步风格的截图方法（非协程版本）。
     * 更新：使用 backgroundHandler 替代 mainHandler。
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
             // 使用 RGBA_8888 格式，maxImages 设为 2 足够
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
        // 停止后台线程
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
     * 改进后的协程截图方法
     */
    suspend fun capture(asBitmap: Boolean = true): Any? = captureMutex.withLock {
        ensureHandlerThread()

        // 内部函数：尝试一次完整的截图流程（复用或新建）
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

        // 第一次尝试：设置超时 50ms
        // 如果屏幕静止，复用的 VirtualDisplay 可能不产生新帧，导致 suspend 挂起
        val result = withTimeoutOrNull(50) {
            attemptCapture()
        }

        if (result != null) {
            return@withLock result
        } else {
            // 超时了：说明 VirtualDisplay 很可能因为屏幕静止而不发帧，或者 ImageReader 有问题
            // 此时我们强制重置资源（会销毁 VirtualDisplay）
            Timber.d("capture: Timeout waiting for image, recreating VirtualDisplay to force update")
            cleanupProjectionResources()
            
            // 第二次尝试：因为资源已被清理，attemptCapture 内部会重建 VirtualDisplay
            // 重建 VirtualDisplay 通常没有任何延时就会立刻发送第一帧，所以这里不需要太长超时
            // 但为了保险，还是给点时间
             return@withLock withTimeoutOrNull(200) {
                attemptCapture()
            }
        }
    }
}
