package com.coc.zkqcode.utils.inputmethod

import android.content.ClipboardManager
import android.content.Context

/**
 * Custom Input Method Service that provides a function to read the clipboard.
 */
class InputMethodService : android.inputmethodservice.InputMethodService() {
    
    override fun onCreate() {
        super.onCreate()
    }

    /**
     * Reads the current text from the primary clipboard.
     * @return The text content of the primary clip, or null if empty or not text.
     */
    fun readClipboard(): String? {
        return try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard == null || !clipboard.hasPrimaryClip()) {
                null
            } else {
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).text?.toString()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
