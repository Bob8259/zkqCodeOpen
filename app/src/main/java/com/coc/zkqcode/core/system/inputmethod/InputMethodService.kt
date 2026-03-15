package com.coc.zkqcode.core.system.inputmethod

import android.content.ClipboardManager
import android.inputmethodservice.InputMethodService
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart

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

    fun commitGameName(text: String) {
        // 1. 获取当前正在输入的连接
        val ic = currentInputConnection ?: logAndRestart("Can not get input connection")

        // 3. 将文字发送到目标文本框
        // 第二个参数 1 表示将光标移动到输入文字之后的第1个位置
        ic.commitText(text, 1)
    }
}
