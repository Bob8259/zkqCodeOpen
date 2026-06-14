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

// Zone2 custom-gear slot: position identified purely by image-color recognition
// (no upgrade arrow, no wear checkmark, no region construction).
data class Zone2Slot(
    val x: Int,
    val y: Int,
    val gearKey: String
)

private const val UPGRADE_SUCCESS = 0
private const val UPGRADE_MAX_LEVEL = 1
private const val UPGRADE_ALREADY_UPGRADING = 2

private const val STEP_DELAY = 1000

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
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_CLONE_MIRROR.key, MyColors.ArcherQueenCloneMirror),
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

    // Zone1: worn gears (still uses arrow detection + region identification)
    if (shouldUpgradeWear) {
        accountMessage("===== 开始检测穿戴装备 =====")
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        if (screenBuffer == null) {
            accountMessage("截屏失败")
            backToHeroHall()
            return
        }
        val zone1Arrows = detectZone1Arrows(screenBuffer)
        accountMessage("检测到 ${zone1Arrows.size} 个穿戴装备箭头")

        if (zone1Arrows.isNotEmpty()) {
            accountMessage("===== 开始升级穿戴装备 =====")
            val zone1Upgraded = upgradeZone1(zone1Arrows)
            totalUpgraded += zone1Upgraded
            accountMessage("穿戴装备升级完成，共升级 $zone1Upgraded 件")
        } else {
            accountMessage("未检测到可升级的穿戴装备")
        }
    }

    // Zone2: custom gears (image-color recognition only, 2 rounds with swipe)
    if (shouldUpgradeCustom) {
        val enabledCustomGears = getAllEnabledCustomGears()
        accountMessage("启用的自定义装备共 ${enabledCustomGears.size} 件")

        // Round 1
        accountMessage("===== 开始第一轮检测（截屏 1/2）=====")
        val screenBufferRound1 = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        if (screenBufferRound1 == null) {
            accountMessage("第一轮截屏失败")
        } else {
            val zone2SlotsRound1 = detectZone2(screenBufferRound1, enabledCustomGears)
            if (zone2SlotsRound1.isNotEmpty()) {
                accountMessage("===== 开始第一轮升级 =====")
                val upgraded = upgradeZone2(zone2SlotsRound1, "第一轮")
                totalUpgraded += upgraded
                accountMessage("第一轮升级完成，共升级 $upgraded 件")
            } else {
                accountMessage("第一轮未检测到自定义装备")
            }
        }

        // Swipe to second page
        accountMessage("===== 滑动到第二页 =====")
        TouchActions.swipe(1236, 495, -200, 495, delayAfter = 100)
        delayWithMultiplier(STEP_DELAY)

        // Round 2
        accountMessage("===== 开始第二轮检测（截屏 2/2）=====")
        val screenBufferRound2 = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        if (screenBufferRound2 == null) {
            accountMessage("第二轮截屏失败")
        } else {
            val zone2SlotsRound2 = detectZone2(screenBufferRound2, enabledCustomGears)
            if (zone2SlotsRound2.isNotEmpty()) {
                accountMessage("===== 开始第二轮升级 =====")
                val upgraded = upgradeZone2(zone2SlotsRound2, "第二轮")
                totalUpgraded += upgraded
                accountMessage("第二轮升级完成，共升级 $upgraded 件")
            } else {
                accountMessage("第二轮未检测到自定义装备")
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
    
    accountMessage("准备升级穿戴装备：$heroName - $gearName")
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
    phaseName: String
): Int {
    val slot = initialSlots.firstOrNull()

    if (slot == null) {
        accountMessage("$phaseName 没有可升级的装备")
        return 0
    }

    val identifiedHeroAndGear = findHeroAndGearByKey(slot.gearKey)
    val gearName = Schema.getDisplayName(slot.gearKey)
    val heroName = identifiedHeroAndGear?.first?.displayName ?: "未知英雄"

    accountMessage("准备升级 $phaseName 装备：$heroName - $gearName (${slot.x}, ${slot.y})")
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

private suspend fun detectZone2(
    byteBuffer: ScreenCaptureManager.CaptureResult,
    enabledCustomGears: List<Pair<String, ColorSchema>>
): List<Zone2Slot> {
    val slots = mutableListOf<Zone2Slot>()

    for ((gearKey, gearSchema) in enabledCustomGears) {
        if (gearSchema.x1 == 0 && gearSchema.x2 == 0 && gearSchema.y1 == 0 && gearSchema.y2 == 0) continue
        val point = findMultiColors(schema = gearSchema, byteBuffer = byteBuffer, increment = 1)
        if (point != null) {
            slots.add(Zone2Slot(x = point.x, y = point.y, gearKey = gearKey))
        }
    }

    // Concise summary log (matches the "可升级建筑 / 优先级列表" style used elsewhere)
    val summary = slots.mapIndexed { idx, slot ->
        "${idx + 1}. ${Schema.getDisplayName(slot.gearKey)} (${slot.x}, ${slot.y})"
    }
    if (summary.isEmpty()) {
        accountMessage("未识别到自定义装备")
    } else {
        accountMessage("识别到 ${slots.size} 件自定义装备：\n${summary.joinToString("\n")}")
    }

    return slots
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

    accountMessage("开始检测穿戴装备箭头...")
    while (true) {
        val point = findMultiColors(schema = searchSchema, byteBuffer = byteBuffer, increment = 1)
        if (point == null) break

        val isDuplicate = points.any { (ex, _) -> abs(point.x - ex) < 20 }
        if (!isDuplicate) {
            points.add(Pair(point.x, point.y))
            accountMessage("检测到穿戴装备箭头：位置(${point.x}, ${point.y})")
        }

        val nextX1 = point.x + 30
        if (nextX1 >= searchSchema.x2) break
        searchSchema = ColorSchema.rescope(searchSchema, nextX1, searchSchema.y1, searchSchema.x2, searchSchema.y2)
    }
    accountMessage("穿戴装备箭头检测完成，共找到 ${points.size} 个")

    return points
}
