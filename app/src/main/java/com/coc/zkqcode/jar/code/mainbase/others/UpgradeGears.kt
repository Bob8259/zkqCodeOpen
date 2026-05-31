package com.coc.zkqcode.jar.code.mainbase.others

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

/**
 * Represents a hero entry in the smith/gear upgrade page.
 * Each hero has a fixed tap coordinate to enter their gear list,
 * and a list of gears with their ColorSchema for identification.
 *
 * @param heroKey The config key prefix for this hero (e.g. "barbarian_king")
 * @param displayName The display name for logging
 * @param tapX The x coordinate to tap this hero in the smith page
 * @param tapY The y coordinate to tap this hero in the smith page
 * @param gears The list of gear config keys and their ColorSchema for identification
 */
data class HeroGearEntry(
    val heroKey: String, val displayName: String, val tapX: Int, val tapY: Int, val gears: List<Pair<String, ColorSchema>> // (gearConfigKey, gearColorSchema)
)

/** Result of gear upgrade confirm flow */
private const val UPGRADE_SUCCESS = 0
private const val UPGRADE_MAX_LEVEL = 1
private const val UPGRADE_ALREADY_UPGRADING = 2

/** Maximum number of equipped gear slots per hero */
private const val MAX_EQUIPPED_GEARS = 2

/** Step delay for observation (3 seconds) */
private const val STEP_DELAY = 1000

/**
 * Hero entries with their tap coordinates and gear identification schemas.
 * New heroes can be added here as their coordinates and gear ColorSchema data become available.
 */
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

/**
 * The gear upgrade action performed inside the hero hall.
 * Enters the smith page, then processes each hero with the unified flow.
 *
 * @param shouldUpgradeWear Whether to upgrade equipped gears (arrow detection)
 * @param shouldUpgradeCustom Whether to upgrade custom-selected gears (ColorSchema detection)
 */
suspend fun upgradeGearsAction(shouldUpgradeWear: Boolean, shouldUpgradeCustom: Boolean) {
    TouchActions.tap(685, 590, delayTime = STEP_DELAY)

    // Verify we're in the smith page
    if (findMultiColorsUntil(schemas = listOf(MyColors.SmithOreIcon), duration = 2000) == null) {
        accountMessage("未找到铁匠铺标志")
        backToHeroHall()
        return
    }
    delayWithMultiplier(STEP_DELAY)

    var totalUpgraded = 0

    for ((_, heroEntry) in HERO_ENTRIES.withIndex()) {

        // Skip heroes with placeholder coordinates
        if (heroEntry.tapX == 0 && heroEntry.tapY == 0) {
            accountMessage("${heroEntry.displayName} 坐标未设置，跳过")
            continue
        }

        // Count how many custom gears are enabled for this hero
        val enabledCustomGears = if (shouldUpgradeCustom) {
            heroEntry.gears.filter { (gearKey, _) -> getBooleanConfigRuntime(gearKey) }
        } else {
            emptyList()
        }

        // Equipped gear upgrade doesn't need per-gear config check — it detects arrows
        val needsProcessing = shouldUpgradeWear || enabledCustomGears.isNotEmpty()
        if (!needsProcessing) {
            accountMessage("${heroEntry.displayName} 没有选中任何装备，跳过")
            continue
        }

        // Calculate total gears to upgrade for this hero
        val wearCount = if (shouldUpgradeWear) MAX_EQUIPPED_GEARS else 0
        val customCount = enabledCustomGears.size
        val totalGearCount = wearCount + customCount

        accountMessage("处理 ${heroEntry.displayName} 的装备升级")

        // Tap the hero entry in the smith page
        TouchActions.tap(heroEntry.tapX, heroEntry.tapY)

        // Track upgraded count for this hero
        @Suppress("AssignedValueIsNeverRead")
        var heroUpgraded = 0

        // Phase 1: Upgrade equipped gears (arrow detection), max 2 per hero
        val upgradedSchemas = mutableSetOf<ColorSchema>()
        if (shouldUpgradeWear) {
            val wearUpgraded = upgradeEquippedGears(heroEntry, upgradedSchemas, heroUpgraded, totalGearCount)
            heroUpgraded += wearUpgraded
            totalUpgraded += wearUpgraded
            if (wearUpgraded > 0) {
                accountMessage("${heroEntry.displayName} 穿戴升级完成，升级${wearUpgraded}件")
            }
            delayWithMultiplier(STEP_DELAY)
        }

        // Phase 2: Upgrade custom-selected gears, skip already upgraded ones
        if (shouldUpgradeCustom) {
            val customUpgraded = upgradeCustomGears(heroEntry, upgradedSchemas, heroUpgraded, totalGearCount)
            heroUpgraded += customUpgraded
            totalUpgraded += customUpgraded
        }

        // Switch to next hero by tapping its coordinate (no need to close hero gear list separately)
        // The next iteration will tap the next hero's coordinate directly
        delayWithMultiplier(STEP_DELAY)
    }

    if (totalUpgraded == 0) {
        accountMessage("没有升级任何装备")
    } else {
        accountMessage("升级装备完成，共升级${totalUpgraded}件")
    }

    // Exit smith page, navigate back to hero hall
    backToHeroHall()
}

