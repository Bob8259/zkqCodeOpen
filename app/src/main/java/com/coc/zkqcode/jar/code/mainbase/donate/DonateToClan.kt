package com.coc.zkqcode.jar.code.mainbase.donate

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun donateToClan(): Boolean {
    val notJoinClan = findMultiColors(schema = MyColors.NotJoinClanFlag)
    if (notJoinClan != null) return true
    
    return enterMainScreen()
}