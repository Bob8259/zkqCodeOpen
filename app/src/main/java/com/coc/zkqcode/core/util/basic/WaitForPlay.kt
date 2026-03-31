package com.coc.zkqcode.core.util.basic

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.exit.AppExitHelper
import kotlinx.coroutines.delay

/** Suspends until the script is playing.
 * Exits the app if paused for more than 5 minutes.
 */
suspend fun waitForPlay() {
    if (GlobalVars.isPlaying.value) return
    ShowMessage("当前已暂停\n为节省资源占用，暂停5分钟后会自动退出。")
    val startTime = System.currentTimeMillis()

    while (!GlobalVars.isPlaying.value) {
        delay(1000)

        // Exit the app if paused for more than 5 minutes
        if (System.currentTimeMillis() - startTime >= 5 * 60_000L) {
            val ctx = ShowMessage.getContext()
            if (ctx != null) {
                AppExitHelper.exitApplication(ctx)
            }
            return
        }
    }
}
