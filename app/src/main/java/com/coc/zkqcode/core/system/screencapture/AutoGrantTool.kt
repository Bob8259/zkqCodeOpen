package com.coc.zkqcode.core.system.screencapture

import com.coc.zkqcode.core.system.accessibility.MyAccessibilityService
import com.topjohnwu.superuser.Shell
import timber.log.Timber

object AutoGrantTool {
    private val SERVICE_PATH: String
        get() = "com.coc.zkqcode/${MyAccessibilityService::class.java.name}"

    /**
     * Use Root permissions to enable accessibility service
     * Recommended to call before requesting screen capture permission
     */
    fun forceEnableAccessibility(): Boolean {
        try {
            val getCmd = "settings get secure enabled_accessibility_services"
            val currentServices = Shell.cmd(getCmd).exec().out.joinToString("")
            if (!currentServices.contains(SERVICE_PATH)) {
                val newList = if (currentServices.isEmpty() || currentServices == "null") {
                    SERVICE_PATH
                } else {
                    "$currentServices:$SERVICE_PATH"
                }
                Shell.cmd("settings put secure enabled_accessibility_services $newList").exec()
            }
            // Toggle master switch once to ensure system rescans and binds service
            Shell.cmd("settings put secure accessibility_enabled 0").exec()
            Thread.sleep(100)
            Shell.cmd("settings put secure accessibility_enabled 1").exec()
            return true
        } catch (_: Exception) {
            return false
        }
    }
}
