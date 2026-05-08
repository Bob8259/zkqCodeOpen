package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions

suspend fun checkItemsToBePlaced(): Boolean {
    val items = findMultiColors(MyColors.ItemsToBePlaced)
    if (items != null) {
        zoomSmallMainBase(isForBuild = true)
        TouchActions.tap()
    }
}