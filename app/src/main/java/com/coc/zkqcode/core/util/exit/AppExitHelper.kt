package com.coc.zkqcode.core.util.exit

import android.content.Context
import android.content.Intent
import com.coc.zkqcode.core.system.daemon.DaemonService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.ui.floatingwindows.ControlWindowService
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxService
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.core.util.basic.RunShell
import kotlin.system.exitProcess

object AppExitHelper {

    fun setNormalExit(context: Context, normal: Boolean) {
        try {
            val prefs = context.getSharedPreferences("exit_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("is_normal_exit", normal).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNormalExit(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences("exit_prefs", Context.MODE_PRIVATE)
            prefs.getBoolean("is_normal_exit", false)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exitApplication(context: Context) {
        setNormalExit(context, true)
        restoreDefaultInputMethod(context)
        stopDaemonProcess()
        stopAllServices(context)
        stopShellServer()
        // Kill the current process
        exitProcess(0)
    }

    fun saveDefaultInputMethod(context: Context, ime: String) {
        try {
            val prefs = context.getSharedPreferences("ime_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("default_ime", ime).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSavedInputMethod(context: Context): String? {
        return try {
            val prefs = context.getSharedPreferences("ime_prefs", Context.MODE_PRIVATE)
            val ime = prefs.getString("default_ime", null)
            if (!ime.isNullOrBlank()) ime else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreDefaultInputMethod(context: Context) {
        val defaultIme = getSavedInputMethod(context)
        if (!defaultIme.isNullOrBlank()) {
            RunShell.runBlockingNoOutput("ime set $defaultIme")
            try {
                context.getSharedPreferences("ime_prefs", Context.MODE_PRIVATE)
                    .edit().remove("default_ime").apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopShellServer() {
        try {
            // 使用 pkill -f 匹配包含指定类名的全路径进程
            // com.coc.zkqserver.ShellServer 是你在 app_process 中启动的类名
            RunShell.runBlockingNoOutput("pkill -f com.coc.zkqserver.ShellServer")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopDaemonProcess() {
        try {
            // Use libsu to execute pgrep to find the PID(s) of the daemon script
            val pids = RunShell.runBlockingOutput("pgrep -f /data/local/tmp/zkq_daemon.sh")

            if (pids.isNotEmpty()) {
                // Kill all found PIDs
                pids.forEach { pid ->
                    if (pid.isNotBlank()) {
                        RunShell.runBlockingNoOutput("kill -9 $pid")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAllServices(context: Context) {
        try {
            context.stopService(Intent(context, UIWindowService::class.java))
            context.stopService(Intent(context, ControlWindowService::class.java))
            context.stopService(Intent(context, MessageBoxService::class.java))
            context.stopService(Intent(context, DaemonService::class.java))

            // Release screen capture resources
            ScreenCaptureManager.releaseAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}