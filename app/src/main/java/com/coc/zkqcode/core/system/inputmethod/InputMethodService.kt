package com.coc.zkqcode.core.system.inputmethod

import com.coc.zkqcode.core.util.basic.ClipboardHelper
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.delay

/**
 * Reliable text input using the clipboard paste approach.
 */
suspend fun inputTextReliable(text: String) {
    showDebugInfo("Using clipboard paste input for: $text")
    ClipboardHelper.setClipboard(text)
    delay(100) // Allow clipboard to settle
    // Simulate Ctrl+V paste via key events
    RunShell.run("input keyevent 279", checkIsPlaying = false) // KEYCODE_PASTE
}
