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
    //The followings are Builder Base Attack colors
    val BuilderBaseGold: ColorSchema = ColorSchema.parse(
        1004, 26, 1026, 60, "0DC0E7", "0|1|0DC0E7,0|2|0DC0E7,0|3|0DC0E7,0|4|0DC0E7,0|5|0DC0E7,0|6|0DC0E7,0|7|0DC0E7,0|8|0DC0E7,0|9|0DC0E7", 0, 0.98
    )
    val BuilderBaseExiler: ColorSchema = ColorSchema.parse(
        1008, 93, 1025, 128, "C54579", "0|1|C54579,0|2|C54579,0|3|C54579,0|4|C54579,0|5|C54579,0|6|C54579,0|7|C54579,0|8|C54579,0|9|C54579", 0, 0.98
    )
    val AttackNow: ColorSchema = ColorSchema.parse(
        825, 431, 1075, 518, "86E9BA", "29|1|85E8B9,64|0|86E9BA,120|-2|89E9BC,158|-5|8CEABE,14|51|3AD48B,54|46|3AD48B,91|51|3AD48B,158|53|3AD38B,188|46|3AD48B", 0, 0.9, "夜世界立即进攻"
    )
    val TrainTroopsWarning: ColorSchema = ColorSchema.parse(
        491, 198, 596, 216, "2021FE", "1|0|2122FE,5|0|2122FE,9|0|2021FE,9|3|3335F6,6|3|3335F6,3|3|292AFC,1|3|3435F6,0|3|3335F6,-1|3|3335F6", 0, 0.95, "练兵警告"
    )
    val CancelAttackSearch: ColorSchema = ColorSchema.parse(
        569, 611, 714, 649, "7D77FE", "29|0|7D77FE,71|-3|8079FF,87|0|7D77FE,116|0|7D77FE,0|19|110FDB,29|19|110FDB,58|19|110FDB,87|19|110FDB,116|19|110FDB", 0, 0.9, "取消搜索"
    )
    val SwitchTroopButton: ColorSchema = ColorSchema.parse(
        116, 663, 618, 709, "F3F3F3", "2|0|F3F3F3,9|-4|86E0B4,20|-2|F4F4F4,20|6|E2DEDC,8|14|22A565,-7|13|23AA67,0|-1|F3F3F3,9|-4|86E0B4,9|-6|86E0B4", 0, 0.9, "切换部队标志"
    )
}
