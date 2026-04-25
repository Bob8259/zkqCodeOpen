package com.coc.zkqcode.jar.code.colorschema

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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.IClanCapitalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.IClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseClanWarColors
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanWarColors
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
    IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseClanWarColors by MainBaseClanWarColors, IMainBaseHelperColors by MainBaseHelperColors,
    IMainBaseHeroHallColors by MainBaseHeroHallColors, IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors, IMainBaseMagicItemColors by MainBaseMagicItemColors, IClanCapitalColors by ClanCapitalColors {
    //Clan Game Colours
    val Calendar = ColorSchema.parse(
        175, 630, 475, 7055, "5678E1", "6|0|4770D3,12|0|9EB1C8,17|0|ECF1F8,23|0|FFFFFF,0|12|FFFFFF,6|12|FFFFFF,12|12|3620B1,17|12|B5AFF1,23|12|FFFFFF", 0, 0.9, "日历标志"
    )
    val OpenClanGame = ColorSchema.parse(
        200, 85, 1075, 690, "7AF1D2", "70|-2|7FF3D7,93|0|7AF1D2,-562|-76|174AF1,-516|-58|1F41B8,-419|-81|C7974C,-415|-10|CD9D50,-136|9|FFBB9E,-18|-33|FFBDA5,82|-33|FFBDAB", 0, 0.9, "打开部落竞赛"
    )
    val FinishedTask = ColorSchema.parse(
        597, 273, 601, 286, "6ADDFA", "1|0|6ADDFA,2|0|6ADDFA,3|0|6ADDFA,4|0|40C8EA,0|7|40C8EA,1|7|40C8EA,2|7|40C8EA,3|7|40C8EA,4|7|40C8EA", 0, 0.9, "已完成的竞赛"
    )
    val NormalTasks = ColorSchema.parse(
        638, 258, 1078, 305, "52E053", "8|4|52E053,18|16|52E053,10|11|52E053,14|-1|52E053,14|-1|52E053,14|12|52E053,26|9|52E053,20|10|52E053,2|1|52E053", 0, 0.9, "竞赛页面普通任务"
    )
    val OnGoingTask = ColorSchema.parse(
        478, 127, 505, 304, "44CCEE", "2|16|50D8FA,2|24|4DD5F7,2|36|44CDEF,2|43|43CBED,2|49|42CAEC,2|53|43CCEE,2|59|44CDEF,1|65|46CEF0,-1|68|45CEF0", 0, 0.9, "进行中的任务"
    )
    val BuilderBaseClanGame = ColorSchema.parse(
        0, 0, 0, 0, "825B0D", "1|0|825B0D,2|0|825B0D,3|0|825B0D,4|0|825B0D,5|0|815B0D,6|0|815B0D,7|0|825B0D,8|0|815A0D,9|0|815A0D", 0, 0.96, "夜世界任务"
    )
    val MainBaseClanGame = ColorSchema.parse(
        0, 0, 0, 0, "D2925C", "1|0|D1915C,2|0|D1915C,3|0|D1925C,4|0|D1925C,5|0|D1915C,6|0|D1915C,7|0|D1925C,8|0|D1925C,9|0|D2925D", 0, 0.96, "主世界任务"
    )

    //Gear Up Colours
    val UpgradeListGearUpIcon = ColorSchema.parse(
        0, 0, 0, 0, "E2E1E3", "-5|0|ACA09B,-5|-6|D5D4D8,1|-7|7E7D7E,3|-11|D4D0D3,8|-11|B9B2B4,7|-14|B8B5BA,11|-11|C1BEC2,11|-7|CCCACD,9|-2|988E87", 0, 0.9, "改装标志"
    )
    val BuildingGearUpButton = ColorSchema.parse(
        180, 500, 1180, 635, "AB9C99", "1|-12|D7D4D8,23|-28|BCB9BE,33|-21|C4C2C6,31|-2|8B827D,21|4|7F7C81,5|4|81746D,1|-6|DAD8DB,13|-15|7E7D7E,22|-17|A3978E", 0, 0.9, "改装建筑按钮"
    )
}
