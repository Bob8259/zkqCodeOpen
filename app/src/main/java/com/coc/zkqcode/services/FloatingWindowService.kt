package com.coc.zkqcode.services

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.ui.pages.CheckRootScreen
import com.coc.zkqcode.ui.pages.HomeScreen
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection

class FloatingWindowService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    // --- Lifecycle 相关必备代码 ---
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry
    
    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performRestore(null)
    }
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showFloatingWindow()
    }

    private fun showFloatingWindow() {
        // 获取屏幕尺寸
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        
        // 计算95%的宽高
        val windowWidth = (screenWidth * 0.95).toInt()
        val windowHeight = (screenHeight * 0.95).toInt()
        
        val params = WindowManager.LayoutParams(
            windowWidth,
            windowHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // 关键：系统覆盖层
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or   // 允许触摸外部
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,     // 允许布局在屏幕内
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER  // 居中显示
        }

        composeView = ComposeView(this).apply {
            // 绑定生命周期所有者，否则 Compose 无法运行
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
                        HomeScreen()
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
    }

    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_STICKY
    }
}
