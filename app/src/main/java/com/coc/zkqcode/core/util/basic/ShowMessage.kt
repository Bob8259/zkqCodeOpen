package com.coc.zkqcode.core.util.basic

import android.content.Context
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxHelper.showFloatingMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import timber.log.Timber
import java.lang.ref.WeakReference

object ShowMessage {
    private var contextRef: WeakReference<Context>? = null
    private var lastMessage: String = ""
    private var lastShowTime: Long = 0L

    fun init(context: Context) {
        this.contextRef = WeakReference(context.applicationContext)
    }

    operator fun invoke(text: String, isChecking: Boolean = true) {
        if (isChecking && !GlobalVars.isPlaying.value && !GlobalVars.isSwitchingAccount) {
            return//the user paused the script, then we should also stop
        }
        val now = System.currentTimeMillis()
        val elapsed = now - lastShowTime
        val shouldShow = elapsed >= 100 && !(text == lastMessage && elapsed < 500)

        lastMessage = text
        lastShowTime = now
        contextRef?.get()?.let { context ->
            // Throttle and dedup only gate the floating message; Timber always logs
            if (shouldShow) {
                showFloatingMessage(context = context, text = text)
            }
            Timber.tag("zkq_debug").v("Verbose: $text")
        } ?: logAndRestart("ShowMessage: Context not initialized or released!")
    }

}