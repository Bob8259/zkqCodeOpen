package com.coc.zkqcode.utils.checkroot

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.database.ConfigManager
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.floatingwindows.UIWindowService
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager
import com.coc.zkqcode.utils.websocket.ServerConnection
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.util.Log
import com.coc.zkqcode.utils.daemon.ServerManager
import java.io.File

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
    var isConfigInitialized by remember { mutableStateOf(false) }

    // 使用 LaunchedEffect 监听并检测
    LaunchedEffect(Unit) {
        status = checkAndGrantPermissions(context) { newStatus ->
            status = newStatus
            // Reset config initialization if status changes back from granted (though unlikely in this flow)
            if (newStatus != RootStatus.GRANTED) {
                isConfigInitialized = false
            }
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
            // Initialize FileActions and Configs once Root is GRANTED
            LaunchedEffect(Unit) {
                if (GlobalVars.fileActions == null) {
                    val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                    GlobalVars.fileActions = FileActions(serverConnection)
                }

                // Initialize states from Schema
                val actions = GlobalVars.fileActions
                if (actions != null) {
                    // Wait for configs to load
                    snapshotFlow { actions.isLoading }.collect { isLoading ->
                        if (!isLoading) {
                            ConfigManager.initializeAllConfigs(actions)
                            isConfigInitialized = true
                        }
                    }
                }
            }

            if (!isConfigInitialized) {
                FullScreenMessage("正在初始化配置文件...")
            } else {
                // Start the floating window service when root check passes AND config is initialized
                LaunchedEffect(Unit) {
                    val serviceIntent = Intent(context, UIWindowService::class.java)
                    context.startService(serviceIntent)
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FullScreenMessage("权限检查通过，配置加载完成。\n正在显示主界面...\n若未能自动显示，请手动点击按钮显示主界面")
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(
                        text = "显示主界面",
                        onClick = {
                            AppStateManager.setMode(AppMode.Main)
                            val serviceIntent = Intent(context, UIWindowService::class.java)
                            GlobalVars.isAutoRunEnabled = true
                            GlobalVars.autoRunTimer = 60
                            GlobalVars.updateWindowPosition = false
                            context.startService(serviceIntent)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
            val serverStarted = ServerManager.startServer(context)
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
                    ServerManager.startServer(context)
                    delay(1000)
                    attempts++
                }
            }

            if (response == null) {
                return@withContext RootStatus.SERVER_ERROR
            }
        }

        // 启动守护进程
        setupAndRunDaemon(context)

        return@withContext RootStatus.GRANTED
    }

private suspend fun setupAndRunDaemon(context: Context) {
    val scriptPath = "/data/local/tmp/zkq_daemon.sh"
    val pkg = context.packageName

    val scriptContent = listOf(
        "#!/system/bin/sh",
        "",
        "while true; do",
        "    if ! pidof $pkg > /dev/null; then",
        "        am start -n $pkg/.MainActivity >>/dev/null 2>&1",
        "    fi",
        "    sleep 10",
        "done"
    )
    try {
        // 1. 创建脚本目录
        val scriptFile = File(scriptPath)
        val scriptDir = scriptFile.parentFile
        if (scriptDir != null && !scriptDir.exists()) {
            Shell.cmd("mkdir -p ${scriptDir.absolutePath}").exec()
            Shell.cmd("chmod 777 ${scriptDir.absolutePath}").exec()
        }

        // 2. 清空并写入脚本内容
        Shell.cmd("echo \"\" > $scriptPath").exec()
        for (line in scriptContent) {
            // 使用单引号包裹内容，防止 shell 解析特殊字符
            Shell.cmd("echo '${line}' >> $scriptPath").exec()
        }

        // 3. 赋予执行权限
        Shell.cmd("chmod 755 $scriptPath").exec()

        // 4. 检查进程是否已在运行 (通过两次采样确认)
        val firstPids = Shell.cmd("ps -ef | grep '$scriptPath' | grep -v grep")
            .exec().out.mapNotNull { line ->
                line.split("\\s+".toRegex()).getOrNull(1)
            }.toMutableList()

        delay(500) // 采样间隔

        val secondPids = Shell.cmd("ps -ef | grep '$scriptPath' | grep -v grep")
            .exec().out.mapNotNull { line ->
                line.split("\\s+".toRegex()).getOrNull(1)
            }.toMutableList()

        // 如果两次有重复的PID说明进程持续存在
        if (firstPids.intersect(secondPids.toSet()).isNotEmpty()) {
            Log.d("CheckRoot", "守护进程已在运行")
            return
        }

        // 5. 启动守护进程
        val cmdStart = "nohup sh $scriptPath > /dev/null 2>&1 &"
        Shell.cmd(cmdStart).exec()
        Log.d("CheckRoot", "守护启动成功")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("CheckRoot", "守护启动失败: ${e.message}")
    }
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