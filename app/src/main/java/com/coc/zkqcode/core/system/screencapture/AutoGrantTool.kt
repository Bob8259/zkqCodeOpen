package com.coc.zkqcode.core.system.screencapture

import com.coc.zkqcode.core.system.accessibility.MyAccessibilityService
import com.coc.zkqcode.core.util.basic.RunShell
import timber.log.Timber

object AutoGrantTool {
    private val SERVICE_PATH: String
        get() = "com.coc.zkqcode/${MyAccessibilityService::class.java.name}"

    /**
     * Use Root permissions to enable accessibility service via a double-toggle cycle.
     * Must run on the main thread — returns immediately so onServiceConnected()
     * can be dispatched via the main Looper without being blocked.
     */
    fun forceEnableAccessibility(): Boolean {
        try {
            val getCmd = "settings get secure enabled_accessibility_services"
            val currentServices = RunShell.runBlockingOutput(getCmd, checkIsPlaying = false).joinToString("")

            // Strip ALL entries belonging to our package (including stale/corrupt variants)
            val strippedList = currentServices
                .split(":")
                .filter { !it.startsWith("com.coc.zkqcode/") && it.isNotEmpty() && it != "null" }
                .joinToString(":")

            // === Cycle 1: process removal so the system fully forgets our service ===
            RunShell.runBlockingNoOutput("settings put secure enabled_accessibility_services $strippedList", checkIsPlaying = false)
            RunShell.runBlockingNoOutput("settings put secure accessibility_enabled 0", checkIsPlaying = false)
            Thread.sleep(30)
            RunShell.runBlockingNoOutput("settings put secure accessibility_enabled 1", checkIsPlaying = false)
            Thread.sleep(50)

            // === Cycle 2: add our service as a brand-new entry and trigger bind ===
            RunShell.runBlockingNoOutput("settings put secure accessibility_enabled 0", checkIsPlaying = false)
            Thread.sleep(30)
            val newList = if (strippedList.isEmpty()) SERVICE_PATH else "$strippedList:$SERVICE_PATH"
            RunShell.runBlockingNoOutput("settings put secure enabled_accessibility_services $newList", checkIsPlaying = false)
            RunShell.runBlockingNoOutput("settings put secure accessibility_enabled 1", checkIsPlaying = false)

            return true
        } catch (e: Exception) {
            Timber.e(e, "forceEnableAccessibility failed")
            return false
        }
    }
}
