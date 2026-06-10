package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizedText
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

/**
 * 部落拉人功能
 * 流程：
 *   1. 点击进入部落页面的两个坐标
 *   2. 检测语言按钮，检测到则点击
 *   3. 没检测到语言按钮 → 检测是否成功进入
 *   4. 成功进入后，循环：
 *        - OCR 检测序号
 *        - 点击目标项
 *        - 检测邀请按钮图色
 *        - 滑动列表
 *        - 若连续 2 次识别到相同序号 → 到底，返回主页面
 */
suspend fun clanInvitePlayers(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.INVITE_PLAYERS.key)) return true

    accountMessage("开始部落拉人")

    // ========== 步骤 1：进入部落页面 ==========
    // 先点击一个坐标，再点击一个坐标
    TouchActions.tap(INVITE_TAP_COORDS[0].x, INVITE_TAP_COORDS[0].y, delayTime = 800)
    TouchActions.tap(INVITE_TAP_COORDS[1].x, INVITE_TAP_COORDS[1].y, delayTime = 1200)

    // ========== 步骤 2：检测语言按钮 ==========
    val langButton = findMultiColorsUntil(
        schemas = listOf(MyColors.ClanInviteLanguageButton, MyColors.ClanInviteLanguageButtonAlt),
        duration = 3000
    )
    if (langButton != null) {
        accountMessage("检测到语言按钮，点击切换")
        TouchActions.tap(langButton.x, langButton.y, delayTime = 800)
    } else {
        // 没检测到语言按钮 → 检测是否成功进入
        accountMessage("未检测到语言按钮，检测是否进入部落拉人页面")
    }

    // ========== 步骤 3：检测是否成功进入页面 ==========
    val enterSuccess = findMultiColorsUntil(
        schemas = listOf(MyColors.ClanInvitePageEntryIndicator, MyColors.ClanInvitePageEntryIndicatorAlt),
        duration = 3000
    )
    if (enterSuccess == null) {
        accountMessage("未检测到进入标志，部落拉人结束")
        return true
    }
    accountMessage("已进入部落拉人页面")

    // 成功进入后点击确认进入坐标
    TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[0].x, INVITE_ENTER_CONFIRM_COORDS[0].y, delayTime = 600)
    TouchActions.tap(INVITE_ENTER_CONFIRM_COORDS[1].x, INVITE_ENTER_CONFIRM_COORDS[1].y, delayTime = 800)

    // ========== 步骤 4：循环处理玩家列表 ==========
    var lastRecognizedIndex: String? = null
    var sameIndexCount = 0
    var loopCount = 0
    val maxLoops = 50

    while (loopCount < maxLoops) {
        loopCount++

        // ---- 4.1 OCR 检测序号 ----
        val recognizedTexts: List<RecognizedText> = TextRecognizer.recognize(
            startX = OCR_INDEX_REGION.left,
            startY = OCR_INDEX_REGION.top,
            endX = OCR_INDEX_REGION.right,
            endY = OCR_INDEX_REGION.bottom,
            useChinese = false,
            threshold = OCR_INDEX_THRESHOLD,
            saveImage = false,
            applyPreprocess = true,
            invertBinarization = true
        )

        val currentIndex = extractIndexNumber(recognizedTexts)
        accountMessage("当前识别序号: ${currentIndex ?: "未识别"}")

        // ---- 4.2 检测是否到底（连续 2 次识别相同序号）----
        if (currentIndex != null && currentIndex == lastRecognizedIndex) {
            sameIndexCount++
            if (sameIndexCount >= 2) {
                accountMessage("检测到已到底部（序号连续重复 2 次），返回主页面")
                break
            }
        } else {
            sameIndexCount = 0
        }
        lastRecognizedIndex = currentIndex

        // ---- 4.3 点击列表项（再点击确认邀请）----
        val itemTap = INVITE_ITEM_TAP_COORDS[loopCount % INVITE_ITEM_TAP_COORDS.size]
        TouchActions.tap(itemTap.x, itemTap.y, delayTime = 500)

        val confirmTap = INVITE_CONFIRM_TAP_COORDS[loopCount % INVITE_CONFIRM_TAP_COORDS.size]
        TouchActions.tap(confirmTap.x, confirmTap.y, delayTime = 500)

        // ---- 4.4 检测邀请按钮图色 ----
        val inviteButton = findMultiColorsUntil(
            schemas = listOf(MyColors.ClanInviteActionButton, MyColors.ClanInviteActionButtonAlt),
            duration = 2000
        )
        if (inviteButton != null) {
            // 检测到邀请按钮，点击
            TouchActions.tap(inviteButton.x, inviteButton.y, delayTime = 600)

            // 点击后关闭弹窗
            val closeButton = findMultiColorsUntil(
                schemas = listOf(MyColors.ClanInviteCloseButton, MyColors.ClanInviteCloseButtonAlt),
                duration = 2000
            )
            if (closeButton != null) {
                TouchActions.tap(closeButton.x, closeButton.y, delayTime = 500)
            }
        }

        // ---- 4.5 滑动列表 ----
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

    accountMessage("部落拉人结束")
    return true
}

