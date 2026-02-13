package com.coc.zkqcode.core.system.daemon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.loadjar.Loadjar
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import timber.log.Timber

class DebugReloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "com.coc.zkqcode.DEBUG_RELOAD") return

        Timber.d("DebugReload: Received reload broadcast")

        // 1. Stop current bot by setting mode to Main
        AppStateManager.setMode(AppMode.Main)

        // 2. Reload JAR
        val loader = Loadjar(context)
        loader.startLoading { status ->
            Timber.d("DebugReload: $status")
            if (status == "Plugin loaded successfully") {
                // 3. Re-run bot after reload
                GlobalVars.isPlaying.value = true
                Handler(Looper.getMainLooper()).postDelayed({
                    AppStateManager.setMode(AppMode.Run)
                    Timber.d("DebugReload: Bot restarted")
                }, 500)
            }
        }
    }
}
