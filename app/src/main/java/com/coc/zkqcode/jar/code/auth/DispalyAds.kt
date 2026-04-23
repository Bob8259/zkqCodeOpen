package com.coc.zkqcode.jar.code.auth

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.floatingwindows.AdItem
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray

// Fetch ads from the server, display them in an overlay for the given duration,
// bracketed by native start/end markers for tamper detection.
// Falls back to a simple countdown if the API request fails.
suspend fun displayAds() {
    if (!GlobalVars.isShowAd) return
    waitForPlay()
    RustTools.markAdStart()

    // Fetch ad data from the server API with fallback
    val adItems = try {
        val body = withContext(Dispatchers.IO) {
            UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/get-ad")
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
    ShowMessage.showAdOverlay(adItems, InGameVars.adTime)
    delay(InGameVars.adTime * 1000L)
    ShowMessage.dismissAdOverlay()
    RustTools.markAdEnd()
}