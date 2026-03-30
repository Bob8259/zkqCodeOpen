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
        null
    }
    GlobalVars.absorbYPercentage = 5
    GlobalVars.isAdPlaying = true
    if (adItems != null) {
        ShowMessage.showAdOverlay(adItems, InGamesVars.adTime)
        delay(InGamesVars.adTime * 1000L)
        ShowMessage.dismissAdOverlay()
    } else {
        // Fallback: simple countdown when API is unreachable
        for (remaining in InGamesVars.adTime downTo 1) {
            ShowMessage("获取广告失败，展示默认广告中：\n广告倒计时：${remaining}秒\n官网注册账号并赞助，可以免广告\n一天2毛5，用多久扣多少，精确到分钟。")
            delay(1000L)
        }
    }
    GlobalVars.isAdPlaying = false
    GlobalVars.absorbYPercentage = 50
    RustTools.markAdEnd()
}