/**
 * Performs the gear upgrade confirm flow using fixed coordinates.
 * This is the original equipment upgrade flow:
 * 0. Check GearMaxLevel — if detected, skip this gear (already at max level)
 * 1. tap(1130, 640) — upgrade
 * 2. tap(1130, 640) — confirm
 * 3. tap(1220, 635) — exit gear detail view, return to hero's gear list
 *
 * After confirm, checks for AlreadyBeingUpgraded to detect if another upgrade is running.
 *
 * @return UPGRADE_SUCCESS if upgrade was performed,
 *         UPGRADE_MAX_LEVEL if gear is already at max level (skipped),
 *         UPGRADE_ALREADY_UPGRADING if another upgrade is running (stop all)
 */
private suspend fun confirmAndExitGearDetail(): Int {
    // Step 0: Check if gear is already at max level before tapping upgrade
    if (findMultiColors(schema = MyColors.GearMaxLevel, increment = 1) != null) {
        accountMessage("该装备已升到最高等级，跳过")
        // Exit gear detail view to return to gear list
        TouchActions.tap(1220, 635, delayTime = STEP_DELAY)
        return UPGRADE_MAX_LEVEL
    }

    // Step 1: tap upgrade button (1130, 640) — upgrade
    TouchActions.tap(1130, 640, delayTime = STEP_DELAY)

    // Step 2: tap confirm button (1130, 640) — confirm
    TouchActions.tap(1130, 640, delayTime = STEP_DELAY)

    // Close gem shop popup if it appears (insufficient resources)
    clickRightBottom(1)

    // Step 3: tap exit button (1220, 635) — exit gear detail view
    TouchActions.tap(1220, 635, delayTime = STEP_DELAY)

    // Check if another gear is already being upgraded
    if (findMultiColors(schema = MyColors.AlreadyBeingUpgraded, increment = 1) != null) {
        accountMessage("已有装备升级中，停止升级")
        return UPGRADE_ALREADY_UPGRADING
    }

    return UPGRADE_SUCCESS
}

/**
 * Upgrades currently equipped gears for a hero by detecting upgrade arrows.
 * Uses [MyColors.UpgradeGearArrow] variants and [MyColors.NewGear] to find upgradable equipped gears.
 * Limited to [MAX_EQUIPPED_GEARS] upgrades per hero (2 equipped slots).
 *
 * Bug fix: Previously, after upgrading the 1st equipped gear, re-scanning would find
 * the same coordinate again. Now we collect ALL arrow coordinates FIRST, then click
 * only the pre-collected coordinates sequentially — no re-scanning after each upgrade.
 *
 * After each successful upgrade, identifies which gear was upgraded by matching
 * its ColorSchema against the hero's known gear list, and adds it to [upgradedSchemas]
 * for deduplication in the custom upgrade phase.
 *
 * @param heroEntry The hero being processed
 * @param upgradedSchemas Set to collect ColorSchemas of upgraded gears for dedup
 * @param heroUpgradedCount Current number of already upgraded gears for this hero (for progress display)
 * @param totalGearCount Total number of gears to upgrade for this hero (for progress display)
 * @return Number of gears successfully upgraded
 */
