package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.delay

suspend fun setZKQInputMethod() {
    val imeId = "com.coc.zkqcode/.core.system.inputmethod.ZKQInputMethodService"

    // 1. Try the standard `ime enable` + `ime set` path first
    RunShell.run("ime enable $imeId")

    val currentIme = RunShell.runAndGetFirst("settings get secure default_input_method")

    if (currentIme != imeId) {
        RunShell.run("ime set $imeId")

        // 2. Verify `ime set` actually worked — some ROMs silently ignore it
        val verifiedIme = RunShell.runAndGetFirst("settings get secure default_input_method")
        if (verifiedIme != imeId) {
            // Fallback: `ime enable` is broken on this device (reports success but doesn't
            // add to enabled list). Write directly to settings secure to force-enable.
            val enabledImes = RunShell.runAndGetFirst("settings get secure enabled_input_methods")
            if (!enabledImes.contains(imeId)) {
                val newEnabled = if (enabledImes.isBlank()) imeId else "$enabledImes:$imeId"
                RunShell.run("settings put secure enabled_input_methods \"$newEnabled\"")
            }
            RunShell.run("settings put secure default_input_method \"$imeId\"")
            showDebugInfo("Input method force-set via settings put")
        } else {
            showDebugInfo("Input method switched to $imeId")
        }
    } else {
        showDebugInfo("Input method is already default.")
    }

    // 3. Wait for the service instance to become available.
    //    After switching, Android binds the service asynchronously — onCreate() may not
    //    have fired yet, leaving ZKQInputMethodService.instance null.
    if (ZKQInputMethodService.instance == null) {
        val maxWaitMs = 5000
        val intervalMs = 100L
        var waited = 0
        while (ZKQInputMethodService.instance == null && waited < maxWaitMs) {
            delay(intervalMs)
            waited += intervalMs.toInt()
        }
        if (ZKQInputMethodService.instance == null) {
            logAndRestart("输入法服务启动超时 (${maxWaitMs}ms)，请检查输入法权限设置", false)
        }
    }
}
