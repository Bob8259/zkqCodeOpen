package com.coc.zkqcode.jar.code.builderbase.upgrade

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import kotlin.math.sqrt
import kotlin.random.Random


object FindBuildPosition {
    private var lastX = 0f
    private var lastY = 0f

    private suspend fun moveWithDelay(x: Float, y: Float) {
        val dx = x - lastX
        val dy = y - lastY
        val distance = sqrt(dx * dx + dy * dy)
        if (distance > 15) {
            val duration = Random.nextInt(100, 201).toLong()
            TouchActions.moveSmoothly(lastX, lastY, x, y, duration, id = 1, isJitter = false)
        } else {
            TouchActions.touchMove(x, y, id = 1, isJitter = false)
        }
        lastX = x
        lastY = y
    }

    suspend fun tryToFindBuildPosition(): Point? {
        val redCross = builderBaseFindBuildButton(type = "Cross")
        if (redCross != null) {
            val centerX = redCross.x + 20
            val centerY = redCross.y + 45
            val downX = centerX.toFloat()
            val downY = centerY.toFloat()
            TouchActions.touchDown(downX, downY, 1)
            lastX = downX
            lastY = downY
            delayWithMultiplier(100)
            return iterateThroughAllPossiblePositions()
        } else {
            ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
            BugReporter.takeScreenshot("Red_Cross_Not_Found")
            killGame()
            clickRightBottom(1)
            return null
        }
    }

    private suspend fun iterateThroughAllPossiblePositions(): Point? {
        val stepX = 10
        val stepY = 20

        val areaIndices = listOf(1, 2, 3).shuffled()

        for (index in areaIndices) {
            val result = when (index) {
                1 -> {
                    // 1. Trapezoid area (y: 130 to 300)
                    var found: Point? = null
                    for (y in 130..300 step stepY) {
                        val ratio = (y - 130).toFloat() / (300 - 130)
                        val startX = (440 + (170 - 440) * ratio).toInt()
                        val endX = (790 + (1080 - 790) * ratio).toInt()
                        val checkResult = checkArea(startX, endX, y, stepX)
                        if (checkResult != null) {
                            found = checkResult
                            break
                        }
                    }
                    found
                }
                2 -> {
                    // 2. Rectangle area (y: 301 to 380)
                    var found: Point? = null
                    for (y in 301..380 step stepY) {
                        val checkResult = checkArea(170, 1080, y, stepX)
                        if (checkResult != null) {
                            found = checkResult
                            break
                        }
                    }
                    found
                }
                3 -> {
                    // 3. Triangle area (y: 381 to 690)
                    var found: Point? = null
                    for (y in 381..690 step stepY) {
                        val ratio = (y - 381).toFloat() / (690 - 381)
                        val startX = (170 + (625 - 170) * ratio).toInt()
                        val endX = (1080 + (625 - 1080) * ratio).toInt()
                        val checkResult = checkArea(startX, endX, y, stepX)
                        if (checkResult != null) {
                            found = checkResult
                            break
                        }
                    }
                    found
                }
                else -> null
            }

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
            moveWithDelay(x.toFloat(), y.toFloat())
            var greenTick = builderBaseFindBuildButton(type = "Tick", duration = 120)
            if (greenTick != null) {
                delayWithMultiplier(200)
                TouchActions.touchUp(1)
                greenTick = builderBaseFindBuildButton(type = "Tick", duration = 80)
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