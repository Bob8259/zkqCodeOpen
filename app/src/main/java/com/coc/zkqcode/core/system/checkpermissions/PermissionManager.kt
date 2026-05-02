package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.system.daemon.ServerManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PermissionManager {
    /**
     * Core permission check and grant logic
     */
    suspend fun checkAndGrantPermissions(
        context: Context,
        onStatusChange: (RootStatus) -> Unit
    ): RootStatus =
        withContext(Dispatchers.IO) {
            val shell = Shell.getShell()
            // 1. Check Root permission first
            if (!shell.isRoot) {
                return@withContext RootStatus.ROOT_DENIED
            }
            // 2. If Root is available, try to grant permissions silently
            val pkg = context.packageName
            RunShell.runNoOutput(
                "pm grant $pkg android.permission.SYSTEM_ALERT_WINDOW && " +
                "cmd appops set $pkg SYSTEM_ALERT_WINDOW allow && " +
                "pm grant $pkg android.permission.POST_NOTIFICATIONS && " +
                "pm grant $pkg android.permission.FOREGROUND_SERVICE",
                checkIsPlaying = false
            )
            // 2.5 Add battery optimization whitelist check
            BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context)

            // 2.6 Enable accessibility service
            AccessibilityPermissionHelper.enableAccessibilityWithRoot(
                context.packageName,
                "com.coc.zkqcode.core.system.accessibility.MyAccessibilityService"
            )

            // 3. Check if permissions are actually granted (pm grant may not work for overlay on some systems)
            val hasOverlay = Settings.canDrawOverlays(context)
            val hasNotification = NotificationManagerCompat.from(context).areNotificationsEnabled()
            if (hasOverlay && hasNotification) {
                val serverStarted = ServerManager.startServer(context)
                if (!serverStarted) {
                    return@withContext RootStatus.SERVER_ERROR
                }

                // Wait for server response
                withContext(Dispatchers.Main) {
                    onStatusChange(RootStatus.WAITING_FOR_SERVER)
                }

                GlobalVars.serverConnectionMethod = null
                var responseMethod: ServerConnectionMethod? = null
                var attempts = 0
                while (responseMethod == null && attempts < 10) {
                    responseMethod = ServerHelper.waitForServerResponse()
                    LogHelper.showDebugInfo("Connection method: $responseMethod")
                    if (responseMethod == null) {
                        LogHelper.showDebugInfo("Server not responding, retrying... (Attempt ${attempts + 1})")
                        ServerManager.startServer(context)
                        delay(1000)
                        attempts++
                    }
                }

                if (responseMethod == null) {
                    return@withContext RootStatus.SERVER_ERROR
                }
            }

            // Start daemon process
            //DaemonManager.setupAndRunDaemon(context)//Temporarily disabled

            return@withContext RootStatus.GRANTED
        }
}
