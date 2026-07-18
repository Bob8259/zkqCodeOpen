package com.coc.zkqcode.core.system.inputmethod

import com.coc.zkqcode.core.util.basic.ClipboardHelper
import com.coc.zkqcode.core.util.basic.RunShell
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reliable text input using the clipboard paste approach.
 */
suspend fun inputTextReliable(text: String) {
    ClipboardHelper.setClipboard(text)
    delay(100.milliseconds) // Allow clipboard to settle
    // Simulate Ctrl+V paste via key events
    RunShell.run("input keyevent 279", checkIsPlaying = false) // KEYCODE_PASTE
}
