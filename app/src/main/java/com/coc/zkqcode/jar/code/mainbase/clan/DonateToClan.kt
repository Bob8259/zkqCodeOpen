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
        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)

        val clanChat = findMultiColors(schema = MyColors.ClanChatIcon, byteBuffer = screenBuffer, increment = 1)
        if (clanChat != null) {
            // Tap the clan chat icon to open the chat panel
            TouchActions.tap(clanChat.x, clanChat.y)
            if (InGameVars.currentGameVersion == GameVersion.CN) {
                TouchActions.tap(523, 239, delayTime = 200)
                TouchActions.tap(152, 88, delayTime = 200)
            } else {
                TouchActions.tap(750, 200, delayTime = 300)//Global version chat area
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
                val topRegionMaxY = 138
                val approveButtons = mutableListOf<android.graphics.Point>()
                for (sliceStartY in (topRegionMaxY + 1)..approveSearchEndY step approveSliceHeight) {
                    val sliceEndY = (sliceStartY + approveSliceHeight).coerceAtMost(approveSearchEndY)
                    var currentY = sliceStartY
                    while (currentY < sliceEndY) {
                        val sliceSchema = ColorSchema.rescope(
                            approveBaseSchema, approveBaseSchema.x1, currentY, approveBaseSchema.x2, sliceEndY
                        )
                        val candidate = findMultiColors(schema = sliceSchema, byteBuffer = screenBuffer, increment = 1) ?: break
                        if (approveButtons.none { kotlin.math.abs(it.y - candidate.y) < 30 }) approveButtons.add(candidate)
                        currentY = (candidate.y + 1).coerceAtLeast(currentY + 1).coerceAtMost(sliceEndY)
                    }
                }
                if (approveButtons.isEmpty()) {
                    for (sliceStartY in approveSearchStartY..topRegionMaxY step approveSliceHeight) {
                        val sliceEndY = (sliceStartY + approveSliceHeight).coerceAtMost(topRegionMaxY)
                        var currentY = sliceStartY
                        while (currentY < sliceEndY) {
                            val sliceSchema = ColorSchema.rescope(
                                approveBaseSchema, approveBaseSchema.x1, currentY, approveBaseSchema.x2, sliceEndY
                            )
                            val candidate = findMultiColors(schema = sliceSchema, byteBuffer = screenBuffer, increment = 1) ?: break
                            if (approveButtons.none { kotlin.math.abs(it.y - candidate.y) < 30 }) approveButtons.add(candidate)
                            currentY = (candidate.y + 1).coerceAtLeast(currentY + 1).coerceAtMost(sliceEndY)
                        }
                    }
                }
                if (approveButtons.isNotEmpty()) {
                    val approveBtn = approveButtons.first()
                    showDebugInfo("检测到${approveButtons.size}个批准按钮，本轮处理第1个: (${approveBtn.x},${approveBtn.y})")
                    var ocrY = approveBtn.y
                    var swiped = false
                    if (approveBtn.x in 15..475 && approveBtn.y in 7..138) {
                        TouchActions.swipe(approveBtn.x, approveBtn.y, approveBtn.x, approveBtn.y + 273, delayTime = 200)
                        ocrY = approveBtn.y + 273
                        swiped = true
                    }
                    val repeatCount = 3
                    val lineFreq = linkedMapOf<String, Int>()
                    repeat(repeatCount) { round ->
                        val ocrResult = TextRecognizer.recognize(
                            startX = (approveBtn.x - 320).coerceAtLeast(20),
                            startY = (ocrY - 108).coerceAtLeast(10),
                            endX = (approveBtn.x + 100).coerceAtMost(480),
                            endY = (ocrY - 54).coerceAtMost(620),
                            useChinese = true,
                            threshold = 130,
                            applyPreprocess = true,
                            invertBinarization = true
                        )
                        val roundText = ocrResult.joinToString(" ") { it.text }
                        showDebugInfo("  第${round + 1}轮OCR: [$roundText]")
                        ocrResult.forEach { item ->
                            lineFreq[item.text] = (lineFreq[item.text] ?: 0) + 1
                        }
                    }
                    val lines = lineFreq.entries.sortedWith(compareByDescending { it.value }).map { it.key }
                    val ocrText = lines.joinToString(" ")
                    showDebugInfo("批准检测 OCR结果: $ocrText")
                    // Secret-code OCR result (multi-round majority) with recognition region
                    showDebugInfo("暗号同意 OCR识别结果(多轮取众数): [$ocrText] (识别区域 x:${(approveBtn.x - 320).coerceAtLeast(20)}-${(approveBtn.x + 100).coerceAtMost(480)}, y:${(ocrY - 108).coerceAtLeast(10)}-${(ocrY - 54).coerceAtMost(620)}, 批准按钮: (${approveBtn.x},${approveBtn.y}))")
                    showDebugInfo("  配置暗号: [${approveCode}] (长度${approveCode.length})")
                    lines.forEachIndexed { index, line ->
                        showDebugInfo("  暗号OCR行${index + 1}(频次${lineFreq[line]}): \"$line\"")
                    }
                    // After OCR, if the top-region button was swiped down, swipe it back
                    if (swiped) {
                        showDebugInfo("顶部区域按钮识别完成，反向滑动回原位")
                        TouchActions.swipe(approveBtn.x, approveBtn.y + 273, approveBtn.x, approveBtn.y, delayTime = 200)
                    }
                    // Skip click when OCR is empty to avoid false reject
                    if (ocrText.isBlank()) {
                        accountMessage("批准成员加入: OCR结果为空，跳过本次检测(不批准不拒绝)")
                        showDebugInfo("【OCR空结果】跳过本轮，等待下轮检测")
                        continue
                    }
                    // 98% fuzzy match on the full joined OCR text (region tightened to this request only)
                    val matched = isApproxMatch(ocrText, approveCode)
                    if (matched) {
                        accountMessage("批准成员加入: 暗号匹配($approveCode), 点击批准")
                        showDebugInfo("暗号匹配($approveCode)，点击批准")
                        TouchActions.tap(approveBtn.x, approveBtn.y)
                        continue
                    } else {
                        accountMessage("批准成员加入: 暗号不匹配($ocrText), 点击拒绝")
                        showDebugInfo("暗号不匹配，点击拒绝")
                        TouchActions.tap(approveBtn.x - 211, approveBtn.y + 2)
                        continue
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
                            // Verify this is a real donation request by checking for the sword or human figure icon
                            val regionX1 = candidate.x - 400
                            val regionY1 = candidate.y - 15
                            val regionX2 = candidate.x
                            val regionY2 = candidate.y + 40
                            val swordCheckSchema = ColorSchema.rescope(
                                MyColors.DonateSword,
                                x1 = regionX1, y1 = regionY1, x2 = regionX2, y2 = regionY2
                            )
                            val swordIcon = findMultiColors(schema = swordCheckSchema, byteBuffer = screenBuffer, increment = 1)
                            if (swordIcon != null) {
                                found = candidate
                                break@outer
                            }
                            val humanFigureCheckSchema = ColorSchema.rescope(
                                MyColors.DonateHumanFigure,
                                x1 = regionX1, y1 = regionY1, x2 = regionX2, y2 = regionY2
                            )
                            val humanFigureIcon = findMultiColors(schema = humanFigureCheckSchema, byteBuffer = screenBuffer, increment = 1)
                            if (humanFigureIcon != null) {
                                found = candidate
                                break@outer
                            }
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
            TouchActions.swipe(1055, 240, -600, 250, delayTime = 100)//Swipe troops
            TouchActions.swipe(1055, 480, -600, 480, delayTime = 100)//Spells
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

private fun levenshteinDistance(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1)
        }
    }
    return dp[a.length][b.length]
}

private fun isApproxMatch(line: String, code: String): Boolean {
    val a = line.lowercase()
    val b = code.lowercase()
    if (b.isEmpty()) return false
    if (a.contains(b)) return true
    for (len in (b.length - 2).coerceAtLeast(1)..(b.length + 2)) {
        if (len > a.length) continue
        for (i in 0..(a.length - len)) {
            val dist = levenshteinDistance(a.substring(i, i + len), b)
            if (dist <= 1 || (1.0 - dist.toDouble() / b.length) >= 0.98) return true
        }
    }
    return false
}
