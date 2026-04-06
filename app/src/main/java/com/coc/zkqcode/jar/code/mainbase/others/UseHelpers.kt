package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun useHelpers(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.HELPER_SETTINGS.key)) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.USE_HELPERS, InGamesVars.currentAccountNumber
    )

    // Guard the helper scan so each account only retries every 22 hours after a successful tap.
    if (!checkMemoryFile(storageKey, 1320)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号今日已检测助手")
        return true
    }

    clickRightBottom(1)
    zoomSmallMainBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapHelpers(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "UseHelpers", classIndex = 2
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt(), delayTime = 500)
            // Follow-up helper actions are intentionally left blank for now.
            writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
            clickRightBottom(1)
            return true
        }
        return false
    }

    if (!scanAndTapHelpers()) {
        swipe(900, 130, 0, 720)
        scanAndTapHelpers()
    }
    return enterMainScreen()
}