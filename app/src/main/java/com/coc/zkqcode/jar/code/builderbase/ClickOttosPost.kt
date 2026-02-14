package com.coc.zkqcode.jar.code.builderbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import java.util.Calendar

suspend fun clickOttosOutPost() {
    val storageKey = "ClickOttosPost${InGamesVars.currentAccountNumber}"
    val lastClickDay = readMemory(storageKey).toIntOrNull()
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    if (lastClickDay != null && lastClickDay == currentDay) {
        ShowMessage("今日已检测奥仔哨站，暂不点击")
        return
    }

    val worker = BuilderBaseWorkerAndResearch.detectWorkerNumber()
    if (worker.total < 2) {
        writeMemory(storageKey, currentDay.toString())
        return
    }
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)
    repeat(3) {
        swipe(269, 139, 1021, 502, delayTime = 100)
    }
    swipe(269, 139, 1021, 502, delayTime = 800)
    repeat(5) {
        val arrow = findMultiColorsUntil(schemas = listOf(MyColors.OrangeTutorialArrow), duration = 500)
        if (arrow != null) {
            TouchActions.tap(arrow.x + 20, arrow.y + 100)
            delayWithMultiplier(10)
            clickRightBottom()
            delayWithMultiplier(50)
        }
    }
    writeMemory(storageKey, currentDay.toString())
}