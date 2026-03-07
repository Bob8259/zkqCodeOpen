package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.coc.zkqcode.core.system.daemon.ServerManager
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

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
            Shell.cmd(
                "pm grant $pkg android.permission.SYSTEM_ALERT_WINDOW",
                "cmd appops set $pkg SYSTEM_ALERT_WINDOW allow",
                "pm grant $pkg android.permission.POST_NOTIFICATIONS",
                "pm grant $pkg android.permission.FOREGROUND_SERVICE",
            ).exec()
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

                var response: String? = null
                var attempts = 0
                while (response == null && attempts < 10) {
                    response = ServerHelper.waitForServerResponse()
                    if (response == null) {
                        Timber.d("Server not responding, retrying... (Attempt ${attempts + 1})")
                        ServerManager.startServer(context)
                        delay(1000)
                        attempts++
                    }
                }

                if (response == null) {
                    return@withContext RootStatus.SERVER_ERROR
                }
            }

            // Start daemon process
            //DaemonManager.setupAndRunDaemon(context)//Temporarily disabled

            return@withContext RootStatus.GRANTED
        }
}
