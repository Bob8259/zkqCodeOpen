package com.coc.zkqcode.core.system.checkpermissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.MainActivity
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.data.websocket.ServerActions
import com.coc.zkqcode.core.util.exit.AppExitHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.ui.floatingwindows.ControlWindowService
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.core.ui.localcomponents.LocalCustomButton
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.system.appupdate.AppUpdateChecker
import com.coc.zkqcode.core.system.appupdate.AppUpdateState
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Local colour palette mirroring AppColors from the jar module
private val ColorPrimary     = Color(0xFF4299E1)
private val ColorBackground  = Color(0xFFE8F4FC)
private val ColorCardBg      = Color(0xA6FFFFFF)
private val ColorTextPrimary = Color(0xFF2D3748)

/**
 * Returns true if the process is already fully initialized with a live server
 * connection. When MainActivity's task was removed (e.g. after a game-kill +
 * rotation) the foreground services and bot keep running, so reopening the app
 * creates a fresh MainActivity. In that case we must NOT re-run the startup
 * sequence, because it would kill the app_process client and tear down the live
 * WebSocket server the running bot depends on.
 */
private fun isAlreadyInitialized(): Boolean {
    if (GlobalVars.serverActions == null) return false
    return when (GlobalVars.serverConnectionMethod) {
        // For the local WS path, only reuse if the client session is still alive.
        ServerConnectionMethod.LOCAL_WS_SERVER ->
            ServerHelper.localServer?.isClientConnected() == true
        // Shell fallback methods are stateless, so an existing ServerActions is enough.
        ServerConnectionMethod.LIBSU_CURL,
        ServerConnectionMethod.LIBSU_WGET -> true
        null -> false
    }
}