private suspend fun upgradeEquippedGears(
    heroEntry: HeroGearEntry, upgradedSchemas: MutableSet<ColorSchema>, heroUpgradedCount: Int, totalGearCount: Int
): Int {
    var upgraded = 0
    val allArrowPoints = mutableListOf<Pair<Int, Int>>()

    // Step 1: Collect ALL upgrade arrow positions BEFORE any upgrades (no re-scanning)
    val arrowSchemas = listOf(
        MyColors.UpgradeGearArrow, MyColors.UpgradeGearArrow2, MyColors.UpgradeGearArrow3, MyColors.UpgradeGearArrow4, MyColors.UpgradeGearArrow5, MyColors.UpgradeGearArrow6, MyColors.NewGear
    )

    for (arrowSchema in arrowSchemas) {
        val point = findMultiColors(schema = arrowSchema, increment = 1)
        if (point != null) {
            // Filter duplicates: skip if too close (within 20px) to an already-found point
            val isDuplicate = allArrowPoints.any { (ex, ey) ->
                abs(point.x - ex) < 20 && abs(point.y - ey) < 20
            }
            if (!isDuplicate) {
                allArrowPoints.add(Pair(point.x, point.y))
            }
        }
    }

    if (allArrowPoints.isEmpty()) {
        accountMessage("${heroEntry.displayName} 未找到可升级的穿戴装备")
        return 0
    }

    // Limit to MAX_EQUIPPED_GEARS (2 equipped slots per hero)
    val upgradePoints = allArrowPoints.take(MAX_EQUIPPED_GEARS)
    accountMessage("${heroEntry.displayName} 找到${allArrowPoints.size}个穿戴装备升级箭头，将升级${upgradePoints.size}个")

    // Step 2: Click each pre-collected arrow coordinate for upgrade (no re-scanning)
    for (arrowCoord in upgradePoints) {
        val (tapX, tapY) = arrowCoord

        // Identify which gear this arrow belongs to BEFORE clicking (coordinate-based, no screen reading)
        val identifiedGear = identifyGearByPosition(heroEntry, tapX, tapY)

        TouchActions.tap(tapX, tapY, delayTime = STEP_DELAY)

        // Confirm upgrade and exit gear detail using fixed coordinates
        val result = confirmAndExitGearDetail()
        if (result == UPGRADE_ALREADY_UPGRADING) {
            // AlreadyBeingUpgraded detected — stop all upgrades
            backToHeroHall()
            return upgraded
        }
        if (result == UPGRADE_MAX_LEVEL) {
            // Gear is at max level — skip to next gear
            val gearName = identifiedGear?.let { Schema.getDisplayName(it.first) } ?: "未知装备"
            accountMessage("${heroEntry.displayName} $gearName 该装备已升到最高等级，跳过")
            continue
        }

        upgraded++
        @Suppress("UNUSED_VARIABLE")
        val currentProgress = heroUpgradedCount + upgraded
        val gearName = identifiedGear?.let { Schema.getDisplayName(it.first) } ?: "未知装备"
        accountMessage("${heroEntry.displayName} $gearName 穿戴升级成功，已升级${upgraded}件")

        // Add identified gear to dedup set (coordinate-based, no visual matching needed)
        if (identifiedGear != null) {
            val (gearKey, gearSchema) = identifiedGear
            upgradedSchemas.add(gearSchema)
            accountMessage("识别到已升级装备: ${Schema.getDisplayName(gearKey)}")
        }
    }

    return upgraded
}

/**
 * Identifies which gear an upgrade arrow belongs to by checking if the arrow
 * coordinate falls within the gear ColorSchema bounding box.
 *
 * This is a pure coordinate calculation — no screen reading required,
 * so it works correctly even after the screen has changed.
 *
 * Uses ±15px tolerance to account for slight position variations.
 * If multiple gears match, returns the one whose center is closest to the arrow.
 *
 * @param heroEntry The hero being processed
 * @param arrowX The x coordinate of the upgrade arrow
 * @param arrowY The y coordinate of the upgrade arrow
 * @return Pair of (gearConfigKey, gearColorSchema) if found, null otherwise
 */
private fun identifyGearByPosition(
    heroEntry: HeroGearEntry, arrowX: Int, arrowY: Int
): Pair<String, ColorSchema>? {
    val TOLERANCE = 15
    var bestMatch: Pair<String, ColorSchema>? = null
    var bestDistance = Int.MAX_VALUE

    for ((gearKey, gearSchema) in heroEntry.gears) {
        // Skip placeholder ColorSchema
        if (gearSchema.x1 == 0 && gearSchema.x2 == 0 && gearSchema.y1 == 0 && gearSchema.y2 == 0) continue

        // Check if arrow coordinate falls within the gear bounding box (with tolerance)
        val inX = arrowX >= gearSchema.x1 - TOLERANCE && arrowX <= gearSchema.x2 + TOLERANCE
        val inY = arrowY >= gearSchema.y1 - TOLERANCE && arrowY <= gearSchema.y2 + TOLERANCE

        if (inX && inY) {
            // Calculate distance from arrow to gear centre
            val centerX = (gearSchema.x1 + gearSchema.x2) / 2
            val centerY = (gearSchema.y1 + gearSchema.y2) / 2
            val distance = abs(arrowX - centerX) + abs(arrowY - centerY)

            if (distance < bestDistance) {
                bestDistance = distance
                bestMatch = Pair(gearKey, gearSchema)
            }
        }
    }

    return bestMatch
}

