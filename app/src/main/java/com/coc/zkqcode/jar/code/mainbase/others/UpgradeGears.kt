package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.herohall.backToHeroHall
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlin.math.abs

data class HeroGearEntry(
    val heroKey: String, val displayName: String, val tapX: Int, val tapY: Int, val gears: List<Pair<String, ColorSchema>>
)

data class Zone2Slot(
    val x: Int,
    val y: Int,
    val hasCheckmark: Boolean,
    val hasArrow: Boolean,
    val gearKey: String?,
    val gearSchema: ColorSchema?
)

data class DetectionResult(
    val zone1Arrows: List<Pair<Int, Int>>,
    val zone2Slots: List<Zone2Slot>
)

private const val UPGRADE_SUCCESS = 0
private const val UPGRADE_MAX_LEVEL = 1
private const val UPGRADE_ALREADY_UPGRADING = 2

private const val STEP_DELAY = 1000

private data class RawDetection(val x: Int, val y: Int, val type: String)

private val HERO_ENTRIES = listOf(
    HeroGearEntry(
        heroKey = "barbarian_king", displayName = "野蛮人之王", tapX = 129, tapY = 395, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_IRON_FIST.key, MyColors.BarbarianKingIronFist),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_PUPPET.key, MyColors.BarbarianKingPuppet),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_RAGE_VIAL.key, MyColors.BarbarianKingRageVial),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_EARTHQUAKE_BOOTS.key, MyColors.BarbarianKingEarthquakeBoots),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_HEALING_BEARD.key, MyColors.BarbarianKingHealingBeard),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_SPIKED_SOCCER.key, MyColors.BarbarianKingSpikedSoccer),
        )
    ),
    HeroGearEntry(
        heroKey = "archer_queen", displayName = "弓箭女皇", tapX = 170, tapY = 391, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_CLOAK.key, MyColors.ArcherQueenCloak),
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_PUPPET.key, MyColors.ArcherQueenPuppet),
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_GIANT_ARROW.key, MyColors.ArcherQueenGiantArrow),
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_FROST_ARROW.key, MyColors.ArcherQueenFrostArrow),
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_HEALER_PUPPET.key, MyColors.ArcherQueenHealerPuppet),
        )
    ),
    HeroGearEntry(
        heroKey = "grand_warden", displayName = "大守护者", tapX = 238, tapY = 392, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_ETERNAL_TOME.key, MyColors.GrandWardenEternalTome),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_LIFE_GEM.key, MyColors.GrandWardenLifeGem),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_HEALING_TOME.key, MyColors.GrandWardenHealingTome),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_GIANT_FIREBALL.key, MyColors.GrandWardenGiantFireball),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_RAGE_GEM.key, MyColors.GrandWardenRageGem),
        )
    ),
    HeroGearEntry(
        heroKey = "royal_champion", displayName = "飞盾战神", tapX = 280, tapY = 396, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_SEEKING_SHIELD.key, MyColors.RoyalChampionSeekingShield),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_HOG_PUPPET.key, MyColors.RoyalChampionHogPuppet),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_ROYAL_GEM.key, MyColors.RoyalChampionRoyalGem),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_SPEED_VIAL.key, MyColors.RoyalChampionSpeedVial),
        )
    ),
    HeroGearEntry(
        heroKey = "dragon_duke", displayName = "飞龙公爵", tapX = 322, tapY = 395, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_FIRE_RING.key, MyColors.DragonDukeFireRing),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_SCALE_ARMOR.key, MyColors.DragonDukeScaleArmor),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_COMBUSTOR.key, MyColors.DragonDukeCombustor),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_LIGHTNING_FANG.key, MyColors.DragonDukeLightningFang),
        )
    ),
    HeroGearEntry(
        heroKey = "minion_prince", displayName = "亡灵王子", tapX = 202, tapY = 397, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_GUARDIAN_PUPPET.key, MyColors.MinionPrinceGuardianPuppet),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_IRON_PANTS.key, MyColors.MinionPrinceIronPants),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_NOBLE_DUMBBELL.key, MyColors.MinionPrinceNobleDumbbell),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_DARK_ORB.key, MyColors.MinionPrinceDarkOrb),
        )
    ),
)

