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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial,
    IMainBaseTraining by MainBaseTraining, IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors,
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors,
    IBuilderBaseAttackColors by BuilderBaseAttackColors {
    //The followings are Builder Base Attack colors
    val BuilderBaseBarbarian: ColorSchema = ColorSchema.parse(
        193, 585, 1261, 623, "FF763A", "7|0|FF763A,15|0|FF773B,23|0|FF783C,30|0|FF793C,0|6|FF773A,7|6|FF793C,15|6|FF7B3E,23|6|FF7D40,30|6|FF7E41", 0, 0.9, "夜世界野蛮人"
    )

    //The followings are Universal Upgrade Colors
    val UpgradeGemIcon: ColorSchema = ColorSchema.parse(
        189, 501, 1117, 625, "7AF5D9", "6|0|81F9DE,12|0|8AFCE4,17|0|92FFEB,23|0|9CFFF1,0|13|8FFBDC,6|13|81F8D2,12|13|78F8CF,17|13|78FBD5,23|13|67E3BF", 0, 0.93, "升级宝石标志"
    )
    val UpgradeGemIcon2: ColorSchema = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志2"
    )
    val UpgradeGemIcon3: ColorSchema = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志8按钮"
    )
}