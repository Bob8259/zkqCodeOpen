package com.coc.zkqcode.jar.ui.pages.homescreen

import android.content.Context
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

@Composable
fun AnnouncementDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var localVersion by remember { mutableStateOf<Long?>(null) }
    var serverVersion by remember { mutableStateOf<Long?>(null) }
    var updateLog by remember { mutableStateOf("正在获取更新记录...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadVersionInfo(context: Context) {
        try {
            withContext(Dispatchers.IO) {
                val assetsDir = File(context.filesDir, "assets")
                if (!assetsDir.exists()) assetsDir.mkdirs()

                localVersion = assetsDir.listFiles()
                    ?.filter { it.name.startsWith("encrypted_") && it.name.endsWith(".jar") }
                    ?.mapNotNull { it.name.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() }
                    ?.maxOrNull() ?: 0L

                // Use fallback utility for version check
                val responseStr = UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/hot-update-md5")

                val json = JSONObject(responseStr)
                val serverFileName = json.getString("fileName")
                serverVersion = serverFileName.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() ?: 0L

                // Fetch update log from server
                try {
                    val updateLogResponse = UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/update-log")
                    val updateLogJson = JSONObject(updateLogResponse)
                    val updatesArray: JSONArray = updateLogJson.getJSONArray("updates")
                    val logBuilder = StringBuilder("更新记录:\n")
                    for (i in 0 until updatesArray.length()) {
                        logBuilder.append(updatesArray.getString(i))
                        if (i < updatesArray.length() - 1) {
                            logBuilder.append("\n")
                        }
                    }
                    updateLog = logBuilder.toString()
                } catch (e: Exception) {
                    updateLog = "获取更新记录失败: ${e.message}"
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadVersionInfo(context)
    }

    val footer = "\n\n更多更新记录可前往官网教程文档内查看"

    val messageText = when {
        isLoading -> "正在检查版本信息..."
        errorMessage != null -> "检查版本失败: $errorMessage"
        localVersion != null && serverVersion != null -> {
            if (serverVersion!! > localVersion!!) {
                "检测到新版本!\n当前版本: $localVersion，服务器版本: $serverVersion\n\n$updateLog$footer"
            } else {
                "当前已是最新版本\n当前版本: $localVersion，服务器版本: $serverVersion\n\n$updateLog$footer"
            }
        }
        else -> "无法获取版本信息\n\n$updateLog$footer"
    }

    val scrollState = rememberScrollState()

    CustomAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "更新日志",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
        },
        text = {
            Text(
                text = messageText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("明白了", color = AppColors.Primary)
            }
        }
    )
}
