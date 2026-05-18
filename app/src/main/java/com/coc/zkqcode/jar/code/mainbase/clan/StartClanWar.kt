package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

private const val CLAN_WAR_RECENT_ONLINE_OCR_LEFT = 480
private const val CLAN_WAR_RECENT_ONLINE_OCR_TOP = 140
private const val CLAN_WAR_RECENT_ONLINE_OCR_RIGHT = 660
private const val CLAN_WAR_RECENT_ONLINE_OCR_BOTTOM = 560
private const val CLAN_WAR_JOIN_BUTTON_SEARCH_LEFT_OFFSET = 350
private const val CLAN_WAR_JOIN_BUTTON_SEARCH_TOP_OFFSET = -40
private const val CLAN_WAR_JOIN_BUTTON_SEARCH_RIGHT_OFFSET = 550
private const val CLAN_WAR_JOIN_BUTTON_SEARCH_BOTTOM_OFFSET = 20

suspend fun startClanWar(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_WAR.key)) return true
    val trainTroopButton = findMultiColorsUntil(listOf(MyColors.TrainTroops, MyColors.TrainTroops2), 100)
    if (trainTroopButton != null) {
        TouchActions.tap(60, 420)
    } else return enterMainScreen()
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 15_000L) {
        val startClanWar = findMultiColors(MyColors.StartClanWar)
        if (startClanWar != null) {
            TouchActions.tap(startClanWar.x, startClanWar.y)
        }
        val innerStartClanWar = findMultiColorsUntil(listOf(MyColors.InnerStartClanWar, MyColors.GrayInnerButton), 100)
        if (innerStartClanWar != null) {
            selectClanWarParticipant()
            return enterMainScreen()
        }
        delay(100)
    }
    return enterMainScreen()
}

suspend fun selectClanWarParticipant() {
    repeat(2) {
        TouchActions.swipe(600, 530, 600, -1000)
    }
    delayWithMultiplier(300)
    when (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_WAR_METHOD.key)) {
        "0" -> {
            repeat(8) {
                repeat(6) {
                    val startButton = findMultiColors(MyColors.InnerStartClanWar)
                    if (startButton != null) {
                        accountMessage("开启成功")
                        TouchActions.tap(startButton.x, startButton.y)
                        return
                    } else {
                        val joinButton = findMultiColors(MyColors.JoinedClanWar)
                        if (joinButton != null) {
                            TouchActions.tap(joinButton.x, joinButton.y)
                        }
                    }
                }
                TouchActions.swipe(600, 110, 600, 530)
                delayWithMultiplier(200)
            }
        }

        "1" -> {
            repeat(8) {
                selectRecentOnlineClanWarParticipants()
                TouchActions.swipe(600, 110, 600, 530)
                delayWithMultiplier(200)
            }
            //TODO: execute  selectClanWarParticipant(), but as if getConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_WAR_METHOD.key) were 0
        }

        else -> ShowMessage("Invalid Start Clan War method configuration")
    }
}

private suspend fun selectRecentOnlineClanWarParticipants() {
    val recognizedTexts = TextRecognizer.recognize(
        startX = CLAN_WAR_RECENT_ONLINE_OCR_LEFT,
        startY = CLAN_WAR_RECENT_ONLINE_OCR_TOP,
        endX = CLAN_WAR_RECENT_ONLINE_OCR_RIGHT,
        endY = CLAN_WAR_RECENT_ONLINE_OCR_BOTTOM,
        useChinese = true,
        threshold = 140,
        applyPreprocess = true,
        invertBinarization = true
    )
    var tappedCount = 0

    recognizedTexts.forEach { recognizedText ->
        val text = recognizedText.text.trim()
        val box = recognizedText.position
        if (text.isBlank() || box == null || !isRecentOnlineText(text)) return@forEach

        val textX = box.left + CLAN_WAR_RECENT_ONLINE_OCR_LEFT
        val textY = box.top + CLAN_WAR_RECENT_ONLINE_OCR_TOP
        val joinedClanWarInTextRow = ColorSchema.rescope(
            schema = MyColors.JoinedClanWar,
            x1 = textX + CLAN_WAR_JOIN_BUTTON_SEARCH_LEFT_OFFSET,
            y1 = textY + CLAN_WAR_JOIN_BUTTON_SEARCH_TOP_OFFSET,
            x2 = textX + CLAN_WAR_JOIN_BUTTON_SEARCH_RIGHT_OFFSET,
            y2 = textY + CLAN_WAR_JOIN_BUTTON_SEARCH_BOTTOM_OFFSET
        )
        val joinButton = findMultiColors(schema = joinedClanWarInTextRow, increment = 1)
        if (joinButton != null) {
            TouchActions.tap(joinButton.x, joinButton.y)
            tappedCount++
        }
    }

    if (tappedCount > 0) {
        ShowMessage("已选择最近上线参战成员: $tappedCount")
    }
}

private fun isRecentOnlineText(text: String): Boolean {
    return text.contains("天") || text.contains("前")
}