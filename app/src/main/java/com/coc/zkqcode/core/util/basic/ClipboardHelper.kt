package com.coc.zkqcode.core.util.basic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.ui.floatingwindows.ClipboardActivity
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Clipboard utility that uses the Application context directly.
 * Supported by a transient transparent activity for reading from background context on Android 10+.
 */
object ClipboardHelper {

    private var pendingResult: CompletableDeferred<String?>? = null

    /**
     * Called by ClipboardActivity to post the clipboard text result.
     */
    fun postResult(text: String?) {
        showDebugInfo("ClipboardHelper: postResult() called, text: $text, pendingResult is null? = ${pendingResult == null}")
        pendingResult?.complete(text)
    }

    /**
     * Reads the clipboard content from a background context by temporarily launching a transparent Activity.
     */
    suspend fun readClipboard(): String? {
        showDebugInfo("ClipboardHelper: readClipboard() initiated")
        val context = ZKQApplication.appContext
        val deferred = CompletableDeferred<String?>()
        pendingResult = deferred

        return try {
            val intent = Intent(context, ClipboardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            showDebugInfo("ClipboardHelper: Launching ClipboardActivity...")
            context.startActivity(intent)

            showDebugInfo("ClipboardHelper: Awaiting ClipboardActivity result (timeout 2000ms)...")
            val result = withTimeoutOrNull(2000L) {
                deferred.await()
            }
            showDebugInfo("ClipboardHelper: Await finished, result: $result")
            result
        } catch (e: Exception) {
            showDebugInfo("ClipboardHelper: Exception during readClipboard: ${e.message}")
            e.printStackTrace()
            null
        } finally {
            pendingResult = null
        }
    }

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

