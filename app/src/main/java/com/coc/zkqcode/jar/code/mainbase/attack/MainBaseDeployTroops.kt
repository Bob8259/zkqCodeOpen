package com.coc.zkqcode.jar.code.mainbase.attack


import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseDeployTroops() {
    zoomSmallMainBase(isForAttack = true)
    val tacticsMode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.TACTICS_MODE.key).toInt()

}