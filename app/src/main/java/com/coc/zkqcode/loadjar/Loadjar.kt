@file:Suppress("SameParameterValue")

package com.coc.zkqcode.loadjar

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager
import dalvik.system.DexClassLoader
import java.io.File

class Loadjar(private val context: Context) {

    private var pluginUI: MainCode? = null

    /**
     * Main composable function that handles all loading logic and displays the UI
     */
    @Composable
    fun LoadAndShowUI(loadStatus: String, onClose: () -> Unit) {
        if (loadStatus == "Plugin loaded successfully") {
            // Load the UI from jar
            pluginUI?.ShowMainUI(context, onClose)
        } else {
            Column (modifier = Modifier.background(Color.White))  {
                Text(text = loadStatus)
                Button(onClick = {
                    AppStateManager.setMode(AppMode.Run)
                    println("点击关闭悬浮窗")
                    onClose()
                }) {
                    Text(text = "关闭悬浮窗")
                }
            }
        }

    }

    /**
     * Start the plugin loading process
     */
    suspend fun startLoading(assetFileName: String = "code.jar", onStatusChange: (String) -> Unit) {
        onStatusChange("加载中...")
        // Always create a new assets folder and extract all assets
        extractAllAssets()
        
        val assetList = context.assets.list("") ?: emptyArray()
        val success = if (assetList.contains("code.jar")) {
             loadPluginFromAssets("code.jar")
        } else if (assetList.contains("encrypted_code.jar")) {
             loadEncryptedPlugin("encrypted_code.jar")
        } else {
            // Fallback to whatever was passed or fail
             loadPluginFromAssets(assetFileName)
        }
        
        val finalStatus = if (success) {
            "Plugin loaded successfully"
        } else {
            "Failed to load plugin"
        }
        onStatusChange(finalStatus)
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
            
            // If file doesn't exist in assets, this might throw
            try {
                context.assets.open(assetFileName).use { input ->
                    jarFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (_: Exception) {
                // File not found in assets
                return false
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
            val instance = pluginClass.getDeclaredConstructor().newInstance() as MainCode
            pluginUI = instance
            GlobalVars.pluginUI = instance

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun loadEncryptedPlugin(assetFileName: String): Boolean {
        return try {
            // 1. Read encrypted bytes
            val encryptedBytes = context.assets.open(assetFileName).use { it.readBytes() }
            
            // 2. Decrypt
            val decryptedBytes = com.coc.zkqcode.zkqnative.NativeTools.decryptJar(encryptedBytes)
            if (decryptedBytes.isEmpty()) return false

            val classLoader: ClassLoader = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 3. Load from memory (Android 8.0+)
                // Since the decrypted bytes are a JAR, we need to extract classes.dex first
                var dexBytes: ByteArray? = null
                java.util.zip.ZipInputStream(java.io.ByteArrayInputStream(decryptedBytes)).use { zipStream ->
                    var entry = zipStream.nextEntry
                    while (entry != null) {
                        if (entry.name == "classes.dex") {
                            dexBytes = zipStream.readBytes()
                            break
                        }
                        entry = zipStream.nextEntry
                    }
                }
                
                if (dexBytes == null) {
                    return false
                }

                val buffer = java.nio.ByteBuffer.wrap(dexBytes)
                dalvik.system.InMemoryDexClassLoader(buffer, context.classLoader)
            } else {
                // 4. Fallback for older versions: Use in-memory file descriptor (memfd/ashmem)
                android.util.Log.d("zkq_debug", "loadEncryptedPlugin: Using memfd fallback for API ${android.os.Build.VERSION.SDK_INT}")
                
                val fd = com.coc.zkqcode.zkqnative.NativeTools.createInMemoryDex(decryptedBytes)

                if (fd < 0) {
                    android.util.Log.e("zkq_debug", "loadEncryptedPlugin: Failed to create in-memory dex, fd=$fd")
                    return false
                }
                android.util.Log.d("zkq_debug", "loadEncryptedPlugin: Created in-memory dex, fd=$fd")

                // Use procfs path to the file descriptor
                val dexPath = "/proc/self/fd/$fd"
                val dexOutputDir = context.codeCacheDir
                
                android.util.Log.d("zkq_debug", "loadEncryptedPlugin: Loading from $dexPath")
                
                DexClassLoader(
                    dexPath,
                    dexOutputDir.absolutePath,
                    null,
                    context.classLoader
                )
            }

            // 5. Instantiate Plugin
            val pluginClass = classLoader.loadClass("com.coc.zkqcode.jar.ui.EnterMainCode")
            val instance = pluginClass.getDeclaredConstructor().newInstance() as MainCode
            pluginUI = instance
            GlobalVars.pluginUI = instance
            
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
