package com.coc.zkqcode.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.toSize
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.ui.pages.control.ControlWindow
import kotlin.math.roundToInt

class ControlWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var controlComposeView: ComposeView? = null

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
            val channelId = "control_service_channel"
            val channelName = "紫孔雀控制服务"
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId, channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("紫孔雀")
                .setContentText("控制窗口正在运行")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            startForeground(1002, notification)
        }

        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
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
            setViewTreeLifecycleOwner(this@ControlWindowService)
            setViewTreeSavedStateRegistryOwner(this@ControlWindowService)

            setContent {
                var currentX by remember { mutableIntStateOf(params.x) }
                ControlWindowContainer(
                    currentX = currentX,
                    onPositionUpdate = { dx, dy ->
                        params.x += dx
                        params.y += dy
                        currentX =
                            params.x //do not remove this. The floating window won't move without this line
                        windowManager.updateViewLayout(this, params)
                    },
                    onSnapToEdge = { finalX ->
                        params.x = finalX
                        currentX =
                            params.x //do not remove this. The floating window won't move without this line
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
                    val intent =
                        Intent(this@ControlWindowService, FloatingWindowService::class.java).apply {
                            putExtra("show_main_ui", true)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    startService(intent)
                    stopSelf()
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

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (controlComposeView != null) {
            windowManager.removeView(controlComposeView)
            controlComposeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_STICKY
    }
}
