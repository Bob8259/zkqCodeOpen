package com.coc.zkqcode.loadjar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coc.zkqcode.interfaces.MainCode
import dalvik.system.DexClassLoader
import java.io.File

class Loadjar(private val context: Context) {

    private var pluginUI: MainCode? = null

    /**
     * Main composable function that handles all loading logic and displays the UI
     */
    @Composable
    fun LoadAndShowUI(assetFileName: String = "code.jar", onClose: () -> Unit) {
        var loadStatus by remember { mutableStateOf("Loading...") }
        LaunchedEffect(Unit) {
            // Always create a new assets folder and extract all assets
            extractAllAssets()
            // Load plugin from assets directly (no extraction during loading)
            val success = loadPluginFromAssets(assetFileName)
            loadStatus = if (success) {
                "Plugin loaded successfully"
            } else {
                "Failed to load plugin"
            }
        }

        if (loadStatus == "Plugin loaded successfully") {
            // Load the UI from jar
            pluginUI?.ShowMainUI(context, onClose)
        } else {
            Column {
                Text(text = loadStatus)
                Button(onClick = onClose) {
                    Text(text = "关闭悬浮窗")
                }
            }
        }

    }

    /**
     * Extract jar/dex from assets to private directory and load the plugin UI
     */
    private fun loadPluginFromAssets(assetFileName: String): Boolean {

        return try {
            // Get private directory for extracted files
            val privateDir = context.filesDir
            val dexOutputDir = context.codeCacheDir

            // Extract jar from assets to private directory
            val jarFile = File(privateDir, assetFileName)
            context.assets.open(assetFileName).use { input ->
                jarFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Load the jar using DexClassLoader
            val classLoader = DexClassLoader(
                jarFile.absolutePath,
                dexOutputDir.absolutePath,
                null,
                context.classLoader
            )

            // Load the plugin implementation class
            val pluginClass = classLoader.loadClass("com.coc.zkqcode.jar.ui.EnterMainCode")
            pluginUI = pluginClass.getDeclaredConstructor().newInstance() as MainCode

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Always create a new assets folder and extract all assets into it
     */
    private fun extractAllAssets(): File {
        val privateDir = context.filesDir
        val assetsDir = File(privateDir, "assets")

        // 1. Create only if the folder does not exist, or clean up for updates
        // Note: If the jar is in assets, do not delete it in this method unless you have finished loading it
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        try {
            val assetList = context.assets.list("") ?: emptyArray()
            for (assetName in assetList) {
                // Filter out system folders, known non-resource files, and all images
                if (assetName == "images" || assetName == "webkit" || assetName == "sounds" ||
                    assetName.endsWith(".png", true) || assetName.endsWith(".jpg", true) ||
                    assetName.endsWith(".jpeg", true) || assetName.endsWith(".webp", true)
                ) continue

                // Key point: Try to determine if it is a file. assets.open will report an error for folders.
                try {
                    val outputFile = File(assetsDir, assetName)

                    // Copy only if it is a file
                    context.assets.open(assetName).use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    // If open fails, it means this is a folder, so we skip it
                    println("Skip directory or unreadable file: $assetName, error: $e")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return assetsDir
    }
}
