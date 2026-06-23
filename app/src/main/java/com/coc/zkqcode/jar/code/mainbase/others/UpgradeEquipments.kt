package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
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

private const val STEP_DELAY = 1000

private val HERO_ENTRIES = listOf(
    HeroGearEntry(
        heroKey = "barbarian_king", displayName = "野蛮人之王", tapX = 129, tapY = 395, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_PUPPET.key, MyColors.BarbarianKingPuppet),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_RAGE_VIAL.key, MyColors.BarbarianKingRageVial),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_EARTHQUAKE_BOOTS.key, MyColors.BarbarianKingEarthquakeBoots),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_HEALING_BEARD.key, MyColors.BarbarianKingHealingBeard),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_SPIKED_SOCCER.key, MyColors.BarbarianKingSpikedSoccer),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_GIANT_GAUNTLET.key, MyColors.BarbarianKingGiantGauntlet),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_SNAKE_BRACELET.key, MyColors.BarbarianKingSnakeBracelet),
            Pair(Schema.MAIN_BASE_GEARS.BARBARIAN_KING_STICK_HORSE.key, MyColors.BarbarianKingStickHorse),
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
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_ACTION_FIGURE.key, MyColors.ArcherQueenActionFigure),
            Pair(Schema.MAIN_BASE_GEARS.ARCHER_QUEEN_MONOLITH_ARROW.key, MyColors.ArcherQueenMonolithArrow),
        )
    ),
    HeroGearEntry(
        heroKey = "grand_warden", displayName = "大守护者", tapX = 238, tapY = 392, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_ETERNAL_TOME.key, MyColors.GrandWardenEternalTome),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_LIFE_GEM.key, MyColors.GrandWardenLifeGem),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_HEALING_TOME.key, MyColors.GrandWardenHealingTome),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_GIANT_FIREBALL.key, MyColors.GrandWardenGiantFireball),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_RAGE_GEM.key, MyColors.GrandWardenRageGem),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_LAVA_BALLOON_PUPPET.key, MyColors.GrandWardenLavaBalloonPuppet),
            Pair(Schema.MAIN_BASE_GEARS.GRAND_WARDEN_HEROIC_TORCH.key, MyColors.GrandWardenHeroicTorch),
        )
    ),
    HeroGearEntry(
        heroKey = "royal_champion", displayName = "飞盾战神", tapX = 280, tapY = 396, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_SEEKING_SHIELD.key, MyColors.RoyalChampionSeekingShield),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_HOG_PUPPET.key, MyColors.RoyalChampionHogPuppet),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_ROYAL_GEM.key, MyColors.RoyalChampionRoyalGem),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_SPEED_VIAL.key, MyColors.RoyalChampionSpeedVial),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_FROZEN_CRYSTAL.key, MyColors.RoyalChampionFrozenCrystal),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_ROCKET_SPEAR.key, MyColors.RoyalChampionRocketSpear),
            Pair(Schema.MAIN_BASE_GEARS.ROYAL_CHAMPION_ELECTRO_BOOTS.key, MyColors.RoyalChampionElectroBoots),
        )
    ),
    HeroGearEntry(
        heroKey = "dragon_duke", displayName = "飞龙公爵", tapX = 322, tapY = 395, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_FIRE_RING.key, MyColors.DragonDukeFireRing),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_SCALE_ARMOR.key, MyColors.DragonDukeScaleArmor),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_COMBUSTOR.key, MyColors.DragonDukeCombustor),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_LIGHTNING_FANG.key, MyColors.DragonDukeLightningFang),
            Pair(Schema.MAIN_BASE_GEARS.DRAGON_DUKE_ROCKET_PACK.key, MyColors.DragonDukeRocketPack),
        )
    ),
    HeroGearEntry(
        heroKey = "minion_prince", displayName = "亡灵王子", tapX = 202, tapY = 397, gears = listOf(
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_GUARDIAN_PUPPET.key, MyColors.MinionPrinceGuardianPuppet),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_IRON_PANTS.key, MyColors.MinionPrinceIronPants),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_NOBLE_DUMBBELL.key, MyColors.MinionPrinceNobleDumbbell),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_DARK_ORB.key, MyColors.MinionPrinceDarkOrb),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_DARK_CROWN.key, MyColors.MinionPrinceDarkCrown),
            Pair(Schema.MAIN_BASE_GEARS.MINION_PRINCE_METEOR_STAFF.key, MyColors.MinionPrinceMeteorStaff),
        )
    ),
)


suspend fun upgradeGearsAction() {
    accountMessage("开始升级装备流程")
    
    TouchActions.tap(685, 590, delayTime = STEP_DELAY)
    accountMessage("点击进入铁匠铺")

    if (findMultiColorsUntil(schemas = listOf(MyColors.SmithOreIcon), duration = 2000) == null) {
        accountMessage("未找到铁匠铺标志，返回英雄殿堂")
        backToHeroHall()
        return
    }
    accountMessage("确认已进入铁匠铺")

    backToHeroHall()
}