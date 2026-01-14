package com.coc.zkqcode.utils

import android.content.Context
import android.content.Intent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import com.coc.zkqcode.utils.floatingwindows.UIWindowService
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.GlobalVars

enum class RootStatus {
    CHECKING,
    ROOT_DENIED,      // 没有Root权限
    PERMISSION_DENIED, // 有Root但静默授权失败，需手动跳转
    WAITING_FOR_SERVER, // 等待服务器启动
    SERVER_ERROR,      // 连接服务器失败
    GRANTED           // 全部权限已就绪
}

@Composable
fun CheckRootScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf(RootStatus.CHECKING) }

    // 使用 LaunchedEffect 监听并检测
    LaunchedEffect(Unit) {
        status = checkAndGrantPermissions(context) { newStatus ->
            status = newStatus
        }
    }

    when (status) {
        RootStatus.CHECKING -> {
            FullScreenMessage("正在检测Root环境...")
        }

        RootStatus.ROOT_DENIED -> {
            FullScreenMessage(
                "请授予 Root 权限（软件名：紫孔雀）否则无法正常运行 "
            )
        }

        RootStatus.PERMISSION_DENIED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FullScreenMessage(
                    "Root 授权成功，但仍需手动开启悬浮窗和通知权限。开启后请重启软件 。"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }) {
                    Text("去设置中心开启")
                }
            }
        }

        RootStatus.WAITING_FOR_SERVER -> {
            FullScreenMessage("等待Root服务器启动...")
        }

        RootStatus.SERVER_ERROR -> {
            FullScreenMessage("连接到Root服务器失败")
        }

        RootStatus.GRANTED -> {
            // Start the floating window service when root check passes
            LaunchedEffect(Unit) {
                val serviceIntent = Intent(context, UIWindowService::class.java).apply {
                    putExtra("show_main_ui", true)
                }
                context.startService(serviceIntent)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FullScreenMessage("权限检查通过，正在显示主界面...\n若未能自动显示，请手动点击按钮显示主界面")
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    text = "显示主界面",
                    onClick = {
                        val serviceIntent = Intent(context, UIWindowService::class.java).apply {
                            putExtra("show_main_ui", true)
                        }
                        GlobalVars.isAutoRunEnabled = true
                        GlobalVars.autoRunTimer = 60
                        context.startService(serviceIntent)
                    }
                )
            }
        }
    }
}

/**
 * 核心检测逻辑
 */
private suspend fun checkAndGrantPermissions(
    context: Context,
    onStatusChange: (RootStatus) -> Unit
): RootStatus =
    withContext(Dispatchers.IO) {
        val shell = Shell.getShell()
        // 1. 先检测 Root 权限
        if (!shell.isRoot) {
            return@withContext RootStatus.ROOT_DENIED
        }

        // 2. 如果有 Root，尝试静默授权
        val pkg = context.packageName
        Shell.cmd(
            "pm grant $pkg android.permission.SYSTEM_ALERT_WINDOW",
            "appops set $pkg SYSTEM_ALERT_WINDOW allow",
            "pm grant $pkg android.permission.POST_NOTIFICATIONS",
            "pm grant $pkg android.permission.FOREGROUND_SERVICE",
        ).exec()

        // 3. 再次检测权限是否真的拿到了（因为部分系统 pm grant 对悬浮窗无效）
        val hasOverlay = Settings.canDrawOverlays(context)
        val hasNotification = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (hasOverlay && hasNotification) {
            val serverStarted = com.coc.zkqcode.utils.daemon.ServerManager.startServer(context)
            if (!serverStarted) {
                return@withContext RootStatus.SERVER_ERROR
            }

            // 等待服务器响应
            withContext(Dispatchers.Main) {
                onStatusChange(RootStatus.WAITING_FOR_SERVER)
            }

            var response: String? = null
            var attempts = 0
            while (response == null && attempts < 10) {
                response = waitForServerResponse()
                if (response == null) {
                    println("Server not responding, retrying... (Attempt ${attempts + 1})")
                    com.coc.zkqcode.utils.daemon.ServerManager.startServer(context)
                    delay(1000)
                    attempts++
                }
            }

            if (response == null) {
                return@withContext RootStatus.SERVER_ERROR
            }
        }

        return@withContext RootStatus.GRANTED
    }

private suspend fun waitForServerResponse(): String? {
    val client = OkHttpClient()
    val request = Request.Builder().url("ws://localhost:6839/zkq").build()
    val deferred = CompletableDeferred<String?>()

    client.newWebSocket(request, object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            // 发送一条测试消息
            webSocket.send("""{"actionType": "system_action", "subAction": "read"}""")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            deferred.complete(text)
            webSocket.close(1000, "Done")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            deferred.complete(null)
        }
    })

    return withTimeoutOrNull(10000) { // 最多等待10秒
        deferred.await()
    }

}

@Composable
private fun FullScreenMessage(message: String) {
    Text(
        text = message,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    )
}