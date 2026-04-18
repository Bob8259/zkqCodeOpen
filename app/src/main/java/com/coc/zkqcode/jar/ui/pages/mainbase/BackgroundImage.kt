package com.coc.zkqcode.jar.ui.pages.mainbase

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.core.util.fileactions.backgroundRevision
import com.coc.zkqcode.core.util.fileactions.getCustomBackgroundFile

// Local fallback revision counter in case BackgroundHelper is not available on older hosts
private val fallbackRevision = mutableIntStateOf(0)

/**
 * Safely get the background revision from core's BackgroundHelper.
 * Falls back to a local counter if the core class is not available (older host).
 */
private fun getRevisionState(): MutableIntState {
    return try {
        backgroundRevision
    } catch (e: Throwable) {
        fallbackRevision
    }
}

// Loads the background image: custom from filesDir first, then fallback to assets/background.jpg.
@Composable
fun BackgroundImage() {
    val context = LocalContext.current
    // Read the revision so recomposition is triggered when the background changes
    val revision by remember { getRevisionState() }
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
        // Use fully qualified name to avoid NoClassDefFoundError on older hosts
        val file = getCustomBackgroundFile(context)
        if (file != null) {
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        } else null
    } catch (e: Throwable) {
        // BackgroundHelper not available on older host — skip custom background
        null
    }
}

