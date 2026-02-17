package com.coc.zkqcode.jar.code.builderbase.attack

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun builderBaseAttack(): Boolean {
    val goldPosition = findMultiColors(schema = MyColors.BuilderBaseGold)
    val exilePosition = findMultiColors(schema = MyColors.BuilderBaseExiler)
    val isResourcesFull = goldPosition != null && exilePosition != null && goldPosition.x < 1015 && exilePosition.x < 1015
    if (isResourcesFull && getConfigRuntime(Schema.BUILDER_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key) == "1") {
        ShowMessage("资源已满，停止对战")
    } else {
        if (goldPosition != null && goldPosition.x < 1015) {
            realAttack("gold")
        } else {
            realAttack("exile")
        }
    }
    return true
}

private fun realAttack(mode: String) {

}
