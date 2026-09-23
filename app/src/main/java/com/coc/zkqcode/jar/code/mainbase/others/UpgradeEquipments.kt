package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.herohall.backToHeroHall
import com.coc.zkqcode.jar.code.mainbase.herohall.clickHeroHallBottom
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.MainBaseEquipmentPriorities

data class HeroEquipmentEntry(
    val heroKey: String, val displayName: String, val tapX: Int, val tapY: Int, val equipments: List<Pair<String, ColorSchema>>
)

private const val STEP_DELAY = 1000

private val HERO_ENTRIES = listOf(
    HeroEquipmentEntry(
        heroKey = "barbarian_king", displayName = "野蛮人之王", tapX = 129, tapY = 395, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_PUPPET.key, MyColors.BarbarianKingBarbarianPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_RAGE_VIAL.key, MyColors.BarbarianKingRageVial),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_EARTHQUAKE_BOOTS.key, MyColors.BarbarianKingEarthquakeBoots),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_HEALING_BEARD.key, MyColors.BarbarianKingVampstache),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_GIANT_GAUNTLET.key, MyColors.BarbarianKingGiantGauntlet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_SPIKED_SOCCER.key, MyColors.BarbarianKingSpikyBall),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_SNAKE_BRACELET.key, MyColors.BarbarianKingSnakeBracelet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_STICK_HORSE.key, MyColors.BarbarianKingStickHorse),
        )
    ),
    HeroEquipmentEntry(
        heroKey = "archer_queen", displayName = "弓箭女皇", tapX = 170, tapY = 391, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_PUPPET.key, MyColors.ArcherQueenArcherPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_CLOAK.key, MyColors.ArcherQueenInvisibilityVial),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_GIANT_ARROW.key, MyColors.ArcherQueenGiantArrow),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_HEALER_PUPPET.key, MyColors.ArcherQueenHealerPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_FROST_ARROW.key, MyColors.ArcherQueenFrozenArrow),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_CLONE_MIRROR.key, MyColors.ArcherQueenMagicMirror),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_ACTION_FIGURE.key, MyColors.ArcherQueenActionFigure),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_MONOLITH_ARROW.key, MyColors.ArcherQueenMonolithArrow),
        )
    ),
    HeroEquipmentEntry(
        heroKey = "minion_prince", displayName = "亡灵王子", tapX = 202, tapY = 397, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_GUARDIAN_PUPPET.key, MyColors.MinionPrinceHenchmenPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_DARK_ORB.key, MyColors.MinionPrinceDarkOrb),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_IRON_PANTS.key, MyColors.MinionPrinceMetalPants),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_NOBLE_DUMBBELL.key, MyColors.MinionPrinceNobleIron),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_DARK_CROWN.key, MyColors.MinionPrinceDarkCrown),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_METEOR_STAFF.key, MyColors.MinionPrinceMeteorStaff),
        )
    ),
    HeroEquipmentEntry(
        heroKey = "grand_warden", displayName = "大守护者", tapX = 238, tapY = 392, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_ETERNAL_TOME.key, MyColors.GrandWardenEternalTome),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_LIFE_GEM.key, MyColors.GrandWardenLifeGem),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_RAGE_GEM.key, MyColors.GrandWardenRageGem),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_HEALING_TOME.key, MyColors.GrandWardenHealingTome),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_GIANT_FIREBALL.key, MyColors.GrandWardenFireball),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_LAVA_BALLOON_PUPPET.key, MyColors.GrandWardenLavaloonPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.GRAND_WARDEN_HEROIC_TORCH.key, MyColors.GrandWardenHeroicTorch),
        )
    ),
    HeroEquipmentEntry(
        heroKey = "royal_champion", displayName = "飞盾战神", tapX = 280, tapY = 396, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_SEEKING_SHIELD.key, MyColors.RoyalChampionSeekingShield),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_ROYAL_GEM.key, MyColors.RoyalChampionRoyalGem),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_HOG_PUPPET.key, MyColors.RoyalChampionHogRiderPuppet),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_SPEED_VIAL.key, MyColors.RoyalChampionHasteVial),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_ROCKET_SPEAR.key, MyColors.RoyalChampionRocketSpear),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_ELECTRO_BOOTS.key, MyColors.RoyalChampionElectroBoots),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.ROYAL_CHAMPION_FROZEN_CRYSTAL.key, MyColors.RoyalChampionFrostFlake),
        )
    ),
    HeroEquipmentEntry(
        heroKey = "dragon_duke", displayName = "飞龙公爵", tapX = 322, tapY = 395, equipments = listOf(
            Pair(Schema.MAIN_BASE_EQUIPMENTS.DRAGON_DUKE_FIRE_RING.key, MyColors.DragonDukeFireHeart),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.DRAGON_DUKE_COMBUSTOR.key, MyColors.DragonDukeFlameBlower),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.DRAGON_DUKE_SCALE_ARMOR.key, MyColors.DragonDukeStunBlaster),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.DRAGON_DUKE_LIGHTNING_FANG.key, MyColors.DragonDukeElectroFangs),
            Pair(Schema.MAIN_BASE_EQUIPMENTS.DRAGON_DUKE_ROCKET_PACK.key, MyColors.DragonDukeRocketBackpack),
        )
    )
)

