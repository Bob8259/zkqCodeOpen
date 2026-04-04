package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeGears(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WEARED_GEAR.key)) return true
    ShowMessage("准备升级装备")
    return withHeroHall { upgradeGearsAction() }
}

private suspend fun upgradeGearsAction() {

}