package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.auth.displayAds
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.walls.calculateResourcesPercentage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun donateToClan(): Boolean {
    val notJoinClan = findMultiColors(schema = MyColors.NotJoinClanFlag, increment = 1)
    if (notJoinClan != null) return true
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_SETTING.key) || InGameVars.townHallLevel < 3) return true
    val donationTimeInterval =
        getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.displayName} 必须是数字，请检查配置")
    // Clamp lowerThreshold to a minimum of 25
    val lowerThreshold = (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.displayName} 必须是数字，请检查配置")).coerceAtLeast(25)
    // Clamp higherThreshold to a maximum of 95
    val higherThreshold = (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key).toIntOrNull()
        ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.displayName} 必须是数字，请检查配置")).coerceAtMost(95)

    val startTime = System.currentTimeMillis()
    var intervalMillis = donationTimeInterval * 1000L
    var lastAdTime = 0L
    // Track farming mode state across donation loop iterations
    var inFarmingMode = false
    var lastBattleTime = 0L
    var loopCount = 0
    while (System.currentTimeMillis() - startTime < intervalMillis) {
        loopCount++
        if (System.currentTimeMillis() - lastAdTime >= 1500L) {
            displayAds()
            lastAdTime = System.currentTimeMillis()
        }
        // Capture a single frame buffer and reuse it for all colour checks in this iteration
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in donateToClan, screen capture failed.")

        val clanChat = findMultiColors(schema = MyColors.ClanChatIcon, byteBuffer = screenBuffer, increment = 1)
        if (clanChat != null) {
            // Tap the clan chat icon to open the chat panel
            TouchActions.tap(clanChat.x, clanChat.y)
            if (InGameVars.currentGameVersion == GameVersion.CN) {
                TouchActions.tap(523, 239, delayTime = 200)
                TouchActions.tap(152, 88, delayTime = 200)
            } else {
                TouchActions.tap(523, 98, delayTime = 300)//Global version chat area
            }
            TouchActions.tap(40, 608)//Goto bottom
            continue
        }
        val iUnderstandButton = findMultiColors(schema = MyColors.IUnderstand, byteBuffer = screenBuffer, increment = 1)
        if (iUnderstandButton != null) {
            TouchActions.tap(iUnderstandButton.x, iUnderstandButton.y)
            continue
        }
        // Approve member detection: check for join requests with matching secret code
        val approveEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.APPROVE_MEMBER_SETTING.key)
        if (approveEnabled) {
            val approveCode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.APPROVE_MEMBER_CODE.key)
            if (approveCode.isNotBlank()) {
                val approveBaseSchema = MyColors.ApproveJoinButton
                val approveSearchStartY = 70
                val approveSearchEndY = 630
                val approveSliceHeight = 60
                val approveBtn = run {
                    var found: android.graphics.Point? = null
                    outer@ for (sliceStartY in approveSearchStartY..approveSearchEndY step approveSliceHeight) {
                        val sliceEndY = (sliceStartY + approveSliceHeight).coerceAtMost(approveSearchEndY)
                        var currentY1 = sliceStartY
                        while (currentY1 < sliceEndY) {
                            val sliceSchema = ColorSchema.rescope(
                                approveBaseSchema, approveBaseSchema.x1, currentY1, approveBaseSchema.x2, sliceEndY
                            )
                            val candidate = findMultiColors(schema = sliceSchema, byteBuffer = screenBuffer, increment = 1) ?: break
                            found = candidate
                            break@outer
                        }
                    }
                    found
                }
                if (approveBtn != null) {
                    var ocrY = approveBtn.y
                    if (approveBtn.x in 15..475 && approveBtn.y in 7..138) {
                        TouchActions.swipe(approveBtn.x, approveBtn.y, approveBtn.x, approveBtn.y + 273, delayTime = 200)
                        ocrY = approveBtn.y + 273
                    }
                    val ocrResult = TextRecognizer.recognize(
                        startX = (approveBtn.x - 320).coerceAtLeast(20),
                        startY = (ocrY - 108).coerceAtLeast(10),
                        endX = (approveBtn.x + 100).coerceAtMost(480),
                        endY = (ocrY - 48).coerceAtMost(620),
                        useChinese = true,
                        threshold = 140,
                        applyPreprocess = true,
                        invertBinarization = true
                    )
                    val ocrText = ocrResult.joinToString(" ") { it.text }
                    showDebugInfo("批准检测 OCR结果: $ocrText")
                    if (ocrText.contains(approveCode, ignoreCase = true)) {
                        accountMessage("批准成员加入: 暗号匹配($approveCode), 点击批准")
                        TouchActions.tap(approveBtn.x, approveBtn.y)
                        continue
                    } else {
                        accountMessage("批准成员加入: 暗号不匹配, 跳过")
                    }
                }
            }
        }
        // Grid-search donation buttons by scanning vertical slices.
        // This avoids missing real donation requests when the first detected button belongs to a troop-config row
        // (which is identified by a castle icon to the left of the button).
        val donationBaseSchema = MyColors.DonationButton
        val donationSearchStartY = 70
        val donationSearchEndY = 630
        val donationSliceHeight = 60
        val donationButton = run {
            var found: android.graphics.Point? = null
            outer@ for (sliceStartY in donationSearchStartY..donationSearchEndY step donationSliceHeight) {
                val sliceEndY = (sliceStartY + donationSliceHeight).coerceAtMost(donationSearchEndY)
                var currentY1 = sliceStartY
                while (currentY1 < sliceEndY) {
                    val sliceSchema = ColorSchema.rescope(
                        donationBaseSchema, donationBaseSchema.x1, currentY1, donationBaseSchema.x2, sliceEndY
                    )
                    val candidate = findMultiColors(schema = sliceSchema, byteBuffer = screenBuffer, increment = 1) ?: break

                    // Check if a troop config castle icon is present to the left of the donation button.
                    // Its presence means this is a troop configuration row (not a real donation request) — skip it.
                    val castleIconSchema = ColorSchema.rescope(
                        MyColors.TroopConfigCastleIcon,
                        x1 = candidate.x - 200,
                        y1 = candidate.y - 30,
                        x2 = candidate.x - 90,
                        y2 = candidate.y + 40
                    )
                    val castleIcon = findMultiColors(schema = castleIconSchema, byteBuffer = screenBuffer, increment = 1)
                    if (castleIcon == null) {
                        // Check if this green button is actually an approve button (not a donation button)
                        val approveCheckSchema = ColorSchema.rescope(
                            MyColors.ApproveJoinButton,
                            x1 = candidate.x - 220,
                            y1 = candidate.y - 30,
                            x2 = candidate.x + 10,
                            y2 = candidate.y + 10
                        )
                        val isApproveBtn = findMultiColors(schema = approveCheckSchema, byteBuffer = screenBuffer, increment = 1)
                        if (isApproveBtn == null) {
                            found = candidate
                            break@outer
                        }
                    }

                    // Continue searching within the current slice after this candidate.
                    currentY1 = (candidate.y + 1).coerceAtLeast(currentY1 + 1).coerceAtMost(sliceEndY)
                }
            }
            found
        }

        if (donationButton != null) {
            TouchActions.tap(donationButton.x, donationButton.y)
            donateActions()
            TouchActions.swipe(1155, 237, -500, 250, delayTime = 100)//Swipe troops
            TouchActions.swipe(1155, 480, -500, 480, delayTime = 100)//Spells
            donateActions()
            clickRightBottom(1)
            // If remaining detection time is less than 3 seconds, extend it to 3 seconds
            val remaining = intervalMillis - (System.currentTimeMillis() - startTime)
            if (remaining < 3000L) {
                intervalMillis += (3000L - remaining)
            }
            continue
        } else {
            val previousDonation = findMultiColors(MyColors.PreviousDonation, byteBuffer = screenBuffer, increment = 1)
            if (previousDonation != null) {
                TouchActions.tap(previousDonation.x, previousDonation.y)
                continue
            }
        }
        val resources = calculateResourcesPercentage(BaseType.Main)
        // Default to 0; only updated when the dark elixir icon is visible on screen
        var darkElixirPercentage = 0
        val darkElixirIcon = findMultiColors(schema = MyColors.DarkElixirIcon, increment = 1)
        if (darkElixirIcon != null) {
            // darkElixirBar is the rightmost coloured point on the dark elixir bar
            val darkElixirBar = findMultiColors(schema = MyColors.DarkElixirColor)
            // x = 1260 → 0%, x = 1079 → 100%; bar shrinks leftward as storage fills, so invert the direction
            darkElixirPercentage = if (darkElixirBar != null) {
                ((1260 - darkElixirBar.x) * 100 / (1260 - 1079)).coerceIn(0, 100)
            } else {
                0
            }
        }
        // Show remaining detection time in seconds
        val remainingSeconds = (intervalMillis - (System.currentTimeMillis() - startTime)) / 1000
        // Display dark elixir as a percentage when unlocked, or as 未解锁 when the icon is absent
        val darkElixirDisplay = if (darkElixirIcon != null) "暗黑重油：${darkElixirPercentage}%" else "暗黑重油：未解锁"
        accountMessage("捐兵检测中，剩余${remainingSeconds}秒结束\n当前资源百分比：\n圣水：${resources.elixir}%\n${darkElixirDisplay}")

        // Check whether resources have recovered above the higher threshold
        val isDarkElixirUnlocked = darkElixirIcon != null
        val bothRecovered = resources.elixir >= higherThreshold && (!isDarkElixirUnlocked || darkElixirPercentage >= higherThreshold)
        if (bothRecovered) {
            inFarmingMode = false
        }
        // Check whether either resource is below the lower threshold
        val isResourceLow = resources.elixir < lowerThreshold || (isDarkElixirUnlocked && darkElixirPercentage < lowerThreshold)
        if (isResourceLow || inFarmingMode) {
            inFarmingMode = true
            // Fire a battle only if 3 minutes have elapsed since the last one;
            // the donation loop keeps running normally between battles
            val timeSinceLastBattle = System.currentTimeMillis() - lastBattleTime
            if (timeSinceLastBattle >= 3 * 60 * 1000L) {
                accountMessage("资源不足，开始刷资源\n圣水：${resources.elixir}%，暗黑重油：${if (isDarkElixirUnlocked) "$darkElixirPercentage%" else "未解锁"}")
                if (!enterMainScreen()) return false
                if (!mainBaseAttack()) return false
                lastBattleTime = System.currentTimeMillis()
            }
        }
        if (loopCount % 20 == 0) {
            if (!checkReconnections()) return false
        }
        if (loopCount % 100 == 0) {
            if (!enterMainScreen()) return false
        }
        delayWithMultiplier(500)
    }

    return enterMainScreen()
}

private suspend fun donateActions() {
    // Iterate through all donation types and tap each if found
    val donationSchemas = listOf(
        MyColors.DonateSuperTroops, MyColors.DonateSuperTroops2, MyColors.DonateNormalTroops, MyColors.DonateSpells
    )
    var emptyLoopCount = 0
    repeat(10) {
        // Early return if nothing was found in 2 consecutive loops
        if (emptyLoopCount >= 2) return

        var foundAny = false
        for (schema in donationSchemas) {
            val target = findMultiColors(schema, increment = 1)
            if (target != null) {
                foundAny = true
                repeat(3) {
                    // Global version uses a random delay to mimic human input; CN version uses default delay
                    if (InGameVars.currentGameVersion == GameVersion.GLOBAL) {
                        TouchActions.tap(target.x, target.y, delayTime = (150..300).random())
                    } else {
                        TouchActions.tap(target.x, target.y, delayTime = 20)
                    }
                }
            }
        }
        if (foundAny) emptyLoopCount = 0 else emptyLoopCount++
    }
}