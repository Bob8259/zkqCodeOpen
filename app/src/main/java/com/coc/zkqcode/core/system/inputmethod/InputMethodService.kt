package com.coc.zkqcode.core.system.inputmethod

import android.content.ClipboardManager
import android.inputmethodservice.InputMethodService
import com.coc.zkqcode.core.util.basic.ClipboardHelper
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.delay

/**
 * Custom Input Method Service that provides a function to read the clipboard.
 */
class ZKQInputMethodService : InputMethodService() {

    companion object {
        var instance: ZKQInputMethodService? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    /**
     * Reads the current text from the primary clipboard.
     * @return The text content of the primary clip, or null if empty or not text.
     */
    fun readClipboard(): String? {
        return try {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
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
        } catch (_: Exception) {
            null
        }
    }

    fun inputText(text: String) {
        // 1. 获取当前正在输入的连接
        val ic = currentInputConnection ?: logAndRestart("Can not get input connection")

        // 3. 将文字发送到目标文本框
        // 第二个参数 1 表示将光标移动到输入文字之后的第1个位置
        ic.commitText(text, 1)
    }
}

/**
 * Reliable text input that works even when ZKQInputMethodService is not bound.
 * Strategy:
 *   1. Try IME commitText (fastest, handles all characters)
 *   2. Fallback: set clipboard + simulate paste via shell (handles CJK)
 */
suspend fun inputTextReliable(text: String) {
    val imeInstance = ZKQInputMethodService.instance
    if (imeInstance != null) {
        try {
            imeInstance.inputText(text)
            return
        } catch (_: Exception) {
            // IME instance exists but inputConnection failed — fall through to fallback
        }
    }

    // Fallback: clipboard paste approach (works with CJK characters)
    showDebugInfo("IME instance unavailable, using clipboard paste fallback for: $text")
    ClipboardHelper.setClipboard(text)
    delay(100) // Allow clipboard to settle
    // Simulate Ctrl+V paste via key events
    RunShell.run("input keyevent 279", checkIsPlaying = false) // KEYCODE_PASTE
}
