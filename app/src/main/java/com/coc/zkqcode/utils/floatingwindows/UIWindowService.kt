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
import com.coc.zkqcode.utils.floatingwindows.NotificationHelper

class UIWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    // --- Lifecycle related essential code ---
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

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

        val windowWidth = (screenWidth * 0.95).toInt()
        val windowHeight = (screenHeight * 0.9).toInt()

        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            windowWidth,
            windowHeight,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@UIWindowService)
            setViewTreeSavedStateRegistryOwner(this@UIWindowService)

            setContent {
                Loadjar(context).LoadAndShowUI(onClose = {
                    if (com.coc.zkqcode.utils.components.GlobalVars.showManualMode) {
                        closeMainUI(silent = true)
                        startService(Intent(this@UIWindowService, SwitchAccountWindowService::class.java))
                    } else {
                        closeMainUI()
                    }
                })
            }
        }

        windowManager.addView(composeView, params)
    }


    private fun closeMainUI(silent: Boolean = false) {
        if (composeView != null) {
            windowManager.removeView(composeView)
            composeView = null
        }
        if (!silent) {
            startService(Intent(this, ControlWindowService::class.java))
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (composeView != null) {
            windowManager.removeView(composeView)
            composeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        if (intent?.getBooleanExtra("show_main_ui", false) == true) {
            showFloatingWindow()
        }
        if (intent?.getBooleanExtra("close_ui_silent", false) == true) {
            closeMainUI(silent = true)
        }
        return START_STICKY
    }
}
