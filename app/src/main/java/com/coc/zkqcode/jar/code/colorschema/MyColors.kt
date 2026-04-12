package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.IClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseMagicItemColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResearchLevelColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseMagicItemColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchLevelColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseUpgradeColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors,
    IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors,
    IMainBaseResearchLevelColors by MainBaseResearchLevelColors, IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors,
    IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseHelperColors by MainBaseHelperColors, IMainBaseHeroHallColors by MainBaseHeroHallColors,
    IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors, IMainBaseMagicItemColors by MainBaseMagicItemColors {
    // UI Colors
    val BackToCamp = ColorSchema.parse(
        58, 597, 102, 644, "10135B", "9|0|80A3EA,18|0|6682B6,27|0|7FA2E6,36|0|293265,0|23|2540A8,9|23|718BFD,18|23|7996FE,27|23|86A6FE,36|23|637AAE", 0, 0.9, "回营"
    )
    val AgreeTerms = ColorSchema.parse(
        260, 470, 299, 509, "ACBAC2", "8|0|ACBAC2,16|0|ACBAC2,23|0|ACBAC2,31|0|ACBAC2,0|19|ACBAC2,8|19|ACBAC2,16|19|ACBAC2,23|19|ACBAC2,31|19|ACBAC2", 0, 0.9, "同意条款"
    )
    val AgreePrivacy = ColorSchema.parse(
        184, 598, 593, 653, "67E2BF", "82|0|67E2BF,208|5|5CDCB3,245|0|67E2BF,327|0|67E2BF,0|27|21B76E,82|27|21B76E,164|27|21B76E,245|27|21B76E,327|27|21B76E", 0, 0.9, "同意隐私"
    )
    val LogIn = ColorSchema.parse(
        663, 406, 879, 451, "FFFFFF", "-4|-8|261CE8,-1|-28|000000,27|-23|EDCD62,57|-16|E4C357,60|7|A77019,164|-23|EDCD62,182|-17|E6C459,178|4|B3791C,155|2|BB7E1E", 0, 0.9, "登录"
    )
    val SkinAd = ColorSchema.parse(
        1167, 39, 1203, 77, "6761B8", "7|0|FFFFFF,14|0|FFFFFF,21|0|FFFFFF,28|0|8C84FF,0|19|3D3D3D,7|19|F9F7F7,14|19|0D0D0D,21|19|F9F9F9,28|19|1511EB", 0, 0.9, "皮肤广告"
    )

    //Main Base Clan Colors
    val TroopConfigCastleIcon = ColorSchema.parse(
        0, 0, 0, 0, "4B596D", "3|0|6D7C8E,7|0|495568,11|0|485465,14|0|495565,0|11|282F40,3|11|1D2026,7|11|1F2023,11|11|232326,14|11|8E9BA6", 0, 0.9
    )

    // Clan Capital Colors
    val TrashBin = ColorSchema.parse(
        479, 275, 533, 332, "7B76FF", "11|0|FFFFFF,22|0|FFFFFF,33|0|FFFFFF,44|0|7C77FF,0|29|1814E3,11|29|EBEBEB,22|29|EEEEEE,33|29|D2D2D2,44|29|1814E3", 0, 0.9, "都城配兵垃圾桶"
    )
    val CapitalBarbarian = ColorSchema.parse(
        176, 424, 265, 517, "7CBAFE", "18|0|73B6EF,36|0|A097A5,53|0|0D14A4,71|0|6C87EC,0|47|3C3E61,18|47|818EC5,36|47|587FC0,53|47|6D9CEE,71|47|6B77A0", 0, 0.9, "都城野蛮人"
    )
    val CapitalCoin = ColorSchema.parse(
        1190, 18, 1264, 72, "E418BB", "-5|0|FF55F0,1|0|E418BB,5|0|D42FA6,3|-2|FB93DA,-9|-10|6CFFFF,-5|-9|68F4FF,3|-8|1BBDFA,-9|13|C53497,-20|8|0DD9FF", 0, 0.9, "都城币"
    )
    val ZeroAttackTimes = ColorSchema.parse(
        1165, 545, 1200, 580, "FFFFFF", "-5|0|2126CF,7|2|2127D2,14|1|FFFFFF,17|1|2126CF,5|14|2129DC,5|-12|221DBA,8|-12|221DBA,7|-8|FFFFFF,7|7|FFFFFF", 0, 0.9, "无进攻机会"
    )
    val RaidInformation = ColorSchema.parse(
        1130, 575, 1265, 705, "F2BF6C", "-1|17|EFBC6C,-1|35|E99D2F,-2|51|E4972E,89|0|F2BF6C,92|7|F2BE6C,89|45|E69A2F,90|63|DF922E,42|1|FFFFFF,43|11|FFFFFF", 0, 0.9, "蓝色突袭信息按钮"
    )
    val RaidMap = ColorSchema.parse(
        1135, 570, 1260, 705, "CCE4E5", "-3|21|C9E2E4,-6|48|AAC6CD,-6|66|A8C4CC,43|12|FFE5B4,83|-4|CDE5E6,87|11|CAE3E4,91|36|ACC8CF,91|45|ABC6CE,87|51|AAC6CD", 0, 0.9, "突袭地图按钮"
    )
    val RaidRedAttackButton = ColorSchema.parse(
        705, 619, 807, 658, "8380FF", "21|0|827FFF,41|0|8380FF,61|0|8280FF,82|0|827FFF,0|19|221EF7,21|19|221EF7,41|19|1D1AB9,61|19|221EF7,82|19|221EF7", 0, 0.9, "都城红色进攻按钮"
    )
}