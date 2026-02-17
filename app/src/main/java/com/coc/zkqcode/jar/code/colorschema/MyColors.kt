package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
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
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors {
    val BuilderBaseGold: ColorSchema = ColorSchema.parse(
        1004,
        26,
        1026,
        60,
        "0DC0E7",
        "0|1|0DC0E7,0|2|0DC0E7,0|3|0DC0E7,0|4|0DC0E7,0|5|0DC0E7,0|6|0DC0E7,0|7|0DC0E7,0|8|0DC0E7,0|9|0DC0E7",
        0,
        0.98
    )
    val BuilderBaseExiler: ColorSchema = ColorSchema.parse(
        1008,
        93,
        1025,
        128,
        "C54579",
        "0|1|C54579,0|2|C54579,0|3|C54579,0|4|C54579,0|5|C54579,0|6|C54579,0|7|C54579,0|8|C54579,0|9|C54579",
        0,
        0.98
    )
}
