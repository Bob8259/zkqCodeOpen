@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import android.graphics.Bitmap
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
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
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@Composable
fun BugReport(onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
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
                text = "问题反馈",
                color = Color.Black,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "提交截图前，请先与作者/客服联系，确认出现问题的页面。\n确认页面后，请进入问题页面，并点击提交截图。\n提交截图后，请告知作者/客服截图已提交(一定要告知，否则截图无效)。",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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
                    text = "提交截图",
                    marginTop = 0.dp,
                    enable = !isSubmitting,
                    onClick = {
                        isSubmitting = true
                        statusMessage = "正在提交..."

                        // Auto-generate image name using timestamp
                        val nameToSubmit = "bug_${System.currentTimeMillis()}"
                        // Make window invisible so screencap sees the real screen
                        isTransparent = true

                        scope.launch {
                            try {
                                // Wait for the transparent recomposition to render
                                ShowMessage("截屏中，请耐心等待")
                                delay(300)
                                val sanitizedName =
                                    if (nameToSubmit.endsWith(".png")) nameToSubmit else "$nameToSubmit.png"

                                val screenshotBitmap =
                                    ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                                if (screenshotBitmap == null) {
                                    statusMessage = "截图保存失败，截图为空。"
                                    return@launch
                                }

                                // Encode the captured bitmap in the app process so upload does not
                                // depend on a root-created file inside private storage.
                                val imageBytes = withContext(Dispatchers.IO) {
                                    ByteArrayOutputStream().use { outputStream ->
                                        screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                        outputStream.toByteArray()
                                    }
                                }
                                screenshotBitmap.recycle()

                                val requestBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                        "image",
                                        sanitizedName,
                                        imageBytes.toRequestBody("image/png".toMediaTypeOrNull())
                                    )
                                    .build()

                                val uploadUrl = "${BuildConfig.BASE_URL}api/bug-report"

                                val request = Request.Builder()
                                    .url(uploadUrl)
                                    .post(requestBody)
                                    .build()

                                val response = withContext(Dispatchers.IO) {
                                    OkHttpClient().newCall(request).execute()
                                }

                                response.use { resp ->
                                    val body = resp.body.string()
                                    statusMessage = if (resp.isSuccessful) {
                                        "截图提交成功！请与作者/客服联系。"
                                    } else {
                                        translateServerError(body)
                                    }
                                }
                            } catch (e: Exception) {
                                statusMessage = "提交失败：${e.message}"
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
            "服务器截图已满，上传失败。请与作者/客服联系。"

        message.contains("No file uploaded", ignoreCase = true) ->
            "未上传文件。"

        message.contains("Missing", ignoreCase = true) && message.contains("image", ignoreCase = true) ->
            "表单数据中缺少图片字段。"

        else ->
            "提交失败：$message"
    }
}
