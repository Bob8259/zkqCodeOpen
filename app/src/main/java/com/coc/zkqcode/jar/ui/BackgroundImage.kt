package com.coc.zkqcode.jar.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.core.util.fileactions.backgroundRevision
import com.coc.zkqcode.core.util.fileactions.getCustomBackgroundFile
import java.io.IOException

// Loads the background image: custom from filesDir first, then fallback to assets/background.jpg.
@Composable
fun BackgroundImage() {
    val context = LocalContext.current
    // Read the revision so recomposition is triggered when the background changes
    val revision by remember { backgroundRevision }
    val bitmap: ImageBitmap? = remember(revision) {
        loadCustomBackground(context) ?: loadAssetBackground(context)
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

private fun loadCustomBackground(context: android.content.Context): ImageBitmap? {
    return try {
        val file = getCustomBackgroundFile(context)
        if (file != null) {
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        } else null
    } catch (e: Exception) {
        null
    }
}

private fun loadAssetBackground(context: android.content.Context): ImageBitmap? {
    return try {
        context.assets.open("background.jpg").use { inputStream ->
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        }
    } catch (e: IOException) {
        null
    }
}