private fun getAllEnabledCustomGears(): List<Pair<String, ColorSchema>> {
    val allGears = mutableListOf<Pair<String, ColorSchema>>()
    for (heroEntry in HERO_ENTRIES) {
        val enabledGears = heroEntry.gears.filter { (gearKey, _) -> getBooleanConfigRuntime(gearKey) }
        allGears.addAll(enabledGears)
    }
    return allGears
}

private fun findHeroAndGearByKey(gearKey: String): Pair<HeroGearEntry, Pair<String, ColorSchema>>? {
    for (heroEntry in HERO_ENTRIES) {
        for (gear in heroEntry.gears) {
            if (gear.first == gearKey) {
                return Pair(heroEntry, gear)
            }
        }
    }
    return null
}

private fun findHeroAndGearByPosition(arrowX: Int, arrowY: Int): Pair<HeroGearEntry, Pair<String, ColorSchema>>? {
    val TOLERANCE = 15
    var bestMatch: Pair<HeroGearEntry, Pair<String, ColorSchema>>? = null
    var bestDistance = Int.MAX_VALUE

    for (heroEntry in HERO_ENTRIES) {
        for ((gearKey, gearSchema) in heroEntry.gears) {
            if (gearSchema.x1 == 0 && gearSchema.x2 == 0 && gearSchema.y1 == 0 && gearSchema.y2 == 0) continue

            val inX = arrowX >= gearSchema.x1 - TOLERANCE && arrowX <= gearSchema.x2 + TOLERANCE
            val inY = arrowY >= gearSchema.y1 - TOLERANCE && arrowY <= gearSchema.y2 + TOLERANCE

            if (inX && inY) {
                val centerX = (gearSchema.x1 + gearSchema.x2) / 2
                val centerY = (gearSchema.y1 + gearSchema.y2) / 2
                val distance = abs(arrowX - centerX) + abs(arrowY - centerY)

                if (distance < bestDistance) {
                    bestDistance = distance
                    bestMatch = Pair(heroEntry, Pair(gearKey, gearSchema))
                }
            }
        }
    }

    return bestMatch
}

suspend fun upgradeGearsAction(shouldUpgradeWear: Boolean, shouldUpgradeCustom: Boolean) {
    accountMessage("开始升级装备流程")
    
    TouchActions.tap(685, 590, delayTime = STEP_DELAY)
    accountMessage("点击进入铁匠铺")

    if (findMultiColorsUntil(schemas = listOf(MyColors.SmithOreIcon), duration = 2000) == null) {
        accountMessage("未找到铁匠铺标志，返回英雄殿堂")
        backToHeroHall()
        return
    }
    accountMessage("确认已进入铁匠铺")
    delayWithMultiplier(STEP_DELAY)

    var totalUpgraded = 0

    accountMessage("===== 开始第一轮检测 =====")
    val enabledCustomGears = getAllEnabledCustomGears()
    val detectionResult = detectBothZones(enabledCustomGears)
    if (detectionResult == null) {
        accountMessage("截屏失败")
        backToHeroHall()
        return
    }

    accountMessage("第一轮检测完成：Zone1 检测到 ${detectionResult.zone1Arrows.size} 个箭头，Zone2 检测到 ${detectionResult.zone2Slots.size} 个槽位")

    if (shouldUpgradeWear && detectionResult.zone1Arrows.isNotEmpty()) {
        accountMessage("===== 开始升级 Zone1（穿戴装备） =====")
        val zone1Upgraded = upgradeZone1(detectionResult.zone1Arrows)
        totalUpgraded += zone1Upgraded
        accountMessage("Zone1 升级完成，共升级 $zone1Upgraded 件")
    } else if (shouldUpgradeWear) {
        accountMessage("Zone1 未检测到可升级的穿戴装备")
    }

    if (shouldUpgradeCustom && detectionResult.zone2Slots.isNotEmpty()) {
        accountMessage("===== 开始升级 Zone2 第一轮（自定义装备） =====")
        val zone2UpgradedRound1 = upgradeZone2(
            detectionResult.zone2Slots,
            shouldUpgradeWear,
            "Zone2第一轮"
        )
        totalUpgraded += zone2UpgradedRound1
        accountMessage("Zone2 第一轮升级完成，共升级 $zone2UpgradedRound1 件")
    } else if (shouldUpgradeCustom) {
        accountMessage("Zone2 第一轮未检测到可升级的自定义装备")
    }

    accountMessage("===== 滑动到第二页 =====")
    TouchActions.swipe(1236, 495, -200, 495, delayAfter = 100)
    delayWithMultiplier(STEP_DELAY)

    if (shouldUpgradeCustom) {
        accountMessage("===== 开始第二轮检测（仅 Zone2） =====")
        val screenBufferRound2 = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        if (screenBufferRound2 == null) {
            accountMessage("第二轮截屏失败")
        } else {
            val zone2SlotsRound2 = detectZone2(screenBufferRound2, enabledCustomGears)
            accountMessage("第二轮检测完成：Zone2 检测到 ${zone2SlotsRound2.size} 个槽位")

            if (zone2SlotsRound2.isNotEmpty()) {
                accountMessage("===== 开始升级 Zone2 第二轮（自定义装备） =====")
                val zone2UpgradedRound2 = upgradeZone2(
                    zone2SlotsRound2,
                    shouldUpgradeWear,
                    "Zone2第二轮"
                )
                totalUpgraded += zone2UpgradedRound2
                accountMessage("Zone2 第二轮升级完成，共升级 $zone2UpgradedRound2 件")
            } else {
                accountMessage("Zone2 第二轮未检测到可升级的自定义装备")
            }
        }
    }

    if (totalUpgraded == 0) {
        accountMessage("所有英雄都没有升级任何装备")
    } else {
        accountMessage("升级装备全部完成，共升级 $totalUpgraded 件")
    }

    backToHeroHall()
}

