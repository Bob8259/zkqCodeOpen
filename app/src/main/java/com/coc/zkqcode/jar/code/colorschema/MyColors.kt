package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseUpgradeColors
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

}