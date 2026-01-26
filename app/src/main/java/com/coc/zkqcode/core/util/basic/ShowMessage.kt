package com.coc.zkqcode.core.util.basic

import android.content.Context
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxHelper.showFloatingMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndDie
import timber.log.Timber
import java.lang.ref.WeakReference

object ShowMessage {
    private var contextRef: WeakReference<Context>? = null

    fun init(context: Context) {
        this.contextRef = WeakReference(context.applicationContext)
    }

    operator fun invoke(text: String) {
        contextRef?.get()?.let { context ->
            showFloatingMessage(context = context, text = text)
            Timber.v("Verbose: $text")
        } ?: logAndDie("ShowMessage: Context not initialized or released!")
    }

}