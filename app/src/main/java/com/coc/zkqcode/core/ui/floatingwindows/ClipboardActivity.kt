package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Activity
import android.content.ClipboardManager
import android.os.Bundle
import com.coc.zkqcode.core.util.basic.ClipboardHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo

/**
 * Transient transparent activity to read clipboard on Android 10+ background service contexts.
 */
class ClipboardActivity : Activity() {

    private var hasRead = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDebugInfo("ClipboardActivity: onCreate() called")
    }

    override fun onResume() {
        super.onResume()
        showDebugInfo("ClipboardActivity: onResume() called")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        showDebugInfo("ClipboardActivity: onWindowFocusChanged() called, hasFocus = $hasFocus")
        if (hasFocus && !hasRead) {
            hasRead = true
            var text: String? = null
            try {
                val cm = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                showDebugInfo("ClipboardActivity: ClipboardManager retrieved (null? = ${cm == null})")
                if (cm != null) {
                    val hasClip = cm.hasPrimaryClip()
                    showDebugInfo("ClipboardActivity: hasPrimaryClip? = $hasClip")
                    if (hasClip) {
                        val clip = cm.primaryClip
                        showDebugInfo("ClipboardActivity: primaryClip retrieved (null? = ${clip == null}, items = ${clip?.itemCount ?: 0})")
                        if (clip != null && clip.itemCount > 0) {
                            val charSeq = clip.getItemAt(0).text
                            showDebugInfo("ClipboardActivity: item text retrieved (null? = ${charSeq == null})")
                            if (charSeq != null) {
                                text = charSeq.toString()
                                showDebugInfo("ClipboardActivity: successfully read clipboard text of length: ${text.length}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showDebugInfo("ClipboardActivity: Exception while reading clipboard: ${e.message}")
                e.printStackTrace()
            } finally {
                showDebugInfo("ClipboardActivity: Posting result ($text) back to ClipboardHelper and finishing activity")
                ClipboardHelper.postResult(text)
                finish()
            }
        }
    }
}
