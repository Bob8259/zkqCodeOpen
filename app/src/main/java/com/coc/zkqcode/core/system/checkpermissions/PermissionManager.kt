package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import com.coc.zkqcode.core.system.daemon.ServerManager
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PermissionManager {
    private const val TAG = "PermissionManager"

    /**
     * 核心检测逻辑
     */
    suspend fun checkAndGrantPermissions(
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

            // 2.5 增加电池优化白名单检测
            BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context)

            // 2.6 开启无障碍服务
            AccessibilityPermissionHelper.enableAccessibilityWithRoot(
                context.packageName,
                "com.coc.zkqcode.utils.accessibility.MyAccessibilityService"
            )

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
                    response = ServerHelper.waitForServerResponse()
                    if (response == null) {
                        Log.d(TAG, "Server not responding, retrying... (Attempt ${attempts + 1})")
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
            DaemonManager.setupAndRunDaemon(context)

            return@withContext RootStatus.GRANTED
        }
}
