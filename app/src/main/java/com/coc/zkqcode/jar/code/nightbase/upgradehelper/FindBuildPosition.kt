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
        delayWithMultiplier(50)
        TouchActions.swipe(990, 700, 990, 380, delayTime = 600)
        val redCross = nightBaseFindBuildButton(type = "Cross")
        if (redCross != null) {
            TouchActions.touchDown((redCross.x + 20).toFloat(), (redCross.y + 45).toFloat(), 1)
            delayWithMultiplier(100)
            iterateThroughAllPossiblePositions()
            TouchActions.touchUp(1)
        } else {
            ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
            BugReporter.takeScreenshot("Red_Cross_Not_Found")
            killGame()
            clickRightBottom()
        }
    }

    private suspend fun iterateThroughAllPossiblePositions(): Boolean {
        val step = 15

        // 1. Trapezoid area (y: 130 to 300)
        for (y in 130..300 step step) {
            val ratio = (y - 130).toFloat() / (300 - 130)
            val startX = (440 + (170 - 440) * ratio).toInt()
            val endX = (790 + (1080 - 790) * ratio).toInt()
            if (checkArea(startX, endX, y, step)) return true
        }

        // 2. Rectangle area (y: 301 to 380)
        for (y in 301..380 step step) {
            if (checkArea(170, 1080, y, step)) return true
        }

        // 3. Triangle area (y: 381 to 690)
        for (y in 381..690 step step) {
            val ratio = (y - 381).toFloat() / (690 - 381)
            val startX = (170 + (625 - 170) * ratio).toInt()
            val endX = (1080 + (625 - 1080) * ratio).toInt()
            if (checkArea(startX, endX, y, step)) return true
        }

        return false
    }

    private suspend fun checkArea(startX: Int, endX: Int, y: Int, step: Int): Boolean {
        for (x in startX..endX step step) {
            TouchActions.touchMove(x.toFloat(), y.toFloat(), id = 1, isJitter = false)
            var greenTick = nightBaseFindBuildButton(type = "Tick", duration = 200)
            if (greenTick != null) {
                delayWithMultiplier(200)
                TouchActions.touchUp(1)
                greenTick = nightBaseFindBuildButton(type = "Tick", duration = 80)
                if (greenTick != null) {
                    delayWithMultiplier(100)
                    TouchActions.tap(greenTick.x, greenTick.y)
                    return true
                }
            }
        }
        return false
    }
}