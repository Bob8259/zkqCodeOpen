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
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import android.content.res.Configuration

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density


class UIWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner,
    OnBackPressedDispatcherOwner {

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

    private val customViewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = customViewModelStore

    private val _onBackPressedDispatcher = OnBackPressedDispatcher {
        // Fallback action when back is pressed and no other callback handles it
        // For a floating window, we might want to close navigation or the window
        // But for now, we just leave it empty or log.
        // If we want to support closing the window on back press when nav stack is empty:
        // closeMainUI() 
    }
    override val onBackPressedDispatcher: OnBackPressedDispatcher
        get() = _onBackPressedDispatcher

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val loadjar = Loadjar(this)
    private val loadStatus = mutableStateOf("加载中...")
    private var isLoadStarted = false

    override fun onCreate() {
        super.onCreate()

        val notification = NotificationHelper.createNotification(this)
        startForeground(1000, notification)

        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        if (!isLoadStarted) {
            isLoadStarted = true
            serviceScope.launch {
                loadjar.startLoading {
                    loadStatus.value = it
                }
            }
        }

        showFloatingWindow()
    }

    private fun showFloatingWindow() {
        if (composeView != null) return

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val windowHeight =
            if (AppStateManager.currentMode == AppMode.Main) (screenHeight * 0.9).toInt() else (screenHeight * 0.7).toInt()

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
            setViewTreeLifecycleOwner(this@UIWindowService)
            setViewTreeSavedStateRegistryOwner(this@UIWindowService)
            setViewTreeViewModelStoreOwner(this@UIWindowService)
            setViewTreeOnBackPressedDispatcherOwner(this@UIWindowService)

            setContent {
                if (AppStateManager.currentMode != AppMode.Run) {
                    FixedDpiTheme {
                        loadjar.LoadAndShowUI(loadStatus = loadStatus.value, onClose = {

                            when (AppStateManager.currentMode) {
                                AppMode.SwitchAccount -> {
                                    updateWindowLayout(
                                        displayMetrics.widthPixels,
                                        (displayMetrics.heightPixels * 0.7).toInt()
                                    )
                                }

                                AppMode.Main -> {
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
                } else {
                    closeMainUI()
                }
            }
        }

        windowManager.addView(composeView, windowParams)
    }

    @Composable
    fun FixedDpiTheme(targetDpi: Float = 300f, content: @Composable () -> Unit) {
        val targetDensityValue = targetDpi / 160f // 计算出 300 DPI 对应的 density

        // 创建自定义的 Density 实例
        // fontScale = 1f 表示不跟随系统设置的字体大小（大号字体模式）缩放
        val customDensity = Density(
            density = targetDensityValue,
            fontScale = 1f
        )

        CompositionLocalProvider(LocalDensity provides customDensity) {
            content()
        }
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val windowHeight =
            if (AppStateManager.currentMode == AppMode.Main) (screenHeight * 0.9).toInt() else (screenHeight * 0.7).toInt()

        updateWindowLayout(screenWidth, windowHeight)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        customViewModelStore.clear()
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
