package com.coc.zkqcode.jar.code.auth

import com.coc.zkqcode.core.util.basic.ShowMessage
import kotlinx.coroutines.delay

// Display a placeholder ad countdown for 15 seconds
suspend fun displayAds() {
    for (remaining in 15 downTo 1) {
        ShowMessage("广告倒计时：${remaining}秒")
        delay(1000L)
    }
    ShowMessage("广告已结束")
}