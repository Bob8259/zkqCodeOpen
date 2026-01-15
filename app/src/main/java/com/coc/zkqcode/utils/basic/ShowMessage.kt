package com.coc.zkqcode.utils.basic

import com.coc.zkqcode.utils.floatingwindows.MessageBoxHelper.showFloatingMessage

import java.lang.ref.WeakReference

object ShowMessage {
    private var contextRef: WeakReference<android.content.Context>? = null

    fun init(context: android.content.Context) {
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