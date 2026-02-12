package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.loadjar.Loadjar
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UIWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner,
    OnBackPressedDispatcherOwner {

    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val HEIGHT_RATIO_MAIN = 0.9f
        private const val HEIGHT_RATIO_DEFAULT = 0.7f
        private const val TARGET_DPI = 300f
        private const val DEFAULT_LOAD_STATUS = "加载中..."
    }

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null
    private lateinit var windowParams: WindowManager.LayoutParams

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performRestore(null)
    }
    override val savedStateRegistry: SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    private val customViewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore = customViewModelStore

    private val _onBackPressedDispatcher = OnBackPressedDispatcher {
        // Fallback action for back press
    }
    override val onBackPressedDispatcher: OnBackPressedDispatcher = _onBackPressedDispatcher

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val loadjar by lazy { Loadjar(this) }
    private val loadStatus = mutableStateOf(DEFAULT_LOAD_STATUS)
    private var isLoadStarted = false

    override fun onCreate() {
        super.onCreate()
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startJarLoading()
        showFloatingWindow()
        serviceScope.launch(Dispatchers.IO) {
            while (true) {
                val configCount = GlobalVars.configStates["config_count"]?.value
                if (GlobalVars.isConfigLoaded && configCount == null) {
                    Shell.cmd("am start -n com.coc.zkqcode/.MainActivity >>/dev/null 2>&1").exec()
                    GlobalVars.autoRunTimer = 5
                }
                delay(1000)
            }
        }

    }

    private fun startJarLoading() {
        if (!isLoadStarted) {
            isLoadStarted = true
            serviceScope.launch {
                loadjar.startLoading { status ->
                    loadStatus.value = status
                }
            }
        }
    }

    private fun showFloatingWindow() {
        if (composeView != null) return

        val (width, height) = calculateWindowSize()
        val windowType = getWindowType()

        windowParams = WindowManager.LayoutParams(
            width,
            height,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            windowAnimations = 0
        }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@UIWindowService)
            setViewTreeSavedStateRegistryOwner(this@UIWindowService)
            setViewTreeViewModelStoreOwner(this@UIWindowService)
            setViewTreeOnBackPressedDispatcherOwner(this@UIWindowService)

            setContent {
                if (AppStateManager.currentMode != AppMode.Run) {
                    FixedDpiTheme {
                        loadjar.LoadAndShowUI(loadStatus = loadStatus.value, onClose = {
                            handleUIIClose()
                        })
                    }
                } else {
                    closeMainUI()
                }
            }
        }

        windowManager.addView(composeView, windowParams)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    private fun handleUIIClose() {
        when (AppStateManager.currentMode) {
            AppMode.SwitchAccount, AppMode.Main -> {
                updateWindowSizeForCurrentMode()
            }

            else -> {
                closeMainUI()
                GlobalVars.isPlaying.value = true
                startService(Intent(this, ControlWindowService::class.java))
            }
        }
    }

    @Composable
    private fun FixedDpiTheme(targetDpi: Float = TARGET_DPI, content: @Composable () -> Unit) {
        val targetDensityValue = targetDpi / 160f
        val customDensity = Density(density = targetDensityValue, fontScale = 1f)

        CompositionLocalProvider(LocalDensity provides customDensity) {
            content()
        }
    }

    private fun calculateWindowSize(): Pair<Int, Int> {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val height = when (AppStateManager.currentMode) {
            AppMode.SwitchAccount -> WindowManager.LayoutParams.WRAP_CONTENT
            AppMode.Main -> (screenHeight * HEIGHT_RATIO_MAIN).toInt()
            else -> (screenHeight * HEIGHT_RATIO_DEFAULT).toInt()
        }

        return Pair(screenWidth, height)
    }

    private fun updateWindowSizeForCurrentMode() {
        if (composeView != null && ::windowParams.isInitialized) {
            val (width, height) = calculateWindowSize()
            windowParams.width = width
            windowParams.height = height
            windowManager.updateViewLayout(composeView, windowParams)
        }
    }

    private fun getWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
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
        updateWindowSizeForCurrentMode()
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
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        showFloatingWindow()
        return START_STICKY
    }

    private fun updateForegroundRecord() {
        val notification = NotificationHelper.createNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            }
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
}
