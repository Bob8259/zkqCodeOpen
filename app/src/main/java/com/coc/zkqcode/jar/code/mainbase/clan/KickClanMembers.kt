package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

/**
 * 踢出部落成员功能。
 *
 * 流程：
 *   1. 检查是否启用、是否已加入部落
 *   2. 读取间隔分钟数，检查记忆文件是否已到时间
 *   3. 执行踢出流程：点击 → 点击 → 滑动 → 点击 → 检测踢出按钮 → 确认踢出
 *   4. 成功后记录时间戳到记忆文件
 *
 * 每次循环只踢一个成员，间隔时间由配置决定。
 */
suspend fun kickClanMembers(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS.key)) return true

    accountMessage("开始检查踢出部落成员")

    // 读取间隔分钟数
    val intervalMinutes = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS_INTERVAL.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS_INTERVAL.displayName} 必须是数字，请检查配置")
    // 游戏踢出后有20分钟冷却，强制最低间隔
    val actualInterval = intervalMinutes.coerceAtLeast(20)

    accountMessage("踢出间隔：${actualInterval}分钟")

    // 检查记忆文件，判断是否已经到时间
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.KICK_CLAN_MEMBERS,
        InGameVars.currentAccountNumber
    )
    if (!checkMemoryFile(storageKey, actualInterval)) {
        accountMessage("距离上次踢出还未到 ${actualInterval} 分钟，跳过踢出")
        return true
    }

    accountMessage("开始执行踢出流程")

    // ====== 流程：点击 → 点击 → 滑动 → 点击 → 检测踢出按钮 ======

    // 步骤1：复用领取成就宝石的入口进入
    TouchActions.tap(50, 45, delayTime = 2000)

    // 步骤2：点击成员标签
    TouchActions.tap(542, 59, delayTime = 1200)

    // 步骤3：滑动成员列表
    TouchActions.swipe(
        startX = 659,
        startY = 604,
        endX = 659,
        endY = -5000,
        delayTime = 500,
        delayAfter = 800
    )
    delayWithMultiplier(300)

    // 步骤4：点击成员项
    TouchActions.tap(595, 648, delayTime = 600)

    // 步骤5：检测踢出按钮图色
    val kickButton = findMultiColorsUntil(
        schemas = listOf(MyColors.ClanKickButton),
        duration = 2000
    )
    if (kickButton != null) {
        accountMessage("检测到踢出按钮图色，点击踢出")
        TouchActions.tap(kickButton.x, kickButton.y, delayTime = 600)
        delayWithMultiplier(500)

        // 检测确认踢出按钮
        val confirmButton = findMultiColorsUntil(
            schemas = listOf(MyColors.ClanKickConfirmButton),
            duration = 2000
        )
        if (confirmButton != null) {
            accountMessage("检测到确认按钮，点击确认踢出")
            TouchActions.tap(confirmButton.x, confirmButton.y, delayTime = 600)

            // 点击确认后自动返回主页面，记录踢出时间
            writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
            accountMessage("踢出完成，已记录时间")
        } else {
            accountMessage("未检测到确认按钮，跳过确认")
            if (!enterMainScreen()) return false
        }
    } else {
        accountMessage("未检测到踢出按钮，跳过本次踢出")
        if (!enterMainScreen()) return false
    }

    return true
}
