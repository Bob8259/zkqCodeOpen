package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
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
 *        b. 检测语言按钮 → 检测是否成功进入 → 等待刷新按钮亮起（最长80秒）
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
    val roundDurationSeconds = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUND_DURATION.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUND_DURATION.displayName} 必须是数字，请检查配置")

    accountMessage("拉人配置：${totalRounds}轮，每轮${roundDurationSeconds}秒")

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

        // ---- 滑动查找刷新按钮（最长等待80秒） ----
        accountMessage("${roundTag}即将进入邀请页面，滑动查找刷新按钮")
        TouchActions.swipe(
            startX = REFRESH_SWIPE_COORDS.startX,
            startY = REFRESH_SWIPE_COORDS.startY,
            endX = REFRESH_SWIPE_COORDS.endX,
            endY = REFRESH_SWIPE_COORDS.endY,
            delayTime = 500,
            delayAfter = 800
        )
        delayWithMultiplier(300)
        accountMessage("${roundTag}等待刷新按钮亮起（最长80秒）")
        val refreshStartTime = System.currentTimeMillis()
        val refreshTimeoutMs = 80_000L
        var refreshLastLogTime = 0L
        var refreshButton: Point? = null
        while (System.currentTimeMillis() - refreshStartTime < refreshTimeoutMs && refreshButton == null) {
            val elapsed = System.currentTimeMillis() - refreshStartTime
            val remainingSec = (refreshTimeoutMs - elapsed) / 1000

            // Log countdown every 10 seconds
            if (elapsed - refreshLastLogTime >= 10_000) {
                refreshLastLogTime = elapsed
                accountMessage("${roundTag}等待刷新按钮亮起，剩余 ${remainingSec} 秒")
            }

            refreshButton = findMultiColors(
                schema = MyColors.ClanInviteRefreshButton,
                increment = 1
            )
            if (refreshButton == null) {
                refreshButton = findMultiColors(
                    schema = MyColors.ClanInviteRefreshButtonAlt,
                    increment = 1
                )
            }
            if (refreshButton == null) {
                delay(50)
            }
        }
        if (refreshButton != null) {
            accountMessage("${roundTag}检测到刷新按钮，点击刷新")
            TouchActions.tap(refreshButton.x, refreshButton.y, delayTime = 600)

            // Swipe down to return to top of list after refresh
            accountMessage("${roundTag}刷新完成，向下滑动返回置顶")
            TouchActions.swipe(
                startX = BACK_TO_TOP_SWIPE_COORDS.startX,
                startY = BACK_TO_TOP_SWIPE_COORDS.startY,
                endX = BACK_TO_TOP_SWIPE_COORDS.endX,
                endY = BACK_TO_TOP_SWIPE_COORDS.endY,
                delayTime = 500,
                delayAfter = 800
            )
            delayWithMultiplier(300)

            // Tap confirm-enter coordinates after refresh succeeds
            TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[0].x, INVITE_ENTER_CONFIRM_COORDS[0].y, delayTime = 600)
            TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[1].x, INVITE_ENTER_CONFIRM_COORDS[1].y, delayTime = 800)

            // ---- 2c. 单轮时长循环 ----
            val roundStartTime = System.currentTimeMillis()
            val roundDurationMillis = roundDurationSeconds * 1000L
            var lastTimeLogTime = 0L

            while (System.currentTimeMillis() - roundStartTime < roundDurationMillis) {
                val elapsed = System.currentTimeMillis() - roundStartTime
                val remainingSec = (roundDurationMillis - elapsed) / 1000

                // Log remaining time every 30 seconds
                if (elapsed - lastTimeLogTime >= 30_000) {
                    lastTimeLogTime = elapsed
                    accountMessage("${roundTag}本轮剩余 ${remainingSec} 秒（共${roundDurationSeconds}秒）")
                }

                // Detect player items (region varies by game version)
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
                    accountMessage("${roundTag}检测到可邀请玩家，点击进入（剩余${remainingSec}秒）")

                    val itemTap = INVITE_ITEM_TAP_COORDS[0]
                    TouchActions.tap(itemTap.x, itemTap.y, delayTime = 500)

                    val moreInfoButton = findMultiColorsUntil(
                        schemas = listOf(MyColors.ClanInviteActionButton, MyColors.ClanInviteActionButtonAlt),
                        duration = 2000
                    )
                    if (moreInfoButton == null) {
                        accountMessage("${roundTag}未检测到更多信息按钮，跳过该成员")
                        swipeList()
                        continue
                    }

                    accountMessage("${roundTag}检测到更多信息按钮，点击（剩余${remainingSec}秒）")
                    TouchActions.tap(moreInfoButton.x, moreInfoButton.y, delayTime = 600)

                    val confirmButton = findMultiColorsUntil(
                        schemas = listOf(MyColors.ClanInviteConfirmButton, MyColors.ClanInviteConfirmButtonAlt),
                        duration = 2000
                    )
                    if (confirmButton != null) {
                        accountMessage("${roundTag}检测到邀请确认按钮，点击邀请（剩余${remainingSec}秒）")
                        TouchActions.tap(confirmButton.x, confirmButton.y, delayTime = 600)
                    }

                    val backIndicator = findMultiColorsUntil(
                        schemas = listOf(MyColors.ClanInviteBackToListIndicator),
                        duration = 2000
                    )
                    if (backIndicator != null) {
                        accountMessage("${roundTag}检测到返回列表标志，点击返回（剩余${remainingSec}秒）")
                        TouchActions.tap(backIndicator.x, backIndicator.y, delayTime = 800)
                    } else {
                        accountMessage("${roundTag}未检测到返回标志，尝试点击默认返回位置（剩余${remainingSec}秒）")
                        TouchActions.tap(176, 56, delayTime = 800)
                    }
                } else {
                    accountMessage("${roundTag}未检测到可邀请玩家项，滑动列表（剩余${remainingSec}秒）")
                }

                swipeList()
            }

            // ---- 2d. Round complete, return to main screen ----
            accountMessage("${roundTag}本轮邀请完成，返回主页面")
            if (!enterMainScreen()) return false
        } else {
            accountMessage("${roundTag}80秒超时，刷新按钮未亮起，跳过本轮拉人")
            if (!enterMainScreen()) return false
        }
    }

    accountMessage("部落拉人全部完成（共${totalRounds}轮）")
    return true
}

/**
 * Swipe up on the player list.
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
    delayWithMultiplier(300)
}

// ============================================================
// Configuration
// ============================================================

private data class TapCoord(val x: Int, val y: Int)

private val INVITE_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(54, 46),
    TapCoord(753, 70)
)

private val INVITE_ENTER_CONFIRM_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(375, 386),
    TapCoord(536, 582)
)

private val INVITE_ITEM_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(388, 651)
)

private data class SwipeCoords(val startX: Int, val startY: Int, val endX: Int, val endY: Int)

private val SWIPE_COORDS: SwipeCoords = SwipeCoords(
    startX = 460,
    startY = 685,
    endX = 460,
    endY = 607
)

// Large upward swipe to reveal the refresh button
private val REFRESH_SWIPE_COORDS: SwipeCoords = SwipeCoords(
    startX = 644,
    startY = 671,
    endX = 644,
    endY = -500
)

// Downward swipe to return to top of list after refresh
private val BACK_TO_TOP_SWIPE_COORDS: SwipeCoords = SwipeCoords(
    startX = 644,
    startY = 200,
    endX = 644,
    endY = 1500
)
