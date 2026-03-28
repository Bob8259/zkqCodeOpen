package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
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
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

// Lightweight message payload for in-process delivery via SharedFlow
data class MessageData(
    val text: String,
    val x: Int,
    val y: Int,
    val fontSize: Float,
    val duration: Long
)

class MessageBoxService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    companion object {
        private const val INACTIVITY_TIMEOUT_MS = 2500L

        // Flag to let MessageBoxHelper bypass Binder IPC when the service is alive
        val isRunning = AtomicBoolean(false)

        // In-process message channel; extraBufferCapacity ensures tryEmit() never fails
        val messageFlow = MutableSharedFlow<MessageData>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    // Defer performRestore() to onCreate() so class-construction failures
    // cannot prevent startForeground() from being called.
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry =
        savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var inactivityJob: Job? = null

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
        // Must call startForeground() before anything else to satisfy the
        // system contract from startForegroundService().
        updateForegroundRecord()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        isRunning.set(true)

        // Collect in-process messages delivered via SharedFlow (bypasses Binder IPC)
        serviceScope.launch {
            messageFlow.collect { msg ->
                applyMessage(msg)
            }
        }

        try {
            showWindow()
        } catch (e: Exception) {
            Timber.e(e, "MessageBoxService: showWindow() failed")
        }
    }

    // Apply message data to Compose state and reset the inactivity timer
    private fun applyMessage(msg: MessageData) {
        messageText = msg.text
        messageX = msg.x
        messageY = msg.y
        messageFontSize = msg.fontSize.sp
        messageDuration = msg.duration
        isVisible = true
        showTrigger++
        resetInactivityTimer()
    }

    // Cancel and restart the inactivity timer; service stops after INACTIVITY_TIMEOUT_MS of silence
    private fun resetInactivityTimer() {
        inactivityJob?.cancel()
        inactivityJob = serviceScope.launch {
            delay(INACTIVITY_TIMEOUT_MS)
            stopSelf()
        }
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
                // Service stays alive; inactivity timer handles shutdown
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

        // Handle cold-start messages delivered via Intent
        intent?.let {
            val text = it.getStringExtra("text") ?: ""
            if (text.isNotEmpty()) {
                applyMessage(
                    MessageData(
                        text = text,
                        x = it.getIntExtra("x", 1280),
                        y = it.getIntExtra("y", 720),
                        fontSize = it.getFloatExtra("fontSize", 15f),
                        duration = it.getLongExtra("duration", 2000L)
                    )
                )
            }
        }

        return START_NOT_STICKY
    }

    private var isForeground = false

    private fun updateForegroundRecord() {
        if (isForeground) return

        try {
            val notification = NotificationHelper.createNotification(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // API 34+: must specify a foreground service type matching the manifest
                startForeground(
                    1000, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
                )
            } else {
                startForeground(1000, notification)
            }
            isForeground = true
        } catch (e: Exception) {
            // If startForeground() fails the service is doomed — stop gracefully
            // instead of letting the system throw RemoteServiceException later.
            Timber.e(e, "MessageBoxService: startForeground() failed, API=${Build.VERSION.SDK_INT}")
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning.set(false)
        serviceScope.cancel()
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
