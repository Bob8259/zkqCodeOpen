package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BuildButtonType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.tryToFindBuildPosition
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions

suspend fun checkItemsToBePlaced(): Boolean {
    val items = findMultiColors(MyColors.ItemsToBePlaced)
    if (items != null) {
        accountMessage("准备放置物品")
        zoomSmallMainBase(isForBuild = true)
        TouchActions.tap(620, 560)
        // Check for green tick (valid placement position)
        var greenTick = mainBaseFindBuildButton(type = BuildButtonType.Tick)

        if (greenTick == null) {
            // No valid position at current location, try to find one
            accountMessage("放置失败，尝试寻找空位")
            greenTick = tryToFindBuildPosition(BaseType.Main)
        }

        if (greenTick != null) {
            accountMessage("物品放置成功")
            TouchActions.tap(greenTick.x, greenTick.y, delayTime = 700)
            TouchActions.tap(640, 550, delayTime = 700)//store
            TouchActions.tap(770, 450)//confirm
        } else {
            accountMessage("未找到可放置位置")
        }

        return enterMainScreen()
    }
    return true
}
