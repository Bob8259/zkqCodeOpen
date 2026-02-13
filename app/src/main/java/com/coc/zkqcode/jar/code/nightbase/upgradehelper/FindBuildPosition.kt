package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.ShowMessage.invoke
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.killGame


object FindBuildPosition {
    suspend fun tryToFindBuildPosition(crossX: Int, crossY: Int) {
        val centerX = crossX + 20
        val centerY = crossY + 45
        TouchActions.swipe(centerX, centerY, 620, 720, delayTime = 600)
        val redCross = nightBaseFindBuildButton(type = "Cross")
        if (redCross != null) {
            
        } else {
            ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
            BugReporter.takeScreenshot("Red_Cross_Not_Found")
            killGame()
            clickRightBottom()
        }
        delayWithMultiplier(10000)
    }
}