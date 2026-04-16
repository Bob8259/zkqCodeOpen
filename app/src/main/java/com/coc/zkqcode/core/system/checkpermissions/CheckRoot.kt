package com.coc.zkqcode.core.system.checkpermissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
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
import com.coc.zkqcode.MainActivity
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.websocket.ServerActions
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.core.ui.localcomponents.LocalCustomButton
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.topjohnwu.superuser.Shell

// Local color palette mirroring AppColors from the jar module
private val ColorPrimary     = Color(0xFF4299E1)
private val ColorBackground  = Color(0xFFE8F4FC)
private val ColorCardBg      = Color(0xA6FFFFFF)
private val ColorTextPrimary = Color(0xFF2D3748)

@Composable
fun CheckRootScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf(RootStatus.CHECKING) }

    // 使用 LaunchedEffect 监听并检测
    LaunchedEffect(Unit) {
        CheckIntergrity.checkAppIntegrity(context)
        Shell.cmd("adb shell input keyevent 4").exec()
        status = PermissionManager.checkAndGrantPermissions(context) { newStatus ->
            status = newStatus
        }
    }

    // Full-screen light-blue background shared by all states
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            RootStatus.CHECKING -> {
                FullScreenMessage("正在检测Root环境...")
            }

            RootStatus.ROOT_DENIED -> {
                FullScreenMessage("请授予 Root 权限后，再启动本软件（本软件名：紫孔雀）\n若授权失败，请尝试重启软件/设备。")
            }

            RootStatus.PERMISSION_DENIED -> {
                FullScreenMessage(
                    message = "Root 授权成功，但仍需手动开启悬浮窗和通知权限。开启后请重启软件。"
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                    ) {
                        Text("去设置中心开启", style = MaterialTheme.typography.labelMedium)
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
                        // Pass the local WS server instance established by ServerHelper (may be null for shell fallback)
                        GlobalVars.serverActions = ServerActions(ServerHelper.localServer)
                    }

                    // Start the floating window service AFTER serverActions is ready
                    val serviceIntent = Intent(context, UIWindowService::class.java)
                    context.startService(serviceIntent)
                    (context as? MainActivity)?.requestMediaProjection()
                }

                FullScreenMessage(
                    message = "权限检查通过，配置加载完成。\n\n若未能自动显示主界面，请手动点击按钮"
                ) {
                    LocalCustomButton(
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

/**
 * A simple glass-style status card with a message body and an optional action button slot.
 *
 * @param message The status message to display.
 * @param action  Optional composable slot for an action button below the message.
 */
@Composable
fun FullScreenMessage(
    message: String,
    action: (@Composable () -> Unit)? = null
) {
    val cardShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .clip(cardShape)
            .background(ColorCardBg)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = ColorTextPrimary
            )

            if (action != null) {
                Spacer(modifier = Modifier.height(20.dp))
                action()
            }
        }
    }
}
