package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema


suspend fun collectClanCastleResources(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key)) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.COLLECT_CLAN_CASTLE_RESOURCES, InGamesVars.currentAccountNumber
    )

    // Guard the clan castle check so each account only scans once per day.
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号今日已检测城堡资源")
        return true
    }

    clickRightBottom(1)
    zoomSmallMainBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapClanCastle(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "CollectClanCastleResources", classIndex = 0
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt(), delayTime = 500)
            val openClanCastle = findMultiColors(MyColors.OpenClanCastle)
            if (openClanCastle != null) {
                TouchActions.tap(openClanCastle.x, openClanCastle.y, delayTime = 500)
                TouchActions.tap(653, 504, delayTime = 300)//Collect
                // Record memory immediately after the collection action succeeds.
                writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
                clickRightBottom(1)
                return true
            }
        }
        return false
    }

    if (!scanAndTapClanCastle()) {
        swipe(900, 130, 0, 720)
        scanAndTapClanCastle()
    }
    return enterMainScreen()
}

