package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions

suspend fun checkItemsToBePlaced(): Boolean {
    val items = findMultiColors(MyColors.ItemsToBePlaced)
    if (items != null) {
        accountMessage("准备放置物品")
        zoomSmallMainBase(isForBuild = true)
        TouchActions.tap(620, 560)
        //TODO: Apply the building logic here.
        // Check for main base green tick, if not found, then use the function that can find build position to find the position.
    }
}
