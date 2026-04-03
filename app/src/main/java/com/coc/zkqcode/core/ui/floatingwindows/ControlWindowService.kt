package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.hotupdate.HotUpdateManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.topjohnwu.superuser.Shell
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ControlWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var controlComposeView: ComposeView? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var botJob: Job? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performRestore(null)
    }
    override val savedStateRegistry: SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showControlWindow()
        startBotLogic()
        startHotUpdateListener()
        startOomProtection()
    }

    private fun startBotLogic() {
        serviceScope.launch {
            snapshotFlow { AppStateManager.currentMode }
                .collect { mode ->
                    if (mode == AppMode.Run) {
                        if (botJob == null || !botJob!!.isActive) {
                            botJob = serviceScope.launch(Dispatchers.IO) {
                                GlobalVars.pluginUI?.runBot()
                            }
                        }
                    } else {
                        botJob?.cancel()
                        botJob = null
                        // Release any stuck touch pointers when bot stops
                        launch(Dispatchers.IO) {
                            TouchActions.releaseAllPointers()
                        }
                    }
                }
        }
    }


    // Launch the hot update signal listener and watchdog on background threads
    private fun startHotUpdateListener() {
        serviceScope.launch(Dispatchers.IO) {
            HotUpdateManager.listenForSignal(this@ControlWindowService)
        }
        serviceScope.launch(Dispatchers.IO) {
            HotUpdateManager.startWatchdog(this@ControlWindowService)
        }
    }

    // Periodically enforce oom_score_adj = -1000 to prevent OOM killer from targeting this process
    private fun startOomProtection() {
        serviceScope.launch(Dispatchers.IO) {
            val pid = Process.myPid()
            while (true) {
                try {
                    val current = RunShell.runAndGetFirst(
                        "cat /proc/$pid/oom_score_adj",
                        isCheckIsPlaying = false
                    )
                    if (current.trim() != "-1000") {
                        RunShell.runNoOutput(
                            "echo -1000 > /proc/$pid/oom_score_adj",
                            isCheckIsPlaying = false
                        )
                    }
                } catch (_: Exception) { }
                delay(5000)
            }
        }
    }

    private fun showControlWindow() {
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 1280
            y = 360
        }

        controlComposeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@ControlWindowService)
            setViewTreeSavedStateRegistryOwner(this@ControlWindowService)

            setContent {
                var currentX by remember { mutableIntStateOf(params.x) }
                var currentY by remember { mutableIntStateOf(params.y) }
                ControlWindowContainer(
                    currentX = currentX,
                    currentY = currentY,
                    onPositionUpdate = { dx, dy ->
                        params.x += dx
                        params.y += dy
                        currentX = params.x
                        currentY = params.y
                        windowManager.updateViewLayout(this, params)
                    },
                    onSnapToEdge = { finalX ->
                        params.x = finalX
                        currentX = params.x
                        windowManager.updateViewLayout(this, params)
                    },
                    onAutoPosition = { newX, newY ->
                        params.x = newX
                        params.y = newY
                        currentX = params.x
                        currentY = params.y
                        windowManager.updateViewLayout(this, params)
                    }
                )
            }
        }

        windowManager.addView(controlComposeView, params)
    }

    @Composable
    private fun ControlWindowContainer(
        currentX: Int,
        currentY: Int,
        onPositionUpdate: (Int, Int) -> Unit,
        onSnapToEdge: (Int) -> Unit,
        onAutoPosition: (Int, Int) -> Unit
    ) {
        var interactionCount by remember { mutableIntStateOf(0) }
        var componentSize by remember { mutableStateOf(Size.Zero) }
        var isAtRightSide by remember { mutableStateOf(true) }

        val windowInfo = LocalWindowInfo.current
        val screenWidth = windowInfo.containerSize.width
        val screenHeight = windowInfo.containerSize.height

        var isDragging by remember { mutableStateOf(false) }

        val currentXState = rememberUpdatedState(currentX)
        val screenWidthState = rememberUpdatedState(screenWidth)
        val screenHeightState = rememberUpdatedState(screenHeight)

        LaunchedEffect(componentSize.width, isAtRightSide, screenWidth) {
            if (isAtRightSide && componentSize.width > 0) {
                val finalX = screenWidth - componentSize.width.toInt()
                onSnapToEdge(finalX)
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                if (GlobalVars.updateWindowPosition && !isDragging) {
                    val absorbEdge = GlobalVars.absorbEdge
                    val absorbYPercentage = GlobalVars.absorbYPercentage

                    val newX =
                        if (absorbEdge == 1) 0 else (screenWidthState.value - componentSize.width).toInt()
                    val newY = (screenHeightState.value * (absorbYPercentage / 100f)).toInt()

                    isAtRightSide = absorbEdge != 1
                    onAutoPosition(newX, newY)
                }
            }
        }

        Box(
            modifier = Modifier
                .onGloballyPositioned { componentSize = it.size.toSize() }
        ) {
            ControlWindow(
                externalInteractionCount = interactionCount,
                isAtRightSide = isAtRightSide,
                currentX = currentX,
                currentY = currentY,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                onOpenMainUI = {
                    val intent =
                        Intent(this@ControlWindowService, UIWindowService::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    GlobalVars.isAutoRunEnabled = true
                    GlobalVars.autoRunTimer = 60
                    GlobalVars.updateWindowPosition = false
                    startService(intent)
                },
                onSwitchAccount = {
                    AppStateManager.setMode(AppMode.SwitchAccount)
                    startService(
                        Intent(
                            this@ControlWindowService,
                            UIWindowService::class.java
                        )
                    )
                },
                onDragStart = {
                    interactionCount++
                    isDragging = true
                },
                onDragEnd = {
                    isDragging = false
                    val currentXVal = currentXState.value
                    val screenWidthVal = screenWidthState.value
                    val finalX = if (currentXVal + componentSize.width / 2 < screenWidthVal / 2) {
                        isAtRightSide = false
                        0
                    } else {
                        isAtRightSide = true
                        (screenWidthVal - componentSize.width).toInt()
                    }
                    onSnapToEdge(finalX)
                },
                onDrag = { dx, dy ->
                    onPositionUpdate(dx.roundToInt(), dy.roundToInt())
                    interactionCount++
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (controlComposeView != null) {
            windowManager.removeView(controlComposeView)
            controlComposeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Shell.cmd("am start -n com.coc.zkqcode/.MainActivity >>/dev/null 2>&1").exec()
        GlobalVars.autoRunTimer = 5
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Detect START_STICKY restart: system delivers null intent after LMKD kill
        if (intent == null) {
            // Relaunch MainActivity to re-initialize the full app (JAR loading, etc.)
            Shell.cmd("am start -n com.coc.zkqcode/.MainActivity >>/dev/null 2>&1").exec()
            GlobalVars.autoRunTimer = 5
        }
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_STICKY
    }

    private fun updateForegroundRecord() {
        val notification = NotificationHelper.createNotification(this)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Only use MEDIA_PROJECTION type — SPECIAL_USE is not declared in
                // the manifest for this service, and would cause startForeground()
                // to throw on API 34+ (Android 14+).
                startForeground(
                    1000, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(1000, notification)
            }
        } catch (e: Exception) {
            Timber.e(e, "ControlWindowService: startForeground() failed, API=${Build.VERSION.SDK_INT}")
        }
    }
}