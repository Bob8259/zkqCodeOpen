package com.coc.zkqcode.core.util.basic

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.exit.AppExitHelper
import kotlinx.coroutines.delay

/** Suspends until the script is playing.
 * Exits the app if paused for more than 60 minutes.
 */
suspend fun waitForPlay() {
    if (GlobalVars.isPlaying.value) return
    ShowMessage("当前已暂停\n为节省资源占用，暂停60分钟后自动退出。")
    val startTime = System.currentTimeMillis()

    while (true) {
        delay(1000)
        // Exit the app if paused for more than 60 minutes
        if (System.currentTimeMillis() - startTime >= 60 * 60_000L) {
            val ctx = ShowMessage.getContext()
            if (ctx != null) {
                AppExitHelper.exitApplication(ctx)
            }
            return
        }
        if (!GlobalVars.isPlaying.value) {
            localSetZKQInputMethod()
            break
        }
    }
}

private suspend fun localSetZKQInputMethod() {
    val imeId = "com.coc.zkqcode/.core.system.inputmethod.ZKQInputMethodService"

    // 1. Try to enable (it's fine to execute again even if already enabled)
    RunShell.run("ime enable $imeId")

    // 2. Get current default input method for comparison
    val currentIme = RunShell.runAndGetFirst("settings get secure default_input_method")

    if (currentIme != imeId) {
        // 3. Execute setting
        RunShell.run("ime set $imeId")
        // Verify switch success
        println("Input method switched to $imeId")
    } else {
        println("Input method is already default.")
    }
}
