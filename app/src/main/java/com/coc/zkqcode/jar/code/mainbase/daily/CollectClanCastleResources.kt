package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema


suspend fun collectClanCastleResources(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key) || InGameVars.townHallLevel < 3) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.COLLECT_CLAN_CASTLE_RESOURCES, InGameVars.currentAccountNumber
    )

    // Guard the clan castle check so each account only scans once per day.
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("该账号今日已检测城堡资源")
        return true
    }
    accountMessage("正在检测城堡资源")
    clickRightBottom(1)
    zoomSmallMainBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapClanCastle(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "CollectClanCastleResources", classIndex = 0
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
            val openClanCastle = findMultiColorsUntil(
                schemas = listOf(MyColors.OpenClanCastle, MyColors.OpenClanCastle7, MyColors.OpenClanCastle2, MyColors.OpenClanCastle3, MyColors.OpenClanCastle4, MyColors.OpenClanCastle5), duration = 200
            )
            if (openClanCastle != null) {
                TouchActions.tap(openClanCastle.x, openClanCastle.y)
                TouchActions.tap(653, 504)//Collect
                TouchActions.tap(777, 437)//Confirm Collection
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

