package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Bitmap
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

private const val CLAN_GAME_MEMORY_KEY = "clan_game_last_run"
private const val CLAN_GAME_INTERVAL_MINUTES = 60

suspend fun clanGame(): Boolean {
    val calendar = findMultiColors(MyColors.Calendar)
    if (calendar != null && checkMemoryFile(CLAN_GAME_MEMORY_KEY, CLAN_GAME_INTERVAL_MINUTES)) {
        TouchActions.tap(calendar.x, calendar.y)
        for (i in 0..2) {
            val openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = 500)
            if (openClanGame != null) {
                ShowMessage("当前部落竞赛未制作完成，正在收集数据中，请稍后。")
                TouchActions.tap(openClanGame.x, openClanGame.y, delayTime = 1000)
                // Upload a screenshot to the server
                uploadScreenshot("clan_game_${System.currentTimeMillis()}")
                TouchActions.swipe(700, 500, 600, 20, delayAfter = 300)
                // Upload a screenshot to the server again
                uploadScreenshot("clan_game_${System.currentTimeMillis()}")
            }
            TouchActions.swipe(600, 660, 600, 120)
        }
        writeMemory(CLAN_GAME_MEMORY_KEY, (System.currentTimeMillis() / 60_000).toString())
    }
    return enterMainScreen()
}

private suspend fun uploadScreenshot(name: String) {
    try {
        val bitmap = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        if (bitmap == null) {
            ShowMessage("截图失败，截图为空。")
            return
        }

        val imageBytes = withContext(Dispatchers.IO) {
            ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        }
        bitmap.recycle()

        val fileName = if (name.endsWith(".png")) name else "$name.png"
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                fileName,
                imageBytes.toRequestBody("image/png".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/bug-report")
            .post(requestBody)
            .build()

        val response = withContext(Dispatchers.IO) {
            OkHttpClient().newCall(request).execute()
        }

        response.use { resp ->
            if (resp.isSuccessful) {
                ShowMessage("截图上传成功: $fileName")
            } else {
                ShowMessage("截图上传失败: ${resp.code}")
            }
        }
    } catch (e: Exception) {
        ShowMessage("截图上传异常: ${e.message}")
    }
}