private suspend fun detectBothZones(
    enabledCustomGears: List<Pair<String, ColorSchema>>
): DetectionResult? {
    accountMessage("开始截屏并检测两个区域")
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
    if (screenBuffer == null) {
        accountMessage("截屏失败")
        return null
    }

    val zone1Arrows = detectZone1Arrows(screenBuffer)
    accountMessage("Zone1 检测到 ${zone1Arrows.size} 个穿戴装备箭头")

    val zone2Slots = detectZone2(screenBuffer, enabledCustomGears)
    accountMessage("Zone2 检测到 ${zone2Slots.size} 个装备槽位")

    return DetectionResult(zone1Arrows, zone2Slots)
}

private suspend fun upgradeZone1(
    initialArrows: List<Pair<Int, Int>>
): Int {
    if (initialArrows.isEmpty()) {
        return 0
    }

    val (tapX, tapY) = initialArrows.first()
    val identifiedHeroAndGear = findHeroAndGearByPosition(tapX, tapY)
    val gearName = identifiedHeroAndGear?.let { Schema.getDisplayName(it.second.first) } ?: "未知装备"
    val heroName = identifiedHeroAndGear?.first?.displayName ?: "未知英雄"
    
    accountMessage("准备升级 Zone1 装备：$heroName - $gearName")
    TouchActions.tap(tapX, tapY, delayTime = STEP_DELAY)

    val result = confirmAndExitGearDetail()
    if (result == UPGRADE_ALREADY_UPGRADING) {
        accountMessage("已有装备升级中，停止所有升级")
        backToHeroHall()
        return 0
    }
    if (result == UPGRADE_MAX_LEVEL) {
        accountMessage("$gearName 已最高等级，跳过")
        return 0
    }

    accountMessage("$heroName - $gearName 穿戴升级成功")
    return 1
}

private suspend fun upgradeZone2(
    initialSlots: List<Zone2Slot>,
    shouldUpgradeWear: Boolean,
    phaseName: String
): Int {
    val slot = initialSlots.firstOrNull { slot ->
        slot.hasArrow &&
        !(shouldUpgradeWear && slot.hasCheckmark)
    }

    if (slot == null) {
        accountMessage("$phaseName 没有符合条件的装备可升级")
        return 0
    }

    val identifiedHeroAndGear = if (slot.gearKey != null) {
        findHeroAndGearByKey(slot.gearKey)
    } else {
        null
    }
    val gearName = slot.gearKey?.let { Schema.getDisplayName(it) } ?: "未知装备"
    val heroName = identifiedHeroAndGear?.first?.displayName ?: "未知英雄"

    accountMessage("准备升级 $phaseName 装备：$heroName - $gearName")
    TouchActions.tap(slot.x, slot.y, delayTime = STEP_DELAY)

    val result = confirmAndExitGearDetail()
    if (result == UPGRADE_ALREADY_UPGRADING) {
        accountMessage("已有装备升级中，停止所有升级")
        backToHeroHall()
        return 0
    }
    if (result == UPGRADE_MAX_LEVEL) {
        accountMessage("$gearName 已最高等级，跳过")
        return 0
    }

    accountMessage("$heroName - $gearName 升级成功")
    return 1
}

