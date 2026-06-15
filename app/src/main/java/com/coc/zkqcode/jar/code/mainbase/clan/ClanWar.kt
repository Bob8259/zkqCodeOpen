package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseDeployTroops
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.aidetection.AiDetector
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

private const val STRATEGY_POSITION = "position"
private const val STRATEGY_RECOMMENDATION = "recommendation"

suspend fun clanWar(): Boolean {
    // Only perform league-vs-clan-war decision when BOTH the league AND clan war switches are enabled.
    val leagueEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key)
        || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LEAGUE.key)
        || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LEAGUE_DONATION.key)

    if (leagueEnabled && isInClanLeaguePeriod()) {
        if (findMultiColors(MyColors.ClanLeagueEntryIndicator) != null) {
            accountMessage("当前处于部落联赛期间且检测到联赛入口，跳过部落战")
            return true
        }
        accountMessage("当前处于部落联赛时间段但未检测到联赛入口标志，继续执行部落战")
    }
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key) || InGameVars.townHallLevel < 3) return true
    findMultiColors(MyColors.ClanWarSwordIcon) ?: return true // No clan war attack times, return
    TouchActions.tap(50, 440) // Enter clan war

    // Determine strategy order based on last used strategy
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_WAR_LAST_STRATEGY, InGameVars.currentAccountNumber
    )
    val lastStrategy = readMemory(storageKey)
    val currentStrategy = if (lastStrategy == STRATEGY_POSITION) {
        STRATEGY_RECOMMENDATION
    } else {
        STRATEGY_POSITION
    }
    if (findMultiColorsUntil(schemas = listOf(MyColors.ClanWarInfo), duration = 1000) == null) {
        accountMessage("未检测到部落竞赛，当前可能是联赛")
        return if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LEAGUE.key)) {
            true
        } else {
            enterMainScreen()
        }
    } else {
        when (currentStrategy) {
            STRATEGY_POSITION -> attackByPosition()
            STRATEGY_RECOMMENDATION -> attackByRecommendation()
        }
        writeMemory(storageKey, currentStrategy)
        return enterMainScreen()
    }

}

/**
 * Navigate to the opponent matching my position and attempt to attack.
 */
private suspend fun attackByPosition() {
    val clanWarInfo = findMultiColorsUntil(schemas = listOf(MyColors.ClanWarInfo), duration = 1000)
    if (clanWarInfo != null) {
        TouchActions.tap(clanWarInfo.x, clanWarInfo.y)
        TouchActions.tap(755, 60)

        val myPosition = detectMyClanWarPosition()
        TouchActions.swipe(640, 300, 640, 10000, delayAfter = 500)
        TouchActions.tap(460, 300) // Tap the first opponent
        repeat(myPosition - 1) {
            TouchActions.tap(950, 570, delayTime = 120)
        }
        val attackButton = findMultiColorsUntil(listOf(MyColors.ClanWarAttack, MyColors.ClanWarAttack2), duration = 1000) ?: return
        accountMessage("已找到目标，尝试进入对战")
        executeClanWarAttack(attackButton.x, attackButton.y)
    } else {
        accountMessage("未找到部落战信息")
    }
}

/**
 * Find the recommended opponent and attempt to attack.
 */
private suspend fun attackByRecommendation() {
    val recommended = findMultiColorsUntil(schemas = listOf(MyColors.RecommendedOpponent), duration = 3000) ?: run {
        accountMessage("未找到推荐对手")
        return
    }
    accountMessage("已找到推荐对手，准备尝试进攻")
    TouchActions.tap(recommended.x, recommended.y - 100)
    val attackButton = findMultiColorsUntil(listOf(MyColors.ClanWarAttack, MyColors.ClanWarAttack2), duration = 1000) ?: return
    executeClanWarAttack(attackButton.x, attackButton.y)
}

/**
 * Shared attack execution: tap attack button, confirm, deploy troops, and kill game.
 */
suspend fun executeClanWarAttack(attackX: Int, attackY: Int) {
    TouchActions.tap(attackX, attackY)
    TouchActions.tap(1080, 640) // Attack
    TouchActions.tap(780, 440) // Confirm attack
    TouchActions.tap(1080, 640) // Attack
    TouchActions.tap(755, 470)//Double confirm
    accountMessage("检测是否进入对战中")
    if (findMultiColorsUntil(schemas = listOf(MyColors.EndBattle), duration = 5000) == null) {
        accountMessage("进入部落战失败")
        return
    }
    mainBaseDeployTroops()
    killGame()
}

suspend fun detectMyClanWarPosition(): Int {
    val ourTeam = findMultiColorsUntil(schemas = listOf(MyColors.OurTeam), duration = 1000)
    if (ourTeam != null) {
        delayWithMultiplier(300)
        for (i in 0 until 13) {
            val position = findMultiColorsUntil(schemas = listOf(MyColors.MyPositionGreen), duration = 500)
            if (position != null) {
                // Calculate crop area using position offsets
                val startX = position.x - 5
                val startY = position.y + 5
                val endX = position.x + 55
                val endY = position.y + 50

                // Capture screen and crop to the number region, then use YOLO to detect digits
                val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                if (screenBuffer != null) {
                    val width = endX - startX
                    val height = endY - startY
                    if (startX >= 0 && startY >= 0 && endX <= screenBuffer.width && endY <= screenBuffer.height) {
                        val cropBitmap = TextRecognizer.preprocess(
                            Bitmap.createBitmap(screenBuffer, startX, startY, width, height), threshold = 250, invertBinarization = true
                        )
                        if (AiDetector.loadWeights("clan-war-numbers")) {
                            try {
                                val detections = AiDetector.detect(
                                    bitmap = cropBitmap, clearWeightsAfter = false, threshold = 0.3f
                                )
                                if (detections.isNotEmpty()) {
                                    val number = detections.sortedBy { it.boundingBox.centerX() }.joinToString("") { it.classIndex.toString() }.toIntOrNull()
                                    accountMessage("检测到部落战位置编号: $number")
                                    if (number != null) {
                                        TouchActions.tap(1120, 55) // Close info page
                                        return number
                                    }
                                } else {
                                    accountMessage("未检测到位置编号 (${position.x}, ${position.y})")
                                }
                            } finally {
                                AiDetector.clearWeights()
                            }
                        }
                    }
                }
                TouchActions.tap(1120, 55) // Close info page
                break
            }
            TouchActions.swipe(320, 570, 320, 210)
        }
    }
    return 0
}