/**
 * Upgrades custom-selected gears for a hero, skipping any gears already
 * upgraded in the equipped phase (identified by [skipSchemas]).
 *
 * For each gear enabled in user config:
 * 1. Skip if its ColorSchema is in [skipSchemas] (already upgraded as equipped gear)
 * 2. Skip if ColorSchema is placeholder (0,0,0,0)
 * 3. Find the gear on screen using its ColorSchema
 * 4. Tap the gear to open upgrade dialog
 * 5. Confirm upgrade using fixed coordinates: tap(1130,640) x2 + tap(1220,635) exit
 *
 * @param heroEntry The hero being processed
 * @param skipSchemas ColorSchemas of gears already upgraded in the equipped phase
 * @param heroUpgradedCount Current number of already upgraded gears for this hero (for progress display)
 * @param totalGearCount Total number of gears to upgrade for this hero (for progress display)
 * @return Number of gears successfully upgraded
 */
private suspend fun upgradeCustomGears(
    heroEntry: HeroGearEntry, skipSchemas: Set<ColorSchema>, heroUpgradedCount: Int, totalGearCount: Int
): Int {
    var upgraded = 0

    for (gearPair in heroEntry.gears) {
        val (gearKey, gearSchema) = gearPair

        // Check if this specific gear is enabled in config
        if (!getBooleanConfigRuntime(gearKey)) {
            continue
        }

        // Dedup: skip gears already upgraded in the equipped phase
        if (gearSchema in skipSchemas) {
            accountMessage("装备 ${Schema.getDisplayName(gearKey)} 已通过穿戴升级处理，跳过")
            continue
        }

        // Skip placeholder ColorSchema (zero-area search regions)
        if (gearSchema.x1 == 0 && gearSchema.x2 == 0 && gearSchema.y1 == 0 && gearSchema.y2 == 0) {
            accountMessage("装备 ${Schema.getDisplayName(gearKey)} 图色数据未设置，跳过")
            continue
        }

        // Find the gear on screen using its ColorSchema
        var gearPoint = findMultiColors(schema = gearSchema, increment = 1)

        // Fallback: if not found by original ColorSchema, the gear may have a ✓ overlay
        // Try arrow detection + coordinate matching to identify the gear
        if (gearPoint == null) {
            for (arrowSchema in listOf(
                MyColors.UpgradeGearArrow, MyColors.UpgradeGearArrow2, MyColors.UpgradeGearArrow3, MyColors.UpgradeGearArrow4, MyColors.UpgradeGearArrow5, MyColors.UpgradeGearArrow6, MyColors.NewGear
            )) {
                val arrowPoint = findMultiColors(schema = arrowSchema, increment = 1)
                if (arrowPoint != null) {
                    val matchedGear = identifyGearByPosition(heroEntry, arrowPoint.x, arrowPoint.y)
                    if (matchedGear != null && matchedGear.first == gearKey) {
                        gearPoint = arrowPoint
                        break
                    }
                }
            }
        }

        if (gearPoint == null) {
            accountMessage("未找到装备 ${Schema.getDisplayName(gearKey)}，可能已满级或不可升级")
            continue
        }

        // Check if the gear has an upgrade arrow (✓) in its top-right corner
        // Only proceed if the gear is currently equipped/upgradable
        val arrowX1 = gearSchema.x2 - 14
        val arrowY1 = gearSchema.y1 - 6
        val arrowX2 = gearSchema.x2 + 4
        val arrowY2 = gearSchema.y1 + 15
        val equippedArrowCheck = ColorSchema.rescope(
            MyColors.UpgradeGearArrow7, arrowX1, arrowY1, arrowX2, arrowY2
        )
        findMultiColors(schema = equippedArrowCheck, increment = 1) ?: continue

        accountMessage("${heroEntry.displayName} 升级装备: ${Schema.getDisplayName(gearKey)}")

        // Tap the gear to open upgrade dialogue
        TouchActions.tap(gearPoint.x, gearPoint.y, delayTime = STEP_DELAY)

        // Confirm upgrade and exit gear detail using fixed coordinates
        val result = confirmAndExitGearDetail()
        if (result == UPGRADE_ALREADY_UPGRADING) {
            // AlreadyBeingUpgraded detected — stop all upgrades
            backToHeroHall()
            return upgraded
        }
        if (result == UPGRADE_MAX_LEVEL) {
            // Gear is at max level — skip to next gear
            accountMessage("${Schema.getDisplayName(gearKey)} 该装备已升到最高等级，跳过")
            continue
        }

        upgraded++
        @Suppress("UNUSED_VARIABLE")
        val currentProgress = heroUpgradedCount + upgraded
        accountMessage("成功升级 ${Schema.getDisplayName(gearKey)}")
    }

    return upgraded
}