private val EQUIPPED_EQUIPMENT_SCHEMAS = mapOf(
    Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_PUPPET.key to MyColors.BarbarianKingBarbarianPuppetEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_RAGE_VIAL.key to MyColors.BarbarianKingRageVialEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_EARTHQUAKE_BOOTS.key to MyColors.BarbarianKingEarthquakeBootsEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_GIANT_GAUNTLET.key to MyColors.BarbarianKingGiantGauntletEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.BARBARIAN_KING_SNAKE_BRACELET.key to MyColors.BarbarianKingSnakeBraceletEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_PUPPET.key to MyColors.ArcherQueenArcherPuppetEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_CLOAK.key to MyColors.ArcherQueenInvisibilityVialEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.ARCHER_QUEEN_GIANT_ARROW.key to MyColors.ArcherQueenGiantArrowEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_GUARDIAN_PUPPET.key to MyColors.MinionPrinceHenchmenPuppetEquipped,
    Schema.MAIN_BASE_EQUIPMENTS.MINION_PRINCE_DARK_ORB.key to MyColors.MinionPrinceDarkOrbEquipped
)


private enum class EquipmentUpgradeMode {
    QUICK,
    BY_HERO
}

suspend fun upgradeEquipmentsAction() {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS.key)) return

    val mode = equipmentUpgradeMode()
    val shouldUpgradeEquipped = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPED_EQUIPMENTS.key)
    if (mode == null && !shouldUpgradeEquipped) return

    TouchActions.tap(685, 590, delayTime = STEP_DELAY)

    if (findMultiColorsUntil(schemas = listOf(MyColors.SmithOreIcon), duration = 2000) == null) {
        accountMessage("未找到铁匠铺标志，返回英雄殿堂")
        backToHeroHall()
        return
    }
    accountMessage("已进入铁匠铺")
    if (shouldUpgradeEquipped && mode != EquipmentUpgradeMode.BY_HERO) {
        upgradeEquippedEquipmentsQuickly()
    }

    if (mode != null && HERO_ENTRIES.any { orderedEnabledEquipments(it).isNotEmpty() }) {
        when (mode) {
            EquipmentUpgradeMode.QUICK -> upgradeEquipmentsQuickly()
            EquipmentUpgradeMode.BY_HERO -> upgradeEquipmentsByHero()
        }
    }
    accountMessage("升级装备完成")
    backToHeroHall()
}

private fun equipmentUpgradeMode(): EquipmentUpgradeMode? {
    val quickEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS_QUICK.key)
    val heroEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS_BY_HERO.key)
    return when {
        heroEnabled -> EquipmentUpgradeMode.BY_HERO
        quickEnabled -> EquipmentUpgradeMode.QUICK
        else -> null
    }
}

private suspend fun upgradeEquipmentsQuickly() {
    tryUpgradeEnabledEquipment(HERO_ENTRIES)
    TouchActions.swipe(1120, 510, -600, 510, delayAfter = 500)
    tryUpgradeEnabledEquipment(HERO_ENTRIES)
}

private suspend fun upgradeEquipmentsByHero() {
    val strictPriority = getBooleanConfigRuntime(
        Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS_STRICT_PRIORITY.key
    )
    for (hero in HERO_ENTRIES) {
        if (orderedEnabledEquipments(hero).isEmpty()) continue

        accountMessage("按英雄升级装备：正在检测${hero.displayName}")
        TouchActions.tap(hero.tapX, hero.tapY, delayTime = STEP_DELAY)
        val upgraded = tryUpgradeEnabledEquipment(
            heroes = listOf(hero),
            ignoreUpgradeArrow = true,
            strictPriority = strictPriority
        )
        if (!upgraded && strictPriority) {
            accountMessage("严格优先级：${hero.displayName}最高优先级装备未找到，跳过后续装备升级")
            return
        }
    }
}

private suspend fun upgradeEquippedEquipmentsQuickly() {
    tryUpgradeEquippedEquipment(HERO_ENTRIES)
    TouchActions.swipe(1120, 510, -600, 510, delayAfter = 500)
    tryUpgradeEquippedEquipment(HERO_ENTRIES)
}

