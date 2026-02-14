package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.killGame


object FindBuildPosition {
    suspend fun tryToFindBuildPosition(crossX: Int, crossY: Int): Point? {
        val centerX = crossX + 20
        val centerY = crossY + 45
        TouchActions.swipe(centerX, centerY, 620, 720, delayTime = 600)
        delayWithMultiplier(50)
        TouchActions.swipe(990, 700, 990, 380, delayTime = 600)
        val redCross = nightBaseFindBuildButton(type = "Cross")
        if (redCross != null) {
            TouchActions.touchDown((redCross.x + 20).toFloat(), (redCross.y + 45).toFloat(), 1)
            delayWithMultiplier(100)
            return iterateThroughAllPossiblePositions()
        } else {
            ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
            BugReporter.takeScreenshot("Red_Cross_Not_Found")
            killGame()
            clickRightBottom()
            return null
        }
    }

    private suspend fun iterateThroughAllPossiblePositions(): Point? {
        val step = 10

        // 1. Trapezoid area (y: 130 to 300)
        for (y in 130..300 step step) {
            val ratio = (y - 130).toFloat() / (300 - 130)
            val startX = (440 + (170 - 440) * ratio).toInt()
            val endX = (790 + (1080 - 790) * ratio).toInt()
            val result = checkArea(startX, endX, y, step)
            if (result != null) {
                TouchActions.touchUp(1)
                return result
            }
        }

        // 2. Rectangle area (y: 301 to 380)
        for (y in 301..380 step step) {
            val result = checkArea(170, 1080, y, step)
            if (result != null) {
                TouchActions.touchUp(1)
                return result
            }
        }

        // 3. Triangle area (y: 381 to 690)
        for (y in 381..690 step step) {
            val ratio = (y - 381).toFloat() / (690 - 381)
            val startX = (170 + (625 - 170) * ratio).toInt()
            val endX = (1080 + (625 - 1080) * ratio).toInt()
            val result = checkArea(startX, endX, y, step)
            if (result != null) {
                TouchActions.touchUp(1)
                return result
            }
        }
        TouchActions.touchUp(1)
        return null
    }

    private suspend fun checkArea(startX: Int, endX: Int, y: Int, step: Int): Point? {
        for (x in startX..endX step step) {
            TouchActions.touchMove(x.toFloat(), y.toFloat(), id = 1, isJitter = false)
            var greenTick = nightBaseFindBuildButton(type = "Tick", duration = 220)
            if (greenTick != null) {
                delayWithMultiplier(200)
                TouchActions.touchUp(1)
                greenTick = nightBaseFindBuildButton(type = "Tick", duration = 80)
                if (greenTick != null) {
                    delayWithMultiplier(100)
                    TouchActions.tap(greenTick.x, greenTick.y)
                    return greenTick
                }
            }
        }
        return null
    }
}