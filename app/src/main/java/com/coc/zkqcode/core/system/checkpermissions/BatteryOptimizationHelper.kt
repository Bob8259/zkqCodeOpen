package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.coc.zkqcode.core.util.basic.RunShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BatteryOptimizationHelper {
    /**
     * Check whether my app is in the battery optimization whitelist.
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Use root access to add to whitelist. If still false, then start an intent ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.
     */
    suspend fun requestIgnoreBatteryOptimizations(context: Context) {
        val packageName = context.packageName
        if (!isIgnoringBatteryOptimizations(context)) {
            // Try with root first.
            RunShell.runNoOutput("dumpsys deviceidle whitelist +$packageName", checkIsPlaying = false)
            RunShell.runNoOutput("cmd deviceidle whitelist +$packageName", checkIsPlaying = false)

            // Check again. If still false, then start an intent ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.
            if (!isIgnoringBatteryOptimizations(context)) {
                withContext(Dispatchers.Main) {
                    try {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:$packageName")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