@Composable
fun CheckRootScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf(RootStatus.CHECKING) }
    // Observe the app-update state to show download/install progress on screen
    val updateState by AppUpdateChecker.state.collectAsState()

    // Use LaunchedEffect to perform startup checks without blocking the main thread.
    LaunchedEffect(Unit) {
        // Idempotent startup: if the process is already initialized and the
        // server connection is still healthy, skip the destructive re-init and
        // reuse the existing session.
        if (isAlreadyInitialized()) {
            LogHelper.showDebugInfo("CheckRootScreen: process already initialized, reusing existing session")
            status = RootStatus.GRANTED
            return@LaunchedEffect
        }

        // Full re-init is required. Clear any stale ServerActions so the GRANTED
        // block recreates it against the freshly started server instead of
        // reusing a reference to a torn-down session.
        GlobalVars.serverActions?.let { stale ->
            LogHelper.showDebugInfo("CheckRootScreen: clearing stale ServerActions before re-init")
            stale.close()
            GlobalVars.serverActions = null
        }

        // Check for a new app version early in startup (non-blocking, fails silently).
        // Only runs on a fresh startup, not when reusing an existing session.
        try {
            AppUpdateChecker.checkForUpdate()
        } catch (e: Exception) {
            LogHelper.showDebugInfo("CheckRootScreen: App update check failed: ${e.message}")
        }

        withContext(Dispatchers.IO) {
            CheckIntergrity.checkAppIntegrity(context)
        }
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
        // App-update states take priority over the normal checking flow
        when (updateState) {
            is AppUpdateState.Downloading -> {
                val progress = (updateState as AppUpdateState.Downloading).progress
                UpdateProgressMessage(
                    message = "检测到新版本，正在下载更新... $progress%",
                    englishMessage = "New version detected, downloading update... $progress%",
                    progress = { progress / 100f }
                )
            }

            AppUpdateState.Installing -> {
                FullScreenMessage(
                    message = "下载完成，正在安装更新...",
                    englishMessage = "Download complete, installing update..."
                )
            }

            is AppUpdateState.Error -> {
                val errMsg = (updateState as AppUpdateState.Error).message
                FullScreenMessage(
                    message = "更新安装失败：$errMsg\n请手动重新下载App。",
                    englishMessage = "Update install failed: $errMsg\nPlease re-download the App manually."
                )
            }

            AppUpdateState.Idle -> when (status) {
                RootStatus.CHECKING -> {
                    FullScreenMessage(
                        message = "正在检测Root环境...",
                        englishMessage = "Checking Root environment..."
                    )
                }

            RootStatus.ROOT_DENIED -> {
                FullScreenMessage(
                    message = "请授予 Root 权限后，再启动本软件（本软件名：紫孔雀）\n若授权失败，请尝试重启软件/设备。",
                    englishMessage = "Please grant Root permission before starting this app (App name: 紫孔雀).\nIf authorization fails, try restarting the app/device."
                )
            }

            RootStatus.PERMISSION_DENIED -> {
                FullScreenMessage(
                    message = "Root 授权成功，但仍需手动开启悬浮窗和通知权限。开启后请重启软件。",
                    englishMessage = "Root authorized, but you still need to manually enable floating window and notification permissions.\nPlease restart the app after enabling."
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
                        Text("去设置中心开启 / Open Settings", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            RootStatus.WAITING_FOR_SERVER -> {
                FullScreenMessage(
                    message = "等待Root服务器启动...",
                    englishMessage = "Waiting for Root server to start..."
                )
            }

            RootStatus.SERVER_ERROR -> {
                FullScreenMessage(
                    message = "连接到Root服务器失败。",
                    englishMessage = "Failed to connect to Root server."
                )
            }

            RootStatus.GRANTED -> {
                // Initialize ServerActions and Configs once Root is GRANTED
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        // Get and store default IME.
                        RunShell.runNoOutput("appops set com.coc.zkqcode PROJECT_MEDIA allow", checkIsPlaying = false)
                        RunShell.runNoOutput("cmd appops set com.coc.zkqcode PROJECT_MEDIA allow", checkIsPlaying = false)
                        val savedIme = AppExitHelper.getSavedInputMethod(context)
                        if (savedIme.isNullOrBlank()) {
                            val result = RunShell.run("settings get secure default_input_method", checkIsPlaying = false)
                            if (result.isNotEmpty() && result[0].isNotBlank()) {
                                val currentIme = result[0].trim()
                                val ourIme = "com.coc.zkqcode/.core.system.inputmethod.ZKQInputMethodService"
                                if (currentIme != ourIme) {
                                    AppExitHelper.saveDefaultInputMethod(context, currentIme)
                                }
                            }
                        }
                    }

                    if (GlobalVars.serverActions == null) {
                        // Pass the local WS server instance established by ServerHelper (maybe null for shell fallback)
                        GlobalVars.serverActions = ServerActions(ServerHelper.localServer)
                    }

                    // Start the control window before showing the main UI window.
                    context.startService(Intent(context, ControlWindowService::class.java))
                    val serviceIntent = Intent(context, UIWindowService::class.java)
                    context.startService(serviceIntent)
                    (context as? MainActivity)?.requestMediaProjection()
                }

                FullScreenMessage(
                    message = "权限检查通过，配置加载完成。\n\n若未能自动显示主界面，请手动点击按钮。",
                    englishMessage = "Permission check passed, configuration loaded.\n\nIf the main interface doesn't appear automatically, click the button to start the UI manually."
                ) {
                    LocalCustomButton(
                        text = "显示主界面 / Show Main Page",
                        onClick = {
                            AppStateManager.setMode(AppMode.Main)
                            val serviceIntent = Intent(context, UIWindowService::class.java)
                            GlobalVars.isAutoRunEnabled = true
                            GlobalVars.updateWindowPosition = false
                            context.startService(Intent(context, ControlWindowService::class.java))
                            context.startService(serviceIntent)
                        }
                    )
                }
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
    englishMessage: String? = null,
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

            if (englishMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = englishMessage,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = ColorTextPrimary.copy(alpha = 0.7f)
                )
            }

            if (action != null) {
                Spacer(modifier = Modifier.height(20.dp))
                action()
            }
        }
    }
}

/**
 * A status card that displays the app-update download progress with a
 * [LinearProgressIndicator] beneath the message.
 *
 * @param message   The status message (includes percentage).
 * @param englishMessage Optional English subtitle.
 * @param progress  Lambda returning the current progress as a 0f–1f fraction.
 */
@Composable
fun UpdateProgressMessage(
    message: String,
    englishMessage: String? = null,
    progress: () -> Float
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

            if (englishMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = englishMessage,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = ColorTextPrimary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorPrimary,
                trackColor = ColorPrimary.copy(alpha = 0.2f)
            )
        }
    }
}
