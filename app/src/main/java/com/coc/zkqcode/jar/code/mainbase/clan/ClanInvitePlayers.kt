package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

/**
 * 部落拉人功能（多轮次）
 *
 * 流程：
 *   0. 主页面检测是否已加入部落 → 未加入则跳过
 *   1. 读取配置轮次和每轮时长
 *   2. 每轮：
 *        a. 点击进入部落页面
 *        b. 检测语言按钮 → 检测是否成功进入
 *        c. 图色检测玩家项，循环邀请直到该轮时间到：
 *             - 检测到玩家项 → 点击 → 检测邀请按钮 → 邀请 → 返回列表
 *             - 检测不到 → 滑动列表
 *        d. 返回主页面，进入下一轮
 *   3. 所有轮次完成 → 结束
 */
suspend fun clanInvitePlayers(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS.key)) return true

    accountMessage("开始部落拉人")

    // ========== 步骤 0：主页面检测是否已加入部落 ==========
    val notInClan = findMultiColors(schema = MyColors.ClanInviteNotInClanFlag, increment = 1)
    if (notInClan != null) {
        accountMessage("检测到未加入部落，跳过拉人任务")
        return true
    }

    // ========== 步骤 1：读取配置 ==========
    val totalRounds = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUNDS.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUNDS.displayName} 必须是数字，请检查配置")
    val roundDurationMinutes = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUND_DURATION.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUND_DURATION.displayName} 必须是数字，请检查配置")

    accountMessage("拉人配置：${totalRounds}轮，每轮${roundDurationMinutes}分钟")

    // ========== 步骤 2：多轮循环 ==========
    for (round in 1..totalRounds) {
        val roundTag = "[第${round}轮] "
        accountMessage("——— 第 ${round}/${totalRounds} 轮 ———")

        // ---- 2a. 进入部落页面 ----
        TouchActions.tap(INVITE_TAP_COORDS[0].x, INVITE_TAP_COORDS[0].y, delayTime = 800)
        TouchActions.tap(INVITE_TAP_COORDS[1].x, INVITE_TAP_COORDS[1].y, delayTime = 1200)

        // ---- 2b. 检测语言确认按钮 ----
        val langButton = findMultiColorsUntil(
            schemas = listOf(MyColors.ClanInviteLanguageButton, MyColors.ClanInviteLanguageButtonAlt),
            duration = 1500
        )
        if (langButton != null) {
            accountMessage("${roundTag}检测到语言确认按钮，点击切换")
            TouchActions.tap(langButton.x, langButton.y, delayTime = 800)
        } else {
            accountMessage("${roundTag}未检测到语言按钮，继续下一步")
        }

        // ---- 检测是否成功进入页面 ----
        val enterSuccess = findMultiColorsUntil(
            schemas = listOf(MyColors.ClanInvitePageEntryIndicator, MyColors.ClanInvitePageEntryIndicatorAlt),
            duration = 3000
        )
        if (enterSuccess == null) {
            accountMessage("${roundTag}未检测到进入标志，跳过该轮")
            continue
        }
        accountMessage("${roundTag}已进入部落拉人页面")

        // 点击确认进入坐标
        TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[0].x, INVITE_ENTER_CONFIRM_COORDS[0].y, delayTime = 600)
        TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[1].x, INVITE_ENTER_CONFIRM_COORDS[1].y, delayTime = 800)

        // ---- 2c. 单轮时长循环 ----
        val roundStartTime = System.currentTimeMillis()
        val roundDurationMillis = roundDurationMinutes * 60_000L
        var lastTimeLogTime = 0L

        while (System.currentTimeMillis() - roundStartTime < roundDurationMillis) {
            val elapsed = System.currentTimeMillis() - roundStartTime
            val remainingSec = (roundDurationMillis - elapsed) / 1000

            // 每 30 秒显示一次剩余时间
            if (elapsed - lastTimeLogTime >= 30_000) {
                lastTimeLogTime = elapsed
                accountMessage("${roundTag}本轮剩余 ${remainingSec / 60} 分 ${remainingSec % 60} 秒")
            }

            // 图色检测玩家项（根据版本自动选择检测区域）
            val playerItemSchema = if (InGameVars.currentGameVersion == GameVersion.CN) {
                ColorSchema.rescope(MyColors.ClanInvitePlayerItemIndicator, 932, 513, 1000, 688)
            } else {
                MyColors.ClanInvitePlayerItemIndicator
            }
            val playerItem = findMultiColorsUntil(
                schemas = listOf(playerItemSchema),
                duration = 1000
            )

            if (playerItem != null) {
                accountMessage("${roundTag}检测到可邀请玩家，点击进入")

                // 点击玩家列表项
                val itemTap = INVITE_ITEM_TAP_COORDS[0]
                TouchActions.tap(itemTap.x, itemTap.y, delayTime = 500)

                // 检测更多信息按钮图色
                val moreInfoButton = findMultiColorsUntil(
                    schemas = listOf(MyColors.ClanInviteActionButton, MyColors.ClanInviteActionButtonAlt),
                    duration = 2000
                )
                if (moreInfoButton == null) {
                    accountMessage("${roundTag}未检测到更多信息按钮，跳过该成员")
                    swipeList()
                    continue
                }

                accountMessage("${roundTag}检测到更多信息按钮，点击")
                TouchActions.tap(moreInfoButton.x, moreInfoButton.y, delayTime = 600)

                // 检测邀请确认按钮图色
                val confirmButton = findMultiColorsUntil(
                    schemas = listOf(MyColors.ClanInviteConfirmButton, MyColors.ClanInviteConfirmButtonAlt),
                    duration = 2000
                )
                if (confirmButton != null) {
                    accountMessage("${roundTag}检测到邀请确认按钮，点击邀请")
                    TouchActions.tap(confirmButton.x, confirmButton.y, delayTime = 600)
                }

                // 邀请后返回成员列表
                val backIndicator = findMultiColorsUntil(
                    schemas = listOf(MyColors.ClanInviteBackToListIndicator),
                    duration = 2000
                )
                if (backIndicator != null) {
                    accountMessage("${roundTag}检测到返回列表标志，点击返回")
                    TouchActions.tap(backIndicator.x, backIndicator.y, delayTime = 800)
                } else {
                    accountMessage("${roundTag}未检测到返回标志，尝试点击默认返回位置")
                    TouchActions.tap(176, 56, delayTime = 800)
                }
            } else {
                accountMessage("${roundTag}未检测到可邀请玩家项，滑动列表（剩余${remainingSec}秒）")
            }

            // 滑动列表
            swipeList()
        }

        // ---- 2d. 该轮结束，返回主页面 ----
        accountMessage("${roundTag}本轮结束（已运行${roundDurationMinutes}分钟），返回主页面")
        if (!enterMainScreen()) return false
    }

    accountMessage("部落拉人全部完成（共${totalRounds}轮）")
    return true
}

/**
 * 向上滑动玩家列表
 */
private suspend fun swipeList() {
    TouchActions.swipe(
        startX = SWIPE_COORDS.startX,
        startY = SWIPE_COORDS.startY,
        endX = SWIPE_COORDS.endX,
        endY = SWIPE_COORDS.endY,
        delayTime = 500,
        delayAfter = 800
    )
    delay(300)
}

// ============================================================
// 配置区
// ============================================================

private data class TapCoord(val x: Int, val y: Int)

// 进入部落页面的两个点击坐标
private val INVITE_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(54, 46),
    TapCoord(753, 70)
)

// 成功进入页面后的两个确认点击坐标
private val INVITE_ENTER_CONFIRM_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(375, 386),
    TapCoord(536, 582)
)

// 玩家列表项点击坐标
private val INVITE_ITEM_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(388, 651)
)

// 滑动参数：向上滑动，从下方滑到上方
private data class SwipeCoords(val startX: Int, val startY: Int, val endX: Int, val endY: Int)
private val SWIPE_COORDS: SwipeCoords = SwipeCoords(
    startX = 460,
    startY = 685,
    endX = 460,
    endY = 607
)
