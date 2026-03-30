package com.coc.zkqcode.jar.code.mainbase.donate

import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun donateToClan(): Boolean {
    val notJoinClan = findMultiColors(schema = MyColors.NotJoinClanFlag)
    if (notJoinClan != null) return true
    val isDonateEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_SETTING.key)
    if (!isDonateEnabled) return true
    val donationTimeInterval = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.displayName} 必须是数字，请检查配置")
    val lowerThreshold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.displayName} 必须是数字，请检查配置")
    val higherThreshold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.displayName} 必须是数字，请检查配置")

    val clanChat = findMultiColors(schema = MyColors.ClanChatIcon)
    if (clanChat != null) {

    }

    return enterMainScreen()
}