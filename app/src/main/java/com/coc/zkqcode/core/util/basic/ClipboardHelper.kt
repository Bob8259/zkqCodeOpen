package com.coc.zkqcode.core.util.basic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.coc.zkqcode.core.app.ZKQApplication

/**
 * Clipboard utility that uses the Application context directly.
 * Note: readClipboard() is NOT possible from a background context on Android 10+.
 * Only the default IME or the foreground app can read clipboard.
 * Use ZKQInputMethodService.instance.readClipboard() for reading.
 */
object ClipboardHelper {

    /**
     * Sets the clipboard content to the given text.
     * Used as part of the clipboard-paste fallback for text input.
     */
    fun setClipboard(text: String) {
        try {
            val clipboard = ZKQApplication.appContext
                .getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("input", text))
        } catch (_: Exception) {
            // Silently fail — caller will handle via shell fallback
        }
    }
}
