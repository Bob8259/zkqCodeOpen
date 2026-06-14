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
        
        val layout = android.widget.FrameLayout(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.parseColor("#80000000")) // 50% opacity black
        }
        
        val message = "正在读取剪贴板，请稍候...\nReading Clipboard, Please wait..."
        
        val textView = android.widget.TextView(this).apply {
            text = message
            gravity = android.view.Gravity.CENTER
            setTextColor(android.graphics.Color.WHITE)
            textSize = 18f
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
            )
        }
        
        layout.addView(textView)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasRead) {
            hasRead = true
            var text: String? = null
            try {
                val cm = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                if (cm != null && cm.hasPrimaryClip()) {
                    val clip = cm.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val charSeq = clip.getItemAt(0).text
                        if (charSeq != null) {
                            text = charSeq.toString()
                        }
                    }
                }
            } catch (e: Exception) {
                showDebugInfo("ClipboardActivity: Exception while reading clipboard: ${e.message}")
                e.printStackTrace()
            } finally {
                ClipboardHelper.postResult(text)
                finish()
            }
        }
    }
}
