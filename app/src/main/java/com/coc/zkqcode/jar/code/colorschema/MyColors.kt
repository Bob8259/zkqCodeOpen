package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResearchLevelColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchLevelColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.IClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseMagicItemColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseMagicItemColors

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
}