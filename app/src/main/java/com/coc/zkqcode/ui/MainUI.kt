package com.coc.zkqcode.ui

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.ui.services.FloatingWindowService
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.ui.pages.single.CheckRootScreen
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection

@Composable
fun MainScreen() {
    CheckRootScreen {
        MainScreenContent()
    }
}

@Composable
private fun MainScreenContent() {
    val context = LocalContext.current

    // 定义启动服务的函数，方便多处复用
    val startFloatingService = {
        val serviceIntent = Intent(context, FloatingWindowService::class.java)

        // 判断 API 版本是否大于等于 26 (Android 8.0 O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 使用前台服务启动方式
            context.startForegroundService(serviceIntent)
        } else {
            // 旧版本使用普通启动方式
            context.startService(serviceIntent)
        }
    }

    // 1. 自动启动逻辑：LaunchedEffect(Unit) 确保只在初次进入页面时触发一次
    LaunchedEffect(Unit) {
        // 自动启动悬浮窗
        startFloatingService()

        // 保持原有的配置加载逻辑
        if (GlobalVars.fileActions == null) {
            val serverConnection = ServerConnection("ws://localhost:6839/zkq")
            GlobalVars.fileActions = FileActions(serverConnection) {
                // 配置加载完成后的回调
            }
        }
    }

    // 2. UI 界面：添加手动启动按钮
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Button(
            onClick = {
                // 手动点击启动悬浮窗
                startFloatingService()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "显示界面")
        }
    }
}