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
import android.os.*
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.graphics.createBitmap
import com.coc.zkqcode.core.system.accessibility.MyAccessibilityService
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object ScreenCaptureManager {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

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
            e.printStackTrace()
        }

        // 清理 VirtualDisplay 和 ImageReader 等资源
        cleanupProjectionResources()

        // 清空缓存的权限凭证
        cachedResultCode = null
        cachedIntentData = null
    }

    /**
     * 同步风格的截图方法（非协程版本）。
     * 更新：使用 backgroundHandler 替代 mainHandler。
     */
    fun takeScreenshot(): Boolean {
        updateMetrics()
        val projection = ensureProjection() ?: return false
        prepareImageReader()
        ensureHandlerThread() // 确保后台线程已启动

        return try {
            virtualDisplay?.release()
            virtualDisplay = null

            println("Creating VirtualDisplay for screenshot (Sync)")
            virtualDisplay = projection.createVirtualDisplay(
                "ScreenCapture",
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                backgroundHandler // <--- 使用后台 Handler
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupProjectionResources()
            false
        }
    }

    private fun prepareImageReader() {
        imageReader?.close()
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
    }

    fun onPermissionGranted(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            cachedResultCode = resultCode
            cachedIntentData = data
            ensureHandlerThread()
            mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)?.also {
                it.registerCallback(projectionCallback, backgroundHandler)
            }
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
        ensureHandlerThread() // 确保后台线程就绪

        return@withLock suspendCancellableCoroutine { cont ->
            updateMetrics()
            val projection = ensureProjection()

            if (projection == null) {
                if (cont.isActive) cont.resume(null)
                return@suspendCancellableCoroutine
            }

            prepareImageReader()

            imageReader?.setOnImageAvailableListener({ reader ->
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

                    if (asBitmap) {
                        // 在后台线程创建 Bitmap，避免阻塞 UI
                        val bitmap =
                            createBitmap(screenWidth + rowPadding / pixelStride, screenHeight)
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
                    e.printStackTrace()
                    if (cont.isActive) cont.resume(null)
                } finally {
                    image.close()
                }
            }, backgroundHandler) // <--- 使用后台线程处理图片数据

            cont.invokeOnCancellation {
                imageReader?.setOnImageAvailableListener(null, null)
            }

            try {
                virtualDisplay?.release()
                virtualDisplay = projection.createVirtualDisplay(
                    "ScreenCapture",
                    screenWidth,
                    screenHeight,
                    screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader?.surface,
                    null,
                    backgroundHandler // <--- 使用后台线程接收画面流
                )
            } catch (_: Exception) {
                cleanupProjectionResources()
                if (cont.isActive) cont.resume(null)
            }
        }
    }
}