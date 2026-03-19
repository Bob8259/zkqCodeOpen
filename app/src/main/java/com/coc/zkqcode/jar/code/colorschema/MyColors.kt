package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseAttackColors
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

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors,
    IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors {

    // Builder Base Attack Color
    val TroopsWithSkills = ColorSchema.parse(
        195, 570, 1210, 630, "FE41C8", "7|0|FE41C8,14|0|FE41C8,21|0|FE41C8,28|0|FE41C8,0|9|FF74D4,7|9|FF74D4,14|9|FF74D4,21|9|FF74D4,28|9|FF74D4", 0, 0.9, "带技能部队"
    )
    val TroopsWithOutSkills = ColorSchema.parse(
        195, 570, 1210, 630, "FF763A", "7|0|FF763A,17|1|FF763A,22|2|FF773B,26|2|FF773B,24|8|FF7C3F,16|12|FF7E41,8|13|FF7C3F,3|12|FF793C,1|9|FF773A", 0, 0.9, "无技能部队"
    )

    //Main Base Upgrade Colors
    val smallElixirUpgradeIcon = ColorSchema.parse(
        0, 0, 0, 0, "FF60FF", "-2|1|FF2DEB,-3|2|FF41D2,-1|3|FF22D3,0|5|DF21B2,2|5|F221C2,2|4|FF22D9,3|4|FF23DD,3|3|FF26EC,3|2|FF39FF", 0, 0.9
    )

    //Universal Upgrade Colors
    val UpgradeWallCrossMark = ColorSchema.parse(
        208, 503, 1130, 627, "FFFFFF", "16|0|0D0D0D,1|-16|0D0D0D,1|15|0D0D0D,-15|0|0D0D0D,-5|-6|0D0D0D,6|-6|0D0D0D,6|5|0D0D0D,-5|5|0D0D0D,0|9|FFFFFF", 0, 0.9,
    )
    val UpgradeWallGreenCrossMark = ColorSchema.parse(
        208, 503, 1130, 627, "12E98F", "2|0|12E98F,2|-4|12E98F,1|3|12E98F,12|-7|12E98F,13|-5|12E98F,13|-3|12E98F,12|0|12E98F,12|2|12E98F,13|4|12E98F", 1, 0.9,
    )
    val DoubleHammer = ColorSchema.parse(
        208, 503, 1130, 627, "DFDFEC", "6|16|5581EE,12|28|5F84ED,28|0|E1E1EE,40|-9|D1D1DF,37|12|4D76DD,44|21|6085EF,-9|7|D1E0F4,6|-7|D2D1E0,8|-2|DBDEEE", 0, 0.9, "双锤子"
    )
}

