package com.coc.zkqcode.utils.floatingwindows

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.delay

class MessageBoxService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performRestore(null)
    }
    override val savedStateRegistry: SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    private var messageX by mutableIntStateOf(1280)
    private var messageY by mutableIntStateOf(720)
    private var messageText by mutableStateOf("")
    private var messageFontSize by mutableStateOf(8.sp)
    private var messageDuration by mutableLongStateOf(2000L)
    private var isVisible by mutableStateOf(false)

    // To handle multiple concurrent requests or updates, we might need a trigger
    private var showTrigger by mutableLongStateOf(0L)

    override fun onCreate() {
        super.onCreate()
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showWindow()
    }

    private fun showWindow() {
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
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
            windowAnimations = 0 // Disable animations
        }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@MessageBoxService)
            setViewTreeSavedStateRegistryOwner(this@MessageBoxService)

            setContent {
                MessageBoxContent()
            }
        }

        windowManager.addView(composeView, params)
    }

    @Composable
    private fun MessageBoxContent() {
        var boxSize by remember { mutableStateOf(IntSize.Zero) }

        LaunchedEffect(showTrigger) {
            if (isVisible) {
                delay(messageDuration)
                isVisible = false
                stopSelf()
            }
        }

        if (isVisible) {

            // Effect to update window position based on size and target rb-corner
            LaunchedEffect(messageX, messageY, boxSize) {
                if (composeView != null && boxSize != IntSize.Zero) {
                    val params = composeView!!.layoutParams as WindowManager.LayoutParams

                    // messageX, messageY is the Right-Bottom corner.
                    // Top-Left = Right-Bottom - Size
                    val targetX = messageX - boxSize.width
                    val targetY = messageY - boxSize.height

                    params.x = targetX
                    params.y = targetY

                    try {
                        windowManager.updateViewLayout(composeView, params)
                    } catch (_: Exception) {
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .onGloballyPositioned { coordinates ->
                        boxSize = coordinates.size
                    }
            ) {
                Text(
                    text = messageText,
                    color = Color.White,
                    fontSize = messageFontSize,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateForegroundRecord()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        intent?.let {
            val text = it.getStringExtra("text") ?: ""
            if (text.isNotEmpty()) {
                messageText = text

                // Defaults
                val x = it.getIntExtra("x", 1280)
                val y = it.getIntExtra("y", 720)
                val size = it.getFloatExtra("fontSize", 15f) // passed as float sp value
                val duration = it.getLongExtra("duration", 2000L)

                messageX = x
                messageY = y
                messageFontSize = size.sp
                messageDuration = duration

                isVisible = true
                showTrigger++

            }
        }

        return START_NOT_STICKY
    }

    private fun updateForegroundRecord() {
        val notification = NotificationHelper.createNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE or
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            } else {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            }
            startForeground(1000, notification, type)
        } else {
            startForeground(1000, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (composeView != null) {
            try {
                windowManager.removeViewImmediate(composeView)
            } catch (_: IllegalArgumentException) {
                // View not attached
            }
            composeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
