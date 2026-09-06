package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BuildButtonType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.moveWithDelay
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.tryToFindBuildPosition
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun placeHeroBanners(): Boolean {
    if (InGameVars.townHallLevel < 7) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.PLACE_HERO_BANNERS, InGameVars.currentAccountNumber
    )

    // Guard: only attempt banner placement once per day (1440 minutes)
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("该账号今日已检测战旗")
        return true
    }
    accountMessage("准备检测是否卡战旗")
    val result = withHeroHall { placeHeroBannerAction() }

    // Always record completion and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return result
}

private suspend fun placeHeroBannerAction() {
    val redExclamationMark = findMultiColorsUntil(schemas = listOf(MyColors.RedExclamationMark), duration = 500)
    // If there is no marker, we only need to close the panel.
    if (redExclamationMark == null) {
        if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_HERO_HALL_REWARDS.key)) {
            TouchActions.tap(820, 105)
            repeat(4) {
                for (tapX in 100..1150 step 100) {
                    TouchActions.tap(tapX, 240, delayTime = 50)
                }
                for (tapX in 100..1150 step 100) {
                    TouchActions.tap(tapX, 270, delayTime = 50)
                }
                TouchActions.tap(470, 455, delayTime = 200)
                TouchActions.tap(690, 390, delayTime = 200)
            }
        }
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
            accountMessage("建造失败，尝试寻找空位")
            val redCross = mainBaseFindBuildButton(type = BuildButtonType.Cross)
            if (redCross != null) {
                val centerX = redCross.x + 20
                val centerY = redCross.y + 45
                TouchActions.touchDown(centerX.toFloat(), centerY.toFloat(), 1)
                moveWithDelay(635F, 330F)
                zoomSmallMainBase(isForBuild = true)
                targetTick = tryToFindBuildPosition(BaseType.Main)
            }
        }

        if (targetTick != null) {
            accountMessage("放战旗成功")
            TouchActions.tap(targetTick.x, targetTick.y, delayTime = 300)
        }
    } else {
        accountMessage("未检测到放战旗按钮")
        clickRightBottom(1)
    }
}
