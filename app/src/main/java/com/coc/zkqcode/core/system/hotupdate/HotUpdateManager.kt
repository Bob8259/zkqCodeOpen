package com.coc.zkqcode.core.system.hotupdate

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.loadjar.Loadjar
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager

object HotUpdateManager {

    /**
     * Collects from the shared update signal flow. Each emission triggers
     * an update check. This suspends indefinitely and should be launched
     * in a long-lived coroutine scope (e.g. the service scope).
     */
    suspend fun listenForSignal(context: Context) {
        GlobalVars.updateCheckSignal.collect {
            LogHelper.showDebugInfo("HotUpdateManager: Received update check signal")
            checkAndUpdate(context)
        }
    }

    private fun checkAndUpdate(context: Context) {
        // TODO: HTTP GET to server with current JAR MD5 to check for update
        // TODO: If update available, download new encrypted JAR to context.filesDir/assets/
        // TODO: Verify downloaded file MD5

        val hasUpdate = false // Placeholder -- assume no update for now
        if (hasUpdate) {
            LogHelper.showDebugInfo("HotUpdateManager: New JAR available, reloading")
            // Reuse DebugReloadReceiver pattern: stop bot, reload, restart
            AppStateManager.setMode(AppMode.Main)
            val loader = Loadjar(context)
            loader.startLoading { status ->
                LogHelper.showDebugInfo("HotUpdateManager: $status")
                if (status == "Plugin loaded successfully") {
                    GlobalVars.isPlaying.value = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        AppStateManager.setMode(AppMode.Run)
                        LogHelper.showDebugInfo("HotUpdateManager: Bot restarted after hot update")
                    }, 500)
                }
            }
        } else {
            LogHelper.showDebugInfo("HotUpdateManager: No update available")
        }
    }
}
