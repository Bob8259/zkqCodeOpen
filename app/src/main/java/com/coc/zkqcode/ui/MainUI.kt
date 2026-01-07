package com.coc.zkqcode.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.ui.pages.HomeScreen
import com.coc.zkqcode.ui.pages.CheckRootScreen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection
import com.coc.zkqcode.services.FloatingWindowService

@Composable
fun MainScreen() {
    CheckRootScreen {
        MainScreenContent()
    }
}

@Composable
private fun MainScreenContent() {
    val context = LocalContext.current
    var configLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // 启动悬浮窗服务
        val serviceIntent = Intent(context, FloatingWindowService::class.java)
        context.startService(serviceIntent)
        
        if (GlobalVars.fileActions == null) {
            val serverConnection = ServerConnection("ws://localhost:6839/zkq") // Updated port from ServerDoc.md
            GlobalVars.fileActions = FileActions(serverConnection) {
                configLoaded = true
            }
        } else {
            configLoaded = true
        }
    }
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
