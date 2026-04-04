package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.ShowMessage.invoke
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.builderbase.upgrade.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BuildButtonType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.moveWithDelay
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.tryToFindBuildPosition
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.yolo.tiledYoloDetect
import kotlinx.coroutines.delay

suspend fun placeHeroBanners(): Boolean {
    clickRightBottom(1)
    zoomSmallMainBase()

    // First scan: detect and tap hero halls sorted by confidence (highest first)
    val firstScan = tiledYoloDetect(
        modelName = "building-detect", callerTag = "FindHeroHall", classIndex = 3
    )
    for (detection in firstScan) {
        val box = detection.boundingBox
        if (tryToOpenHeroHall(box.centerX().toInt(), box.centerY().toInt())) return enterMainScreen()
    }

    // Swipe to reveal more of the base
    swipe(900, 130, 0, 720)

    // Second scan: detect and tap hero halls in the newly visible area
    val secondScan = tiledYoloDetect(
        modelName = "building-detect", callerTag = "FindHeroHall", classIndex = 3
    )
    for (detection in secondScan) {
        val box = detection.boundingBox
        if (tryToOpenHeroHall(box.centerX().toInt(), box.centerY().toInt())) return enterMainScreen()
    }

    return enterMainScreen()
}

suspend fun tryToOpenHeroHall(x: Int, y: Int): Boolean {
    TouchActions.tap(x, y, delayTime = 300)
    val openHeroHallIcon = findMultiColorsUntil(schemas = listOf(MyColors.OpenHeroHall), duration = 500)
    if (openHeroHallIcon != null) {
        TouchActions.tap(openHeroHallIcon.x, openHeroHallIcon.y, delayTime = 600)
        TouchActions.tap(598, 591, delayTime = 300)//Place hero banner page
        val placeHeroBanner = findMultiColorsUntil(schemas = listOf(MyColors.PlaceBannerButton), duration = 500)

        if (placeHeroBanner != null) {
            TouchActions.tap(placeHeroBanner.x, placeHeroBanner.y, delayTime = 300)
            //Locate the confirmation button (Green Tick)
            var targetTick = mainBaseFindBuildButton(type = BuildButtonType.Tick)
            if (targetTick == null) {
                ShowMessage("建造失败，尝试寻找空位")
                val redCross = mainBaseFindBuildButton(type = BuildButtonType.Cross)
                if (redCross != null) {
                    val centerX = redCross.x + 20
                    val centerY = redCross.y + 45
                    val downX = centerX.toFloat()
                    val downY = centerY.toFloat()
                    TouchActions.touchDown(downX, downY, 1)
                    moveWithDelay(635F, 330F)
                    zoomSmallMainBase(isForBuild = true)
                    targetTick = tryToFindBuildPosition(BaseType.Main)
                } else {
                    ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
                    BugReporter.takeScreenshot("Red_Cross_Not_Found")
                    killGame()
                    clickRightBottom(1)
                    return true
                }
            }

            if (targetTick != null) {
                ShowMessage("放战旗成功")
                TouchActions.tap(targetTick.x, targetTick.y, delayTime = 300)
            }
        } else {
            clickRightBottom(1)
        }
        return true
    }
    return false
}