private fun orderedEnabledEquipments(hero: HeroEquipmentEntry): List<Pair<String, ColorSchema>> =
    hero.equipments
        .filter { (key, _) -> getBooleanConfigRuntime(key) }
        .sortedBy { (key, _) ->
            getConfigRuntime(MainBaseEquipmentPriorities.keyFor(key)).toIntOrNull() ?: Int.MAX_VALUE
        }

private suspend fun clearNewEquipments() {
    repeat(30) {
        val newEquipment = findMultiColorsUntil(listOf(MyColors.NewEquipment), 200)
        if (newEquipment != null) {
            TouchActions.tap(newEquipment.x, newEquipment.y)
            clickHeroHallBottom(2)
        } else {
            return
        }
    }
}

private suspend fun performEquipmentUpgrade(x: Int, y: Int) {
    TouchActions.tap(x, y)
    TouchActions.tap(970, 610)
    TouchActions.tap(970, 610)
    clickHeroHallBottom(2)
}

/**
 * Scans enabled equipment in configured order and upgrades the first usable item.
 *
 * The normal and equipped colour schemas identify the same equipment. When
 * [ignoreUpgradeArrow] is true, a matched equipment is upgraded directly; this
 * is used by hero mode. Quick mode keeps the upgrade-arrow check.
 *
 * When [strictPriority] is true, failure to match the current highest-priority
 * equipment stops the scan instead of falling through to lower priorities.
 */
private suspend fun tryUpgradeEnabledEquipment(
    heroes: List<HeroEquipmentEntry>,
    ignoreUpgradeArrow: Boolean = false,
    strictPriority: Boolean = false
): Boolean {
    clearNewEquipments()
    val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(
        asBitmap = false,
        errorMessage = "tryUpgradeEnabledEquipment: Screen Capture Failed."
    )

    for (hero in heroes) {
        for ((key, colorSchema) in orderedEnabledEquipments(hero)) {
            val name = colorSchema.name ?: key
            val equippedEquipment = EQUIPPED_EQUIPMENT_SCHEMAS[key]?.let { schema ->
                findMultiColors(schema = schema, byteBuffer = screenBuffer, increment = 1)
            }
            val equipment = equippedEquipment
                ?: findMultiColors(schema = colorSchema, byteBuffer = screenBuffer, increment = 1)

            if (equipment == null) {
                accountMessage("装备$name 找到状态:未找到,是否可升级:否")
                if (strictPriority) return false
                continue
            }

            if (ignoreUpgradeArrow) {
                accountMessage("装备$name 已找到，按英雄优先级直接执行升级")
                performEquipmentUpgrade(equipment.x, equipment.y)
                return true
            }

            val arrowSchema = ColorSchema.rescope(
                MyColors.EquipmentUpgradeArrow, equipment.x + 15, equipment.y - 50, equipment.x + 50, equipment.y - 15
            )
            val upgradeable = findMultiColors(schema = arrowSchema, byteBuffer = screenBuffer, increment = 1) != null
            accountMessage("装备$name 找到状态:已找到,是否可升级:${if (upgradeable) "是" else "否"}")

            if (!upgradeable) {
                if (strictPriority) return false
                continue
            }

            performEquipmentUpgrade(equipment.x, equipment.y)
            return true
        }
    }
    return false
}

private suspend fun tryUpgradeEquippedEquipment(heroes: List<HeroEquipmentEntry>) {
    clearNewEquipments()
    val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(
        asBitmap = false,
        errorMessage = "tryUpgradeEquippedEquipment: Screen Capture Failed."
    )

    for (hero in heroes) {
        for ((key, colorSchema) in orderedEnabledEquipments(hero)) {
            val equippedEquipment = EQUIPPED_EQUIPMENT_SCHEMAS[key]?.let { schema ->
                findMultiColors(schema = schema, byteBuffer = screenBuffer, increment = 1)
            }
            val equipment = equippedEquipment
                ?: findMultiColors(schema = colorSchema, byteBuffer = screenBuffer, increment = 1)
                ?: continue
            val arrowSchema = ColorSchema.rescope(
                MyColors.EquipmentUpgradeArrow, equipment.x + 15, equipment.y - 50, equipment.x + 50, equipment.y - 15
            )
            if (findMultiColors(schema = arrowSchema, byteBuffer = screenBuffer, increment = 1) == null) continue

            accountMessage("已找到可升级的穿戴装备：${colorSchema.name ?: key}")
            performEquipmentUpgrade(equipment.x, equipment.y)
            return
        }
    }
}