private suspend fun confirmAndExitGearDetail(): Int {
    accountMessage("进入装备详情页")

    accountMessage("点击升级按钮 (1130, 640)")
    TouchActions.tap(1130, 640, delayTime = STEP_DELAY)

    accountMessage("点击确认按钮 (1130, 640)")
    TouchActions.tap(1130, 640, delayTime = STEP_DELAY)

    accountMessage("检测是否已返回装备列表")
    var returnAttempt = 0
    val maxReturnAttempts = 10 // 最多尝试 10 次返回
    val returnCheckIntervalMs = 500
    
    while (findMultiColors(schema = MyColors.GearListReturned, increment = 1) == null && returnAttempt < maxReturnAttempts) {
        returnAttempt++
        accountMessage("第 ${returnAttempt} 次尝试返回装备列表")
        clickRightBottom(1)
        delayWithMultiplier(returnCheckIntervalMs)
    }

    if (findMultiColors(schema = MyColors.GearListReturned, increment = 1) != null) {
        accountMessage("确认已返回装备列表")
    } else {
        accountMessage("尝试返回装备列表超时")
    }

    accountMessage("升级流程完成")
    return UPGRADE_SUCCESS
}

private suspend fun detectZone1Arrows(byteBuffer: ScreenCaptureManager.CaptureResult): List<Pair<Int, Int>> {
    val points = mutableListOf<Pair<Int, Int>>()
    val baseSchema = MyColors.Zone1EquippedArrow
    var searchSchema = baseSchema

    accountMessage("开始检测 Zone1 穿戴装备箭头...")
    while (points.size < 2) {
        val point = findMultiColors(schema = searchSchema, byteBuffer = byteBuffer, increment = 1)
        if (point == null) break

        val isDuplicate = points.any { (ex, _) -> abs(point.x - ex) < 20 }
        if (!isDuplicate) {
            points.add(Pair(point.x, point.y))
            accountMessage("检测到 Zone1 箭头：位置(${point.x}, ${point.y})")
        }

        val nextX1 = point.x + 30
        if (nextX1 >= searchSchema.x2) break
        searchSchema = ColorSchema.rescope(searchSchema, nextX1, searchSchema.y1, searchSchema.x2, searchSchema.y2)
    }
    accountMessage("Zone1 箭头检测完成，共找到 ${points.size} 个")

    return points
}

