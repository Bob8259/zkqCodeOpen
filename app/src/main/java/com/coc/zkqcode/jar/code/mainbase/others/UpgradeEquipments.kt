package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.herohall.backToHeroHall
import com.coc.zkqcode.jar.code.mainbase.herohall.clickHeroHallBottom
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

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
    ),
)


suspend fun upgradeEquipmentsAction() {
    TouchActions.tap(685, 590, delayTime = STEP_DELAY)

    if (findMultiColorsUntil(schemas = listOf(MyColors.SmithOreIcon), duration = 2000) == null) {
        accountMessage("未找到铁匠铺标志，返回英雄殿堂")
        backToHeroHall()
        return
    }
    accountMessage("已进入铁匠铺")
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_EQUIPED_EQUIPMENTS.key)) {
        val upgradeArrow = findMultiColorsUntil(listOf(MyColors.EquipmentUpgradeArrow), 1500)
        if (upgradeArrow != null) {
            accountMessage("已找到可升级的穿戴装备")
            TouchActions.tap(upgradeArrow.x - 20, upgradeArrow.y + 20)
            TouchActions.tap(970, 610)//Upgrade
            TouchActions.tap(970, 610)
            clickHeroHallBottom(3)
        }
    }
    if (HERO_ENTRIES.any { hero -> hero.equipments.any { (key, _) -> getBooleanConfigRuntime(key) } }) {
        // Detect and upgrade one enabled equipment on the current page, then swipe
        // to the next page and perform the same detection again.
        tryUpgradeEnabledEquipment()
        TouchActions.swipe(1220, 510, -500, 510, delayAfter = 500)
        tryUpgradeEnabledEquipment()
    }
    backToHeroHall()
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

/**
 * Scans the current page for the first enabled equipment that has an upgrade arrow
 * nearby, and upgrades it. Only one equipment is upgraded per call.
 *
 * For each enabled equipment, its color schema is searched on the captured screen.
 * When found at (x, y), the upgrade arrow ([MyColors.EquipmentUpgradeArrow]) is
 * searched in the sub-region (x+15, y-50) to (x+50, y-15) via [ColorSchema.rescope].
 * If the arrow is present, the equipment is tapped and the upgrade sequence is
 * performed, then the function returns immediately.
 */
private suspend fun tryUpgradeEnabledEquipment() {
    clearNewEquipments()
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndRestart("tryUpgradeEnabledEquipment: Screen Capture Failed.")

    for (hero in HERO_ENTRIES) {
        for ((key, colorSchema) in hero.equipments) {
            if (!getBooleanConfigRuntime(key)) continue

            val name = colorSchema.name ?: key
            val equipment = findMultiColors(schema = colorSchema, byteBuffer = screenBuffer, increment = 1)

            if (equipment == null) {
                accountMessage("装备$name 找到状态:未找到,是否可升级:否")
                continue
            }

            val x = equipment.x
            val y = equipment.y
            // Narrow the upgrade-arrow search to the small area near the equipment icon
            val arrowSchema = ColorSchema.rescope(
                MyColors.EquipmentUpgradeArrow, x + 15, y - 50, x + 50, y - 15
            )

            val arrow = findMultiColors(schema = arrowSchema, byteBuffer = screenBuffer, increment = 1)
            val upgradeable = arrow != null
            accountMessage("装备$name 找到状态:已找到,是否可升级:${if (upgradeable) "是" else "否"}")

            if (!upgradeable) continue

            TouchActions.tap(x, y)
            TouchActions.tap(970, 610)//Upgrade
            TouchActions.tap(970, 610)
            clickHeroHallBottom(2)
            return
        }
    }
}