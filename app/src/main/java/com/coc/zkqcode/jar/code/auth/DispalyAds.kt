package com.coc.zkqcode.jar.code.auth

import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.floatingwindows.AdItem
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

private val httpClient = OkHttpClient()

// Fetch ads from the server, display them in an overlay for the given duration,
// bracketed by native start/end markers for tamper detection.
// Falls back to a simple countdown if the API request fails.
suspend fun displayAds() {
    if (!GlobalVars.isShowAd) return
    waitForPlay()
    RustTools.markAdStart()

    // Fetch ad data from the server API
    val adItems = try {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/get-ad")
            .get()
            .build()
        val body = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute().use { it.body.string() }
        }
        val arr = JSONArray(body)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            AdItem(
                content = obj.getString("content"),
                link = if (obj.isNull("link")) null else obj.getString("link"),
                topAd = obj.optInt("top_ad", 0)
            )
        }
    } catch (_: Exception) {
        // Default ads when the API request fails
        listOf(
            AdItem(content = "测试普通广告", link = null, topAd = 0)
        )
    }
    ShowMessage.showAdOverlay(adItems, InGamesVars.adTime)
    delay(InGamesVars.adTime * 1000L)
    ShowMessage.dismissAdOverlay()
    RustTools.markAdEnd()
}