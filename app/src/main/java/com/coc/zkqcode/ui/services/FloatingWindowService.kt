package com.coc.zkqcode.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.ui.pages.CheckRootScreen
import com.coc.zkqcode.ui.pages.ControlWindow
import com.coc.zkqcode.ui.pages.HomeScreen
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection
import kotlin.math.roundToInt

class FloatingWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null
    private var controlComposeView: ComposeView? = null

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "floating_service_channel"
            val channelName = "紫孔雀服务"
            val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

            if (manager.getNotificationChannel(channelId) == null) {
                val channel = android.app.NotificationChannel(
                    channelId, channelName,
                    android.app.NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }

            val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setContentTitle("紫孔雀")
                .setContentText("紫孔雀服务正在运行")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
                .build()

            startForeground(1001, notification)
        }

        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showFloatingWindow()
        showControlWindow()
    }

    @Suppress("AssignedValueIsNeverRead")
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
            x = 0
            y = 200
        }

        controlComposeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingWindowService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWindowService)

            setContent {
                var currentX by remember { mutableIntStateOf(params.x) }
                ControlWindowContainer(
                    currentX = currentX,
                    onPositionUpdate = { dx, dy ->
                        params.x += dx
                        params.y += dy
                        currentX = params.x
                        windowManager.updateViewLayout(this, params)
                    },
                    onSnapToEdge = { finalX ->
                        params.x = finalX
                        currentX = params.x
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
        onPositionUpdate: (Int, Int) -> Unit,
        onSnapToEdge: (Int) -> Unit
    ) {
        var interactionCount by remember { mutableIntStateOf(0) }
        var componentSize by remember { mutableStateOf(Size.Zero) }
        var isAtRightSide by remember { mutableStateOf(false) }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        val currentXState = rememberUpdatedState(currentX)
        val screenWidthState = rememberUpdatedState(screenWidth)

        // Keep the window at the right edge when its size changes (e.g. expands)
        LaunchedEffect(componentSize.width, isAtRightSide) {
            if (isAtRightSide && componentSize.width > 0) {
                val finalX = screenWidth - componentSize.width.toInt()
                onSnapToEdge(finalX)
            }
        }

        Box(
            modifier = Modifier
                .onGloballyPositioned { componentSize = it.size.toSize() }
        ) {
            ControlWindow(
                externalInteractionCount = interactionCount,
                isAtRightSide = isAtRightSide,
                onOpenMainUI = {
                    if (composeView == null) {
                        showFloatingWindow()
                    }
                },
                onDragStart = {
                    interactionCount++
                },
                onDragEnd = {
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

    private fun showFloatingWindow() {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val windowWidth = (screenWidth * 0.95).toInt()
        val windowHeight = (screenHeight * 0.95).toInt()

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
            setViewTreeLifecycleOwner(this@FloatingWindowService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWindowService)

            setContent {
                MainScreenContent()
            }
        }

        windowManager.addView(composeView, params)
    }

    @Composable
    private fun MainScreenContent() {
        var configLoaded by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            if (GlobalVars.fileActions == null) {
                val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                GlobalVars.fileActions = FileActions(serverConnection) {
                    configLoaded = true
                }
            } else {
                configLoaded = true
            }
        }

        CheckRootScreen {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    if (configLoaded) {
                        HomeScreen(onSaveSuccess = {
                            if (composeView != null) {
                                windowManager.removeView(composeView)
                                composeView = null
                            }
                        })
                    } else {
                        Text("正在加载配置...", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (composeView != null) {
            windowManager.removeView(composeView)
        }
        if (controlComposeView != null) {
            windowManager.removeView(controlComposeView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_STICKY
    }
}