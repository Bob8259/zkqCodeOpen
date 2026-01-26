package com.coc.zkqcode.core.util.basic

import android.content.Context
import com.coc.zkqcode.core.ui.floatingwindows.MessageBoxHelper.showFloatingMessage
import java.lang.ref.WeakReference

object ShowMessage {
    private var contextRef: WeakReference<Context>? = null

    fun init(context: Context) {
        this.contextRef = WeakReference(context.applicationContext)
    }

    operator fun invoke(text: String) {
        contextRef?.get()?.let { context ->
            showFloatingMessage(context = context, text = text)
        } ?: run {
            System.err.println("ShowMessage: Context not initialized or released!")
        }
    }

}