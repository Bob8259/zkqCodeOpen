package com.coc.zkqcode.utils.exit

import android.content.Context
import android.content.Intent
import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.utils.daemon.DaemonService
import com.coc.zkqcode.core.ui.floatingwindows.ControlWindowService
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxService
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.core.system.accessibility.MyAccessibilityService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.topjohnwu.superuser.Shell
import kotlin.system.exitProcess

object AppExitHelper {

    fun exitApplication(context: Context) {
        restoreDefaultInputMethod()
        stopDaemonProcess()
        stopAllServices(context)
        // Kill the current process
        exitProcess(0)
    }

    fun restoreDefaultInputMethod() {
        val defaultIme = GlobalVars.defaultInputMethod
        if (!defaultIme.isNullOrBlank()) {
            Shell.cmd("ime set $defaultIme").exec()
        }
    }

    private fun stopDaemonProcess() {
        try {
            // Use libsu to execute pgrep to find the PID(s) of the daemon script
            val pgrepResult = Shell.cmd("pgrep -f /data/local/tmp/zkq_daemon.sh").exec()

            // Get all output lines (PIDs)
            val pids = pgrepResult.out

            if (pids.isNotEmpty()) {
                // Kill all found PIDs
                pids.forEach { pid ->
                    if (pid.isNotBlank()) {
                        Shell.cmd("kill -9 $pid").exec()
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

            // Stop accessibility service
            MyAccessibilityService.disableService()

            // Release screen capture resources
            ScreenCaptureManager.releaseAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}