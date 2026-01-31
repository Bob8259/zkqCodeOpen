package com.coc.zkqcode.core.util.basic

import android.content.Context
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxHelper.showFloatingMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
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
        val now = System.currentTimeMillis()
        // If message is the same and it hasn't been long since last show, ignore it to save Binder IPC
        if (text == lastMessage && (now - lastShowTime) < 500) {
            return
        }
        
        lastMessage = text
        lastShowTime = now

        contextRef?.get()?.let { context ->
            showFloatingMessage(context = context, text = text)
            Timber.tag("zkq_debug").v("Verbose: $text")
        } ?: logAndStop("ShowMessage: Context not initialized or released!")
    }

}