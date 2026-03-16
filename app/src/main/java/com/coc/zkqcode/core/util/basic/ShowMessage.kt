package com.coc.zkqcode.core.util.basic

import android.content.Context
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxHelper.showFloatingMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.InGamesVars
import timber.log.Timber
import java.lang.ref.WeakReference

object ShowMessage {
    private var contextRef: WeakReference<Context>? = null
    private var lastMessage: String = ""
    private var lastShowTime: Long = 0L

    fun init(context: Context) {
        this.contextRef = WeakReference(context.applicationContext)
    }

    operator fun invoke(text: String) {
        if (!GlobalVars.isPlaying.value && !GlobalVars.isSwitchingAccount) {
            return//the user paused the script, then we should also stop
        }
        val now = System.currentTimeMillis()
        // If message is the same, and it hasn't been long since last show, ignore it to save Binder IPC
        if (text == lastMessage && (now - lastShowTime) < 500) {
            return
        }
        // Prepend account number prefix if available
        val displayText = "账号${InGamesVars.currentAccountNumber}" + text
        lastMessage = displayText
        lastShowTime = now

        contextRef?.get()?.let { context ->
            showFloatingMessage(context = context, text = displayText)
            Timber.tag("zkq_debug").v("Verbose: $displayText")
        } ?: logAndRestart("ShowMessage: Context not initialized or released!")
    }

}