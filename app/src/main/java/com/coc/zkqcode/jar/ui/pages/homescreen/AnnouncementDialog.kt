package com.coc.zkqcode.jar.ui.pages.homescreen

import android.content.Context
import androidx.compose.foundation.layout.Column
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
import com.coc.zkqcode.jar.ui.Strings
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
    var updateLog by remember { mutableStateOf(Strings.fetchingUpdates) }
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
                    val logBuilder = StringBuilder(Strings.updateLogHeader)
                    for (i in 0 until updatesArray.length()) {
                        logBuilder.append(updatesArray.getString(i))
                        if (i < updatesArray.length() - 1) {
                            logBuilder.append("\n")
                        }
                    }
                    updateLog = logBuilder.toString()
                } catch (e: Exception) {
                    updateLog = Strings.fetchFailed(e.message)
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

    val footer = Strings.moreLogsFooter

    val messageText = when {
        isLoading -> Strings.checkingVersion
        errorMessage != null -> Strings.checkFailed(errorMessage)
        localVersion != null && serverVersion != null -> {
            if (serverVersion!! > localVersion!!) {
                "${Strings.newVersionDetected(localVersion!!, serverVersion!!)}\n\n$updateLog$footer"
            } else {
                "${Strings.alreadyLatest(localVersion!!, serverVersion!!)}\n\n$updateLog$footer"
            }
        }
        else -> "${Strings.versionInfoUnavailable}\n\n$updateLog$footer"
    }

    val scrollState = rememberScrollState()

    CustomAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Strings.changelogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.gotIt, color = AppColors.Primary)
            }
        }
    )
}
