package com.coc.zkqcode.loadui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coc.zkqcode.jar.ui.MainUI
import dalvik.system.DexClassLoader
import java.io.File

class LoadUI(private val context: Context) {

    private var pluginUI: MainUI? = null

    // 1. 定义监听器，当插件修改数据时，这里会立即触发 println
    private val sharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "text") {
                val newValue = prefs.getString(key, "")
                println("【宿主收到消息】text 字段已更改为: $newValue")
            }
        }

    init {
        // 2. 注册监听
        val sp = context.getSharedPreferences("plugin_prefs", Context.MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    /**
     * Main composable function that handles all loading logic and displays the UI
     */
    @Composable
    fun LoadAndShowUI(assetFileName: String = "ui.jar", onClose: () -> Unit) {
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
            Text(text = loadStatus)
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
            val pluginClass = classLoader.loadClass("com.coc.zkqcode.ui.EnterUI")
            pluginUI = pluginClass.getDeclaredConstructor().newInstance() as MainUI

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

        // 1. 只有当文件夹不存在时创建，或者为了更新而清理
        // 注意：如果 jar 在 assets 里，不要在这个方法里删掉它，除非你已经加载完了
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        try {
            val assetList = context.assets.list("") ?: emptyArray()
            for (assetName in assetList) {
                // 过滤掉系统自带的文件夹和已知的非资源文件
                if (assetName == "images" || assetName == "webkit" || assetName == "sounds") continue

                // 重点：尝试判断是否是文件。assets.open 对文件夹会报错。
                try {
                    val outputFile = File(assetsDir, assetName)

                    // 只有文件才进行复制
                    context.assets.open(assetName).use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    // println("成功释放文件: $assetName")
                } catch (e: Exception) {
                    // 如果 open 报错，说明这是一个文件夹，我们跳过它
                    println("跳过目录或无法读取的文件: $assetName，错误$e")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return assetsDir
    }
}
