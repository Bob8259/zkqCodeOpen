package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
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
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

private const val STRATEGY_POSITION = "position"
private const val STRATEGY_RECOMMENDATION = "recommendation"

suspend fun clanWar(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key)) return true
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

    when (currentStrategy) {
        STRATEGY_POSITION -> attackByPosition()
        STRATEGY_RECOMMENDATION -> attackByRecommendation()
    }

    writeMemory(storageKey, currentStrategy)
    
    return enterMainScreen()
}

/**
 * Navigate to the opponent matching my position and attempt to attack.
 * Returns true if the attack was executed.
 */
private suspend fun attackByPosition(): Boolean {
    val myPosition = detectMyClanWarPosition()
    TouchActions.swipe(640, 300, 640, 10000, delayAfter = 500)
    TouchActions.tap(460, 300) // Tap the first opponent
    repeat(myPosition - 1) {
        TouchActions.tap(950, 570, delayTime = 50)
    }
    val attackButton = findMultiColors(MyColors.ClanWarAttack) ?: return false
    ShowMessage("已找到目标，尝试进入对战")
    return executeClanWarAttack(attackButton.x, attackButton.y)
}

/**
 * Find the recommended opponent and attempt to attack.
 * Returns true if the attack was executed.
 */
private suspend fun attackByRecommendation(): Boolean {
    val recommended = findMultiColorsUntil(
        schemas = listOf(MyColors.RecommendedOpponent), duration = 1000
    ) ?: return false
    ShowMessage("已找到推荐对手，准备尝试进攻")
    TouchActions.tap(recommended.x, recommended.y - 100)
    val attackButton = findMultiColors(MyColors.ClanWarAttack) ?: return false
    return executeClanWarAttack(attackButton.x, attackButton.y)
}

/**
 * Shared attack execution: tap attack button, confirm, deploy troops, and kill game.
 */
private suspend fun executeClanWarAttack(attackX: Int, attackY: Int): Boolean {
    TouchActions.tap(attackX, attackY)
    TouchActions.tap(1080, 640) // Attack
    TouchActions.tap(780, 440) // Confirm attack
    TouchActions.tap(1080, 640) // Attack
    TouchActions.tap(755, 470)//Double confirm
    ShowMessage("检测是否进入对战中")
    if (findMultiColorsUntil(schemas = listOf(MyColors.EndBattle), duration = 5000) == null) {
        ShowMessage("进入部落战失败")
        return false
    }
    mainBaseDeployTroops()
    killGame()
    return true
}

suspend fun detectMyClanWarPosition(): Int {
    val clanWarInfo = findMultiColorsUntil(schemas = listOf(MyColors.ClanWarInfo), duration = 1000)
    if (clanWarInfo != null) {
        TouchActions.tap(clanWarInfo.x, clanWarInfo.y)
        TouchActions.tap(755, 60)
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
                                Bitmap.createBitmap(screenBuffer, startX, startY, width, height),
                                threshold = 250,
                                invertBinarization = true
                            )
                            if (AiDetector.loadWeights("clan-war-numbers")) {
                                try {
                                    val detections = AiDetector.detect(
                                        bitmap = cropBitmap,
                                        clearWeightsAfter = false,
                                        threshold = 0.3f
                                    )
                                    if (detections.isNotEmpty()) {
                                        val number = detections.sortedBy { it.boundingBox.centerX() }
                                            .joinToString("") { it.classIndex.toString() }
                                            .toIntOrNull()
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
    } else {
        accountMessage("未检测到部落战信息")
    }

    return 0
}

