package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

@Composable
fun BugReport(onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageName by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    // Hide the UI so screencap captures the screen behind the transparent window
    var isTransparent by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .alpha(if (isTransparent) 0f else 1f)
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .drawVerticalScrollbar(scrollState)
                .padding(end = 6.dp)
        ) {
            Text(
                text = "Bug反馈",
                color = Color.Black,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "提交截图后，请与作者/客服联系。",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Image name input row
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "图片名称:",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.labelMedium
                )
                BasicTextField(
                    value = imageName,
                    onValueChange = { imageName = it },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .heightIn(max = 120.dp)
                )
            }

            // Status / error message
            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Row {
                CustomButton(
                    text = "提交",
                    marginTop = 0.dp,
                    enable = !isSubmitting,
                    onClick = {
                        if (imageName.isBlank()) {
                            statusMessage = "请输入图片名称"
                            return@CustomButton
                        }
                        isSubmitting = true
                        statusMessage = "正在提交..."

                        val nameToSubmit = imageName
                        // Capture private path before launching coroutine
                        val privateDir = context.filesDir.absolutePath
                        // Make window invisible so screencap sees the real screen
                        isTransparent = true

                        scope.launch {
                            try {
                                // Wait for the transparent recomposition to render
                                delay(300)

                                val sanitizedName =
                                    if (nameToSubmit.endsWith(".png")) nameToSubmit else "$nameToSubmit.png"
                                val dir = "$privateDir/bugReport"
                                val fullPath = "$dir/$sanitizedName"

                                ShowMessage.invoke("[debug] 开始截图: $fullPath")

                                // Take screenshot via root shell, chmod so the app can read the file
                                RunShell.runNoOutput(
                                    "mkdir -p $dir && screencap -p $fullPath && chmod 644 $fullPath",
                                    isCheckIsPlaying = false
                                )
                                delay(1500)

                                val file = File(fullPath)
                                if (!file.exists()) {
                                    statusMessage = "截图保存失败，文件不存在。"
                                    ShowMessage.invoke("[debug] 截图文件不存在: $fullPath")
                                    return@launch
                                }
                                ShowMessage.invoke("[debug] 截图完成, 文件大小: ${file.length()} bytes, 开始上传...")

                                val requestBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                        "image",
                                        sanitizedName,
                                        file.asRequestBody("image/png".toMediaTypeOrNull())
                                    )
                                    .build()

                                val uploadUrl = "${BuildConfig.BASE_URL}api/bug-report"
                                ShowMessage.invoke("[debug] 上传地址: $uploadUrl")

                                val request = Request.Builder()
                                    .url(uploadUrl)
                                    .post(requestBody)
                                    .build()

                                val response = withContext(Dispatchers.IO) {
                                    OkHttpClient().newCall(request).execute()
                                }

                                response.use { resp ->
                                    val body = resp.body.string()
                                    ShowMessage.invoke("[debug] 服务器返回: code=${resp.code}, body=$body")
                                    if (resp.isSuccessful) {
                                        statusMessage = "截图提交成功！"
                                    } else {
                                        statusMessage = translateServerError(body)
                                    }
                                }
                            } catch (e: Exception) {
                                statusMessage = "提交失败：${e.message}"
                                ShowMessage.invoke("[debug] 异常: ${e.javaClass.simpleName}: ${e.message}")
                            } finally {
                                // Restore the window so the user sees the result
                                isTransparent = false
                                isSubmitting = false
                            }
                        }
                    }
                )

                CustomButton(
                    text = "关闭窗口",
                    marginTop = 0.dp,
                    onClick = {
                        AppStateManager.setMode(AppMode.Run)
                        GlobalVars.isPlaying.value = false
                        GlobalVars.updateWindowPosition = true
                        scope.launch {
                            onClose()
                        }
                    }
                )
            }
        }
    }
}

// Draw a vertical scrollbar thumb on the right edge of the composable
private fun Modifier.drawVerticalScrollbar(
    scrollState: ScrollState,
    color: Color = Color.Gray
): Modifier = drawWithContent {
    drawContent()
    val scrollableHeight = scrollState.maxValue.toFloat()
    if (scrollableHeight > 0f) {
        val visibleHeight = size.height
        val totalHeight = visibleHeight + scrollableHeight
        val thumbHeight = (visibleHeight / totalHeight) * visibleHeight
        val thumbOffset = (scrollState.value / scrollableHeight) * (visibleHeight - thumbHeight)
        val barWidth = 4.dp.toPx()
        drawRoundRect(
            color = color.copy(alpha = 0.4f),
            topLeft = Offset(size.width - barWidth, thumbOffset),
            size = Size(barWidth, thumbHeight),
            cornerRadius = CornerRadius(barWidth / 2f)
        )
    }
}

// Translate known server error messages to Chinese
private fun translateServerError(responseBody: String): String {
    val message = try {
        JSONObject(responseBody).optString("message", responseBody)
    } catch (_: Exception) {
        responseBody
    }

    return when {
        message.contains("folder is full", ignoreCase = true) ->
            "Bug截图文件夹已满，最多允许5张图片。"
        message.contains("No file uploaded", ignoreCase = true) ->
            "未上传文件。"
        message.contains("Missing", ignoreCase = true) && message.contains("image", ignoreCase = true) ->
            "表单数据中缺少图片字段。"
        else ->
            "提交失败：$message"
    }
}
