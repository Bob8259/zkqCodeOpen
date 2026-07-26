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
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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
                text = Strings.bugReportTitle,
                color = Color.Black,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = Strings.bugReportInstructions,
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
                    text = Strings.submitScreenshot,
                    marginTop = 0.dp,
                    enable = !isSubmitting,
                    onClick = {
                        isSubmitting = true
                        statusMessage = Strings.submitting

                        // Auto-generate image name using timestamp
                        val nameToSubmit = "bug_${System.currentTimeMillis()}"
                        // Make window invisible so screencap sees the real screen
                        isTransparent = true

                        scope.launch {
                            try {
                                // Wait for the transparent recomposition to render
                                accountMessage(Strings.capturing)
                                delay(300)
                                val sanitizedName =
                                    if (nameToSubmit.endsWith(".png")) nameToSubmit else "$nameToSubmit.png"

                                val screenshotBitmap =
                                    ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
                                if (screenshotBitmap == null) {
                                    statusMessage = Strings.screenshotFailed
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

                                // Use fallback utility for upload and surface known server errors.
                                val responseBody = withContext(Dispatchers.IO) {
                                    UrlFallbackUtil.executeWithFallbackAndReadBody(
                                        endpoint = "api/bug-report",
                                        requestBuilder = { baseUrl ->
                                            Request.Builder()
                                                .url("${baseUrl}api/bug-report")
                                                .post(requestBody)
                                                .build()
                                        }
                                    )
                                }

                                statusMessage = if (hasKnownServerError(responseBody)) {
                                    translateServerError(responseBody)
                                } else {
                                    Strings.submitSuccess
                                }
                            } catch (e: Exception) {
                                val errorMessage = e.message.orEmpty()
                                statusMessage = if (hasKnownServerError(errorMessage)) {
                                    translateServerError(errorMessage)
                                } else {
                                    Strings.submitFailed(e.message)
                                }
                            } finally {
                                // Restore the window so the user sees the result
                                isTransparent = false
                                isSubmitting = false
                            }
                        }
                    }
                )

                CustomButton(
                    text = Strings.closeWindow,
                    marginTop = 0.dp,
                    onClick = {
                        GlobalVars.isPlaying.value = false
                        GlobalVars.updateWindowPosition = true
                        scope.launch {
                            onClose()
                            // Switch mode after onClose() so the composition (and its
                            // coroutine scope) stays alive until the close work finishes.
                            AppStateManager.setMode(AppMode.Run)
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

private fun hasKnownServerError(responseBody: String): Boolean {
    val message = extractServerMessage(responseBody)
    return message.contains("folder is full", ignoreCase = true) ||
            message.contains("No file uploaded", ignoreCase = true) ||
            (message.contains("Missing", ignoreCase = true) && message.contains("image", ignoreCase = true))
}

// Translate known server error messages to Chinese
private fun translateServerError(responseBody: String): String {
    val message = extractServerMessage(responseBody)

    return when {
        message.contains("folder is full", ignoreCase = true) ->
            Strings.serverFolderFull

        message.contains("No file uploaded", ignoreCase = true) ->
            Strings.noFileUploaded

        message.contains("Missing", ignoreCase = true) && message.contains("image", ignoreCase = true) ->
            Strings.missingImageField

        else ->
            Strings.submitFailed(message)
    }
}

private fun extractServerMessage(responseBody: String): String {
    return try {
        JSONObject(responseBody).optString("message", responseBody)
    } catch (_: Exception) {
        responseBody
    }
}
