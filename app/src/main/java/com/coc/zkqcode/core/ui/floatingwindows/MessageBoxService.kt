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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.coc.zkqcode.core.ui.localcomponents.LocalCustomButton
import com.coc.zkqcode.core.util.exit.AppExitHelper
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

// Lightweight message payload for in-process delivery via SharedFlow
data class MessageData(
    val text: String, val x: Int, val y: Int, val fontSize: Float, val duration: Long
)

// Ad item from the server API
data class AdItem(
    val content: String, val link: String?, val topAd: Int
)

// Payload carrying ad items and the display duration for the overlay
data class AdOverlayData(
    val items: List<AdItem>, val durationSeconds: Int
)

class MessageBoxService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    companion object {
        private const val INACTIVITY_TIMEOUT_MS = 2500L

        // Flag to let MessageBoxHelper bypass Binder IPC when the service is alive
        val isRunning = AtomicBoolean(false)

        // In-process message channel; extraBufferCapacity ensures tryEmit() never fails
        val messageFlow = MutableSharedFlow<MessageData>(
            extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        // Ad overlay channel; null signals dismiss
        val adFlow = MutableSharedFlow<AdOverlayData?>(
            extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    // Defer performRestore() to onCreate() so class-construction failures
    // cannot prevent startForeground() from being called.
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry

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

    // --- Ad overlay state (second window, independent of debug messages) ---
    private var adComposeView: ComposeView? = null
    private var adItems by mutableStateOf<List<AdItem>>(emptyList())
    private var isAdVisible by mutableStateOf(false)
    private var adDurationSeconds by mutableIntStateOf(15)
    private var adCountdown by mutableIntStateOf(15)

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

        // Collect ad overlay show/dismiss signals
        serviceScope.launch {
            adFlow.collect { data ->
                if (data != null) {
                    adItems = data.items.sortedByDescending { it.topAd }
                    adDurationSeconds = data.durationSeconds
                    adCountdown = data.durationSeconds
                    isAdVisible = true
                    showAdWindow()
                } else {
                    isAdVisible = false
                    removeAdWindow()
                }
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

    // Cancel and restart the inactivity timer; service stops after INACTIVITY_TIMEOUT_MS of silence.
    // While the ad overlay is visible, skip self-stop to keep the service alive.
    private fun resetInactivityTimer() {
        inactivityJob?.cancel()
        inactivityJob = serviceScope.launch {
            delay(INACTIVITY_TIMEOUT_MS)
            if (!isAdVisible) {
                stopSelf()
            }
        }
    }

    private fun showWindow() {
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
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
                    }) {
                Text(
                    text = messageText, color = Color.White, fontSize = messageFontSize, fontWeight = FontWeight.Normal
                )
            }
        }
    }

    // --- Ad overlay window management ---

    private fun showAdWindow() {
        if (adComposeView != null) return

        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }

        val displayMetrics = resources.displayMetrics
        val adParams = WindowManager.LayoutParams(
            displayMetrics.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT, windowType,
            // Focusable so links are clickable (no FLAG_NOT_FOCUSABLE)
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            windowAnimations = 0
        }

        adComposeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@MessageBoxService)
            setViewTreeSavedStateRegistryOwner(this@MessageBoxService)

            setContent {
                AdOverlayContent()
            }
        }

        try {
            windowManager.addView(adComposeView, adParams)
        } catch (e: Exception) {
            Timber.e(e, "MessageBoxService: showAdWindow() failed")
            adComposeView = null
        }
    }

    private fun removeAdWindow() {
        if (adComposeView != null) {
            try {
                windowManager.removeViewImmediate(adComposeView)
            } catch (_: IllegalArgumentException) {
                // View not attached
            }
            adComposeView = null
        }
    }

    @Composable
    private fun AdOverlayContent() {
        val context = LocalContext.current
        // Cap the overlay height at 95% of screen to prevent overflow on long ad lists
        val screenHeightDp = LocalConfiguration.current.screenHeightDp

        // Countdown timer that ticks every second using dynamic duration
        LaunchedEffect(isAdVisible, adDurationSeconds) {
            if (isAdVisible) {
                for (remaining in adDurationSeconds downTo 1) {
                    adCountdown = remaining
                    delay(1000L)
                }
                adCountdown = 0
            }
        }

        if (isAdVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = (screenHeightDp * 0.95f).dp)
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "广告剩余: ${adCountdown}秒",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Use weight so the list flexes within the height-constrained parent
                        AdList(context)

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color.LightGray
                        )

                        Text(
                            text = "官网注册账号并赞助，可以免广告。每天仅需0.25卡班积分，用多久扣多少，精确到分钟。\n\n免费用户不限制多开数量，但多开超过2个账号后广告时间会成比例增加。广告播放时，只能退出辅助或等待，不能进行其他操作。",
                            fontSize = 8.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        // Exit button to let users quit the app during ad playback
                        LocalCustomButton(
                            text = "退出辅助",
                            onClick = { AppExitHelper.exitApplication(context) }
                        )
                    }
                }
            }
        }
    }

    // Extracted ad list into a ColumnScope extension so weight() modifier is available
    @Composable
    private fun ColumnScope.AdList(context: android.content.Context) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(adItems) { item ->
                if (item.link != null) {
                    Text(
                        text = item.content,
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW, item.link.toUri()
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Timber.e(e, "Failed to open ad link")
                                }
                            }
                            .padding(vertical = 4.dp)
                    )
                } else {
                    Text(
                        text = item.content,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
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
                        text = text, x = it.getIntExtra("x", 1280), y = it.getIntExtra("y", 720), fontSize = it.getFloatExtra("fontSize", 15f), duration = it.getLongExtra("duration", 2000L)
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
                    1000, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
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
        removeAdWindow()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
