package com.coc.zkqcode.core.system.checkpermissions

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
import com.coc.zkqcode.MainActivity
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.websocket.ServerActions
import com.coc.zkqcode.core.data.websocket.ServerConnection
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.topjohnwu.superuser.Shell

@Composable
fun CheckRootScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf(RootStatus.CHECKING) }
    var isConfigInitialized by remember { mutableStateOf(false) }

    // 使用 LaunchedEffect 监听并检测
    LaunchedEffect(Unit) {
        CheckIntergrity.checkAppIntegrity(context)
        Shell.cmd("adb shell input keyevent 4").exec()
        status = PermissionManager.checkAndGrantPermissions(context) { newStatus ->
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
            // Initialize ServerActions and Configs once Root is GRANTED
            LaunchedEffect(Unit) {
                // Get and store default IME
                if (GlobalVars.defaultInputMethod == null) {
                    val result = Shell.cmd("settings get secure default_input_method").exec()
                    if (result.isSuccess && result.out.isNotEmpty()) {
                        GlobalVars.defaultInputMethod = result.out[0]
                    }
                }

                if (GlobalVars.serverActions == null) {
                    val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                    GlobalVars.serverActions = ServerActions(serverConnection)
                }

                // Initialize states from Schema
                val actions = GlobalVars.serverActions
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
                    (context as? MainActivity)?.requestMediaProjection()
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
                }
            }
        }
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
