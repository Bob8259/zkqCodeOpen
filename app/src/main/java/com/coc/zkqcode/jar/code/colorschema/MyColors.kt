package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors,
    IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors,
    IMainBaseResearchLevelColors by MainBaseResearchLevelColors, IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors,
    IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseHeroHallColors by MainBaseHeroHallColors, IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors {
    //Main Base Helper Colors
    val ResearchHelperIcon = ColorSchema.parse(
        171, 492, 1054, 631, "353F5C", "5|0|48547E,11|0|374570,16|0|333F5F,21|0|6686CF,0|21|CEBFC8,5|21|32416B,11|21|6E89BC,16|21|435379,21|21|4E669E", 0, 0.9, "研究助手外部按钮"
    )
    val BuilderHelperIcon = ColorSchema.parse(
        171, 492, 1054, 631, "6FB9F3", "6|2|5A9DD2,16|4|9EA4B5,24|5|6EB1E8,25|17|2A4AA0,17|24|85A0ED,12|18|ABD4FF,7|29|88B2EF,10|32|8FBCF0,21|32|95C3F5", 0, 0.9, "工人助手外部按钮"
    )
    val InnerResearchHelper = ColorSchema.parse(
        134, 566, 571, 659, "2F344B", "6|0|465681,13|0|2F344B,19|0|333C55,25|0|36405E,0|26|7B7486,6|26|2D364E,13|26|4F679D,19|26|53679A,25|26|516596", 0, 0.9, "里面的实验助手"
    )
    val InnerBuilderHelper = ColorSchema.parse(
        126, 557, 581, 662, "3896FF", "6|7|368FFF,17|25|A0E9FF,7|35|ABEEFF,9|21|B0F6FF,29|49|5779AB,37|46|524A4D,33|50|5D80B5,30|50|5071A6,33|48|4F74AE", 0, 0.9, "里面的建筑助手"
    )
    val LockedResearchHelper = ColorSchema.parse(
        134, 566, 571, 659, "30324E", "11|0|121828,23|9|6B83B3,9|12|576FA8,1|13|657FB7,30|44|0EC3FD,40|46|0E243A,51|42|0EC2FD,51|49|0EC0FB,31|53|0EC1FC", 0, 0.9, "锁住的实验助手"
    )

    val LockedBuilderHelper = ColorSchema.parse(
        126, 557, 581, 662, "78AAFF", "2|17|96C4FF,-9|11|788CDD,-2|-14|326DE3,19|3|375BC9,13|22|AFF1FF,28|31|0EC1FC,37|34|0E243A,47|31|0EC0FC,47|37|0EBEFB", 0, 0.9, "锁住的工人学徒"
    )
    val AssignHelper = ColorSchema.parse(
        1031, 284, 1126, 314, "89E9BC", "19|0|89E9BC,38|0|6EB494,57|0|89E9BC,76|0|89E9BC,0|15|3AD48B,19|15|3AD48B,67|14|3AD48B,57|15|3AD48B,76|15|3AD48B", 0, 0.9, "指派助手"
    )
}