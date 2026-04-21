package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.recognizer.cleanOcrDigits
import kotlinx.coroutines.delay

suspend fun detectMyClanWarPosition() {
    val clanWarInfo = findMultiColors(MyColors.ClanWarInfo)
    if (clanWarInfo != null) {
        TouchActions.tap(clanWarInfo.x, clanWarInfo.y)
        TouchActions.tap(755, 60)
    }
    val ourTeam = findMultiColorsUntil(schemas = listOf(MyColors.OurTeam), duration = 1000)
    if (ourTeam != null) {
        delayWithMultiplier(1000)
        repeat(13) {
            val position = findMultiColorsUntil(schemas = listOf(MyColors.MyPositionGreen), duration = 500)
            if (position != null) {
                // Calculate crop area using position offsets
                val startX = position.x - 10
                val startY = position.y + 12
                val endX = position.x + 54
                val endY = position.y + 44

                // Use ML Kit to recognize the number in the area
                val results = TextRecognizer.recognize(
                    startX = startX, startY = startY, endX = endX, endY = endY,
                    useChinese = false, applyPreprocess = true, threshold = 200
                )
                if (results.isNotEmpty()) {
                    val rawText = results.joinToString("") { it.text }
                    val number = cleanOcrDigits(rawText)
                    accountMessage("Detected clan war position number: $number")
                } else {
                    accountMessage("No number detected at position (${position.x}, ${position.y})")
                }
                // Wait for 1 hour
                delay(3600_000)
            }
            TouchActions.swipe(320, 570, 320, 210)
        }
    }
}

suspend fun clanWarTest() {
    repeat(13) {
        TouchActions.swipe(320, 570, 320, 210)
        val position = findMultiColorsUntil(schemas = listOf(MyColors.MyPositionGreen), duration = 500)
        if (position != null) {
            // Calculate crop area using position offsets
            val startX = position.x - 10
            val startY = position.y + 12
            val endX = position.x + 54
            val endY = position.y + 44

            // Use ML Kit to recognize the number in the area
            val results = TextRecognizer.recognize(
                startX = startX, startY = startY, endX = endX, endY = endY,
                useChinese = false, applyPreprocess = true, threshold = 200
            )
            if (results.isNotEmpty()) {
                val rawText = results.joinToString("") { it.text }
                val number = cleanOcrDigits(rawText)
                accountMessage("Detected clan war position number: $number")
            } else {
                accountMessage("No number detected at position (${position.x}, ${position.y})")
            }
            // Wait for 1 hour
            delay(3600_000)
        }
    }
}
