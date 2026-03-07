package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.builderbase.upgrade.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlin.random.Random


private var lastX = 0f
private var lastY = 0f

private suspend fun moveWithDelay(x: Float, y: Float) {
    val dx = x - lastX
    val dy = y - lastY
    val distance = sqrt(dx * dx + dy * dy)
    if (distance > 15) {
        val duration = Random.nextInt(200, 301)
        TouchActions.moveSmoothly(lastX, lastY, x, y, duration, id = 1, isJitter = false)
        delayWithMultiplier(200)
    } else {
        TouchActions.touchMove(x, y, id = 1, isJitter = false)
    }
    lastX = x
    lastY = y
}

suspend fun tryToFindBuildPosition(baseType: BaseType): Point? {

    val redCross = if (baseType == BaseType.Main) mainBaseFindBuildButton(type = BuildButtonType.Cross) else builderBaseFindBuildButton(type = BuildButtonType.Cross)

    if (redCross != null) {
        val centerX = redCross.x + 20
        val centerY = redCross.y + 45
        val downX = centerX.toFloat()
        val downY = centerY.toFloat()
        TouchActions.touchDown(downX, downY, 1)
        try {
            lastX = downX
            lastY = downY
            delayWithMultiplier(100)
            return iterateThroughAllPossiblePositions(baseType)
        } finally {
            withContext(NonCancellable) {
                TouchActions.touchUp(1)
            }
        }
    } else {
        ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
        BugReporter.takeScreenshot("Red_Cross_Not_Found")
        killGame()
        clickRightBottom(1)
        return null
    }
}

private suspend fun iterateThroughAllPossiblePositions(baseType: BaseType): Point? {
    val stepX = 15
    val stepY = 20

    val areaIndices = listOf(1, 2, 3).shuffled(Random(System.nanoTime()))

    for (index in areaIndices) {
        val result = when (index) {
            1 -> {
                // 1. Trapezoid area (y: 140 to 300)
                // Randomly reverse Y and X axes to add randomness while maintaining the same area
                val reverseY = Random.nextBoolean()
                val reverseX = Random.nextBoolean()
                val yStart = if (reverseY) 300 else 140
                val yEnd = if (reverseY) 140 else 300

                var found: Point? = null
                val yRange = if (yStart <= yEnd) (yStart..yEnd step stepY) else (yStart downTo yEnd step stepY)
                for (y in yRange) {
                    val ratio = (y - 140).toFloat() / (300 - 140)
                    val leftX = (435 + (165 - 435) * ratio).toInt()
                    val rightX = (795 + (1085 - 795) * ratio).toInt()
                    val xStart = if (reverseX) rightX else leftX
                    val xEnd = if (reverseX) leftX else rightX

                    val checkResult = checkArea(xStart, xEnd, y, stepX, baseType)
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
                    val checkResult = checkArea(170, 1080, y, stepX, baseType)
                    if (checkResult != null) {
                        found = checkResult
                        break
                    }
                }
                found
            }

            3 -> {
                // 3. Triangle area (y: 381 to 690)
                // Randomly reverse Y and X axes to add randomness while maintaining the same area
                val reverseY = Random.nextBoolean()
                val reverseX = Random.nextBoolean()
                val yStart = if (reverseY) 690 else 381
                val yEnd = if (reverseY) 381 else 690

                var found: Point? = null
                val yRange = if (yStart <= yEnd) (yStart..yEnd step stepY) else (yStart downTo yEnd step stepY)
                for (y in yRange) {
                    val ratio = (y - 381).toFloat() / (690 - 381)
                    // Move left x boundary 5 pixels left, and move right x boundary 5 pixels right
                    val leftX = (160 + (615 - 160) * ratio).toInt()
                    val rightX = (1090 + (635 - 1090) * ratio).toInt()
                    val xStart = if (reverseX) rightX else leftX
                    val xEnd = if (reverseX) leftX else rightX

                    val checkResult = checkArea(xStart, xEnd, y, stepX, baseType)
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
            return result
        }
    }

    return null
}

private suspend fun checkArea(startX: Int, endX: Int, y: Int, step: Int, baseType: BaseType): Point? {
    // Support both ascending and descending X ranges
    val xRange = if (startX <= endX) (startX..endX step step) else (startX downTo endX step step)
    for (x in xRange) {
        moveWithDelay(x.toFloat(), y.toFloat())
        var greenTick = if (baseType == BaseType.Main) mainBaseFindBuildButton(type = BuildButtonType.Tick, duration = 120) else builderBaseFindBuildButton(type = BuildButtonType.Tick, duration = 120)

        if (greenTick != null) {
            delayWithMultiplier(200)
            TouchActions.touchUp(1)
            greenTick = if (baseType == BaseType.Main) mainBaseFindBuildButton(type = BuildButtonType.Tick, duration = 80) else builderBaseFindBuildButton(type = BuildButtonType.Tick, duration = 80)

            if (greenTick != null) {
                delayWithMultiplier(100)//Do not click the green tick here
                return greenTick
            }
        }
    }
    return null
}

