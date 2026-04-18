package com.coc.zkqcode.core.util.fileactions

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import java.io.File

private const val CUSTOM_BG_FILENAME = "custom_background.jpg"

// Revision counter to trigger recomposition when the custom background changes
val backgroundRevision = mutableIntStateOf(0)

/** Call this after saving a new custom background to force BackgroundImage to reload. */
fun notifyBackgroundChanged() {
    backgroundRevision.intValue++
}

/** Copy the user-selected image URI into the app's private storage. */
fun saveCustomBackground(context: Context, uri: Uri): Boolean {
    return try {
        val dest = File(context.filesDir, CUSTOM_BG_FILENAME)
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        notifyBackgroundChanged()
        true
    } catch (e: Exception) {
        false
    }
}

/** Delete the custom background, reverting to the default asset image. */
fun resetCustomBackground(context: Context): Boolean {
    return try {
        val file = File(context.filesDir, CUSTOM_BG_FILENAME)
        if (file.exists()) file.delete()
        notifyBackgroundChanged()
        true
    } catch (e: Exception) {
        false
    }
}

/** Get the custom background file, or null if it doesn't exist. */
fun getCustomBackgroundFile(context: Context): File? {
    val file = File(context.filesDir, CUSTOM_BG_FILENAME)
    return if (file.exists()) file else null
}
