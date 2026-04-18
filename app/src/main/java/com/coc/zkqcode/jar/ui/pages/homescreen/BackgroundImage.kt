package com.coc.zkqcode.jar.ui.pages.homescreen

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val CUSTOM_BG_FILENAME = "custom_background.jpg"

// Revision counter to trigger recomposition when the custom background changes
private val backgroundRevision = mutableIntStateOf(0)

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

// Loads the background image: custom from filesDir first, then fallback to assets/background.jpg.
@Composable
fun BackgroundImage() {
    val context = LocalContext.current
    // Read the revision so recomposition is triggered when the background changes
    val revision by remember { backgroundRevision }
    val bitmap: ImageBitmap? = remember(revision) {
        loadCustomBackground(context)
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun loadCustomBackground(context: Context): ImageBitmap? {
    return try {
        val file = File(context.filesDir, CUSTOM_BG_FILENAME)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        } else null
    } catch (e: Exception) {
        null
    }
}

