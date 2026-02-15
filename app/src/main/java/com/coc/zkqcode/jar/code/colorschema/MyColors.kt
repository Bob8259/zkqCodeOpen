package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseObstaclesRemovalColors
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
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors {
    // The followings are Builder Base Research Colors
    val ResearchIcon: ColorSchema = ColorSchema.parse(
        320, 12, 959, 73, "F727C7", "5|0|F300B1,10|0|F006A4,14|0|ED0898,19|0|E00786,0|8|F21DAE,5|8|A1E6EE,10|8|657FAE,14|8|FFFFFF,19|8|FFFFFF", 0, 0.9, "研究标志"
    )
}