private suspend fun detectZone2(
    byteBuffer: ScreenCaptureManager.CaptureResult,
    enabledCustomGears: List<Pair<String, ColorSchema>>
): List<Zone2Slot> {
    val detections = mutableListOf<RawDetection>()

    accountMessage("开始检测 Zone2 装备箭头...")
    var arrowCount = 0
    var arrowSchema = MyColors.Zone2GearArrow
    while (true) {
        val point = findMultiColors(schema = arrowSchema, byteBuffer = byteBuffer, increment = 1)
        if (point == null) break
        val isDuplicate = detections.any { it.type == "gear_arrow" && abs(point.x - it.x) < 20 && abs(point.y - it.y) < 20 }
        if (!isDuplicate) {
            detections.add(RawDetection(point.x, point.y, "gear_arrow"))
            arrowCount++
            accountMessage("检测到 Zone2 装备箭头：位置(${point.x}, ${point.y})")
        }
        val nextX1 = point.x + 30
        if (nextX1 >= arrowSchema.x2) break
        arrowSchema = ColorSchema.rescope(arrowSchema, nextX1, arrowSchema.y1, arrowSchema.x2, arrowSchema.y2)
    }
    accountMessage("Zone2 装备箭头检测完成，共找到 $arrowCount 个")

    accountMessage("开始检测 Zone2 装备身份...")
    var gearIdentityCount = 0
    for ((gearKey, gearSchema) in enabledCustomGears) {
        val gearName = Schema.getDisplayName(gearKey)
        if (gearSchema.x1 == 0 && gearSchema.x2 == 0 && gearSchema.y1 == 0 && gearSchema.y2 == 0) continue
        val point = findMultiColors(schema = gearSchema, byteBuffer = byteBuffer, increment = 1)
        if (point != null) {
            detections.add(RawDetection(point.x, point.y, "gear_identity:$gearKey"))
            gearIdentityCount++
            accountMessage("检测到 Zone2 装备：$gearName 位置(${point.x}, ${point.y})")
        }
    }
    accountMessage("Zone2 装备身份检测完成，共找到 $gearIdentityCount 个")

    accountMessage("开始检测 Zone2 穿戴标记...")
    var checkmarkCount = 0
    var checkmarkSchema = MyColors.Zone2EquipCheckmark
    while (true) {
        val point = findMultiColors(schema = checkmarkSchema, byteBuffer = byteBuffer, increment = 1)
        if (point == null) break
        val isDuplicate = detections.any { it.type == "equip_checkmark" && abs(point.x - it.x) < 20 && abs(point.y - it.y) < 20 }
        if (!isDuplicate) {
            detections.add(RawDetection(point.x, point.y, "equip_checkmark"))
            checkmarkCount++
            accountMessage("检测到 Zone2 穿戴标记：位置(${point.x}, ${point.y})")
        }
        val nextX1 = point.x + 30
        if (nextX1 >= checkmarkSchema.x2) break
        checkmarkSchema = ColorSchema.rescope(checkmarkSchema, nextX1, checkmarkSchema.y1, checkmarkSchema.x2, checkmarkSchema.y2)
    }
    accountMessage("Zone2 穿戴标记检测完成，共找到 $checkmarkCount 个")

    return buildZone2Slots(detections, enabledCustomGears)
}

private fun buildZone2Slots(
    detections: List<RawDetection>, 
    enabledCustomGears: List<Pair<String, ColorSchema>>
): List<Zone2Slot> {
    if (detections.isEmpty()) {
        accountMessage("Zone2 没有检测结果，返回空列表")
        return emptyList()
    }
    accountMessage("开始构建 Zone2 槽位，共 ${detections.size} 个原始检测")

    val tolerance = 20
    val used = BooleanArray(detections.size) { false }
    val slots = mutableListOf<Zone2Slot>()

    for (i in detections.indices) {
        if (used[i]) continue
        val (cx, cy, _) = detections[i]

        val clusterDets = mutableListOf<RawDetection>()
        var sumX = 0
        var sumY = 0
        for (j in detections.indices) {
            if (used[j]) continue
            val (dx, dy, _) = detections[j]
            if (abs(dx - cx) <= tolerance && abs(dy - cy) <= tolerance) {
                used[j] = true
                clusterDets.add(detections[j])
                sumX += dx
                sumY += dy
            }
        }

        val centerX = sumX / clusterDets.size
        val centerY = sumY / clusterDets.size
        val hasArrow = clusterDets.any { it.type == "gear_arrow" }
        val hasCheckmark = clusterDets.any { it.type == "equip_checkmark" }

        val identityDet = clusterDets.firstOrNull { it.type.startsWith("gear_identity:") }
        val gearKey = identityDet?.type?.removePrefix("gear_identity:")
        val gearName = if (gearKey != null) Schema.getDisplayName(gearKey) else "未知装备"
        val gearSchema = if (gearKey != null) {
            enabledCustomGears.firstOrNull { (k, _) -> k == gearKey }?.second
        } else null

        val slotInfo = buildString {
            append("构建 Zone2 槽位：位置($centerX, $centerY)")
            append(" 有箭头=${if (hasArrow) "是" else "否"}")
            append(" 有穿戴标记=${if (hasCheckmark) "是" else "否"}")
            if (gearKey != null) append(" 装备=$gearName")
        }
        accountMessage(slotInfo)

        slots.add(
            Zone2Slot(
                x = centerX,
                y = centerY,
                hasCheckmark = hasCheckmark,
                hasArrow = hasArrow,
                gearKey = gearKey,
                gearSchema = gearSchema
            )
        )
    }
    accountMessage("Zone2 槽位构建完成，共 ${slots.size} 个槽位")

    return slots
}
