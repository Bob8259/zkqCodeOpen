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
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

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

                // Use fallback utility for version check
                val responseStr = UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/hot-update-md5")

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
        2026-04-25:
        - 新增部落竞赛的功能
        - 修复夜世界升级列表错误的问题
        2026-04-23:
        - 修复温度监控功能不关闭游戏的问题
        - 修复改装建筑导致主世界升级卡住的问题
        - 新增主世界改装建筑功能，独立于普通升级建筑
        - 已完成部落竞赛的AI部分
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