/**
 * 从 OCR 识别结果中提取序号数字
 */
private fun extractIndexNumber(texts: List<RecognizedText>): String? {
    // 优先取位置最高（Y 坐标最小）的数字
    val topmost = texts
        .filter { it.text.isNotBlank() && it.text.any { c -> c.isDigit() } }
        .sortedBy { it.position?.top ?: Int.MAX_VALUE }
        .firstOrNull() ?: return null

    // 提取纯数字部分
    val digits = topmost.text.filter { it.isDigit() }
    return digits.ifBlank { null }
}

// ============================================================
// 配置区：坐标、颜色模式占位（后续由用户提供真实数据后替换）
// ============================================================

// 进入部落页面的两个点击坐标
private data class TapCoord(val x: Int, val y: Int)
private val INVITE_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(640, 360),  // TODO: 替换为真实坐标 1
    TapCoord(800, 500)   // TODO: 替换为真实坐标 2
)

// 成功进入页面后的两个确认点击坐标
private val INVITE_ENTER_CONFIRM_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(640, 400),  // TODO: 替换为真实坐标 1
    TapCoord(640, 550)   // TODO: 替换为真实坐标 2
)

// 玩家列表项点击坐标（多位置循环点击）
private val INVITE_ITEM_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(640, 300),  // TODO: 替换为真实坐标 1
    TapCoord(640, 450),  // TODO: 替换为真实坐标 2
    TapCoord(640, 600)   // TODO: 替换为真实坐标 3
)

// 确认邀请按钮点击坐标
private val INVITE_CONFIRM_TAP_COORDS: Array<TapCoord> = arrayOf(
    TapCoord(800, 350),  // TODO: 替换为真实坐标 1
    TapCoord(800, 500),  // TODO: 替换为真实坐标 2
    TapCoord(800, 650)   // TODO: 替换为真实坐标 3
)

// 滑动参数
private data class SwipeCoords(val startX: Int, val startY: Int, val endX: Int, val endY: Int)
private val SWIPE_COORDS: SwipeCoords = SwipeCoords(
    startX = 640,  // TODO: 替换为真实起点 X
    startY = 600,  // TODO: 替换为真实起点 Y
    endX = 640,    // TODO: 替换为真实终点 X
    endY = 300     // TODO: 替换为真实终点 Y
)

// OCR 序号识别区域（屏幕坐标，720x1280 为参考分辨率）
private data class OcrRegion(val left: Int, val top: Int, val right: Int, val bottom: Int)
private val OCR_INDEX_REGION: OcrRegion = OcrRegion(
    left = 200,   // TODO: 替换为真实区域左
    top = 250,    // TODO: 替换为真实区域上
    right = 400,  // TODO: 替换为真实区域右
    bottom = 650  // TODO: 替换为真实区域下
)

private const val OCR_INDEX_THRESHOLD: Int = 140
