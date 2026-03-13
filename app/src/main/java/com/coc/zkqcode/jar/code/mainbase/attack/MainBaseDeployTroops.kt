package com.coc.zkqcode.jar.code.mainbase.attack


import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseDeployTroops() {
    zoomSmallMainBase(isForAttack = true)
    val tacticsMode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.TACTICS_MODE.key).toInt()
    val dragon = findMultiColors(schema = MyColors.DragonAtDeployBar)
    if (dragon != null) {
        dragToDeploy(dragon.x, dragon.y)
    }
}

private suspend fun dragToDeploy(x: Int, y: Int) {
    TouchActions.touchDown(600F, 51F, 1)
    delayWithMultiplier(900)
    
}