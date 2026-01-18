package com.coc.zkqcode.utils.floatingwindows


import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.loadjar.Loadjar
import com.coc.zkqcode.utils.components.GlobalVars

class UIWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    // --- Lifecycle related essential code ---
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry
    private lateinit var windowParams: WindowManager.LayoutParams
    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performRestore(null)
    }
    override val savedStateRegistry: SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        val notification = NotificationHelper.createNotification(this)
        startForeground(1000, notification)

        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showFloatingWindow()
    }

    private fun showFloatingWindow() {
        if (composeView != null) return

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val windowHeight =
            if (GlobalVars.currentMode == "Main") (screenHeight * 0.9).toInt() else (screenHeight * 0.6).toInt()

        val windowType = getWindowType()

        windowParams = WindowManager.LayoutParams(
            screenWidth,
            windowHeight,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            windowAnimations = 0
        }
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@UIWindowService)
            setViewTreeSavedStateRegistryOwner(this@UIWindowService)

            setContent {
                Loadjar(context).LoadAndShowUI(onClose = {
                    when (GlobalVars.currentMode) {
                        "SwitchAccount" -> {
                            updateWindowLayout(
                                displayMetrics.widthPixels,
                                (displayMetrics.heightPixels * 0.6).toInt()
                            )
                        }
                        "Main" -> {
                            updateWindowLayout(
                                displayMetrics.widthPixels,
                                (displayMetrics.heightPixels * 0.9).toInt()
                            )
                        }
                        else -> {
                            closeMainUI()
                            startService(
                                Intent(
                                    this@UIWindowService,
                                    ControlWindowService::class.java
                                )
                            )
                        }
                    }

                })
            }
        }

        windowManager.addView(composeView, windowParams)
    }

    private fun updateWindowLayout(newWidth: Int, newHeight: Int) {
        if (composeView != null && ::windowParams.isInitialized) {
            windowParams.width = newWidth
            windowParams.height = newHeight
            // 只有调用此方法，WindowManager 才会重新渲染窗口大小
            windowManager.updateViewLayout(composeView, windowParams)
        }
    }

    private fun getWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // 兼容 Android 7.1 及以下
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun closeMainUI() {
        if (composeView != null) {
            windowManager.removeView(composeView)
            composeView = null
        }

        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (composeView != null) {
            windowManager.removeViewImmediate(composeView)
            composeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        showFloatingWindow()
        return START_STICKY
    }
}
