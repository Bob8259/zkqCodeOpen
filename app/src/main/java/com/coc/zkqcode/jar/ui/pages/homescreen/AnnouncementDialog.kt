package com.coc.zkqcode.jar.ui.pages.homescreen

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
fun AnnouncementDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var localVersion by remember { mutableStateOf<Long?>(null) }
    var serverVersion by remember { mutableStateOf<Long?>(null) }
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

                val httpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                val baseUrl = BuildConfig.BASE_URL
                val checkRequest = Request.Builder()
                    .url("${baseUrl}api/hot-update-md5")
                    .get()
                    .build()

                val responseStr = httpClient.newCall(checkRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Version check failed: ${response.code}")
                    }
                    response.body.string()
                }

                val json = JSONObject(responseStr)
                val serverFileName = json.getString("fileName")
                serverVersion = serverFileName.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() ?: 0L
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

    val updateLog = """
        更新记录:
        2026-04-18:
        - 新增UI背景，修复部分设备都城配兵问题
        2026-04-16:
        - 修复部分设备不升级战宠的问题
        - 优化下兵逻辑
    """.trimIndent()

    val messageText = when {
        isLoading -> "正在检查版本信息..."
        errorMessage != null -> "检查版本失败: $errorMessage"
        localVersion != null && serverVersion != null -> {
            if (serverVersion!! > localVersion!!) {
                "检测到新版本!\n当前版本: $localVersion，服务器版本: $serverVersion\n\n$updateLog"
            } else {
                "当前已是最新版本\n当前版本: $localVersion，服务器版本: $serverVersion\n\n$updateLog"
            }
        }
        else -> "无法获取版本信息\n\n$updateLog"
    }

    CustomAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "公告",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
        },
        text = {
            Text(
                text = messageText,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("明白了", color = AppColors.Primary)
            }
        }
    )
}
