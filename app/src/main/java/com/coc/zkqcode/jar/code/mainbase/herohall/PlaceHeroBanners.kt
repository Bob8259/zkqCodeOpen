package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BuildButtonType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.moveWithDelay
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.tryToFindBuildPosition
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory

suspend fun placeHeroBanners(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.PLACE_HERO_BANNERS, InGamesVars.currentAccountNumber
    )

    // Guard: only attempt banner placement once per day (1440 minutes)
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号今日已检测战旗")
        return true
    }
    ShowMessage("准备检测是否卡战旗")
    val result = withHeroHall { placeHeroBannerAction() }

    // Always record completion and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return result
}

private suspend fun placeHeroBannerAction() {
    val redExclamationMark = findMultiColorsUntil(schemas = listOf(MyColors.RedExclamationMark), duration = 500)
    // If there is no marker, we only need to close the panel.
    if (redExclamationMark == null) {
        clickRightBottom(1)
        return
    }

    TouchActions.tap(598, 591, delayTime = 300) // Place hero banner page
    val placeHeroBanner = findMultiColorsUntil(schemas = listOf(MyColors.PlaceBannerButton), duration = 500)

    if (placeHeroBanner != null) {
        TouchActions.tap(placeHeroBanner.x, placeHeroBanner.y, delayTime = 300)
        // Locate the confirmation button (Green Tick).
        var targetTick = mainBaseFindBuildButton(type = BuildButtonType.Tick)
        if (targetTick == null) {
            ShowMessage("建造失败，尝试寻找空位")
            val redCross = mainBaseFindBuildButton(type = BuildButtonType.Cross)
            if (redCross != null) {
                val centerX = redCross.x + 20
                val centerY = redCross.y + 45
                TouchActions.touchDown(centerX.toFloat(), centerY.toFloat(), 1)
                moveWithDelay(635F, 330F)
                zoomSmallMainBase(isForBuild = true)
                targetTick = tryToFindBuildPosition(BaseType.Main)
            } else {
                ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
                BugReporter.takeScreenshot("Red_Cross_Not_Found")
                killGame()
                clickRightBottom(1)
                return
            }
        }

        if (targetTick != null) {
            ShowMessage("放战旗成功")
            TouchActions.tap(targetTick.x, targetTick.y, delayTime = 300)
        }
    } else {
        ShowMessage("未检测到放战旗按钮")
        clickRightBottom(1)
    }
}
