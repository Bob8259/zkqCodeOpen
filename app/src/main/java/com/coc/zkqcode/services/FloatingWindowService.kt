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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.ui.components.CustomButton
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.ui.pages.single.HomeScreen
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection

class FloatingWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "floating_service_channel"
            val channelName = "紫孔雀服务"
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
                .setContentText("紫孔雀服务正在运行")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            startForeground(1001, notification)
        }

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
        val actions = GlobalVars.fileActions
        val configLoaded = remember(actions?.configJson) {
            actions != null && actions.configJson.size() > 0
        }

        LaunchedEffect(Unit) {
            if (GlobalVars.fileActions == null) {
                val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                GlobalVars.fileActions = FileActions(serverConnection)
            }
        }

        MainUI(configLoaded)
    }

    @Composable
    private fun MainUI(configLoaded: Boolean) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (configLoaded) {
                    HomeScreen(onSaveSuccess = {
                        closeMainUI()
                    })
                } else {
                    Text("正在加载配置...", modifier = Modifier.padding(16.dp))
                    CustomButton(
                        text = "停止加载并退出",
                        onClick = { stopSelf() }
                    )
                }
            }
        }
    }

    private fun closeMainUI() {
        if (composeView != null) {
            windowManager.removeView(composeView)
            composeView = null
        }
        val intent = Intent(this, ControlWindowService::class.java)
        startService(intent)
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
        return START_STICKY
    }
}
