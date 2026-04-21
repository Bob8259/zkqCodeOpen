package com.coc.zkqcode.jar.code.colorschema

import android.provider.CalendarContract
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.ClanCapitalTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital.IClanCapitalColors
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
    IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors, IMainBaseMagicItemColors by MainBaseMagicItemColors, IClanCapitalColors by ClanCapitalColors {
    //Clan War Colours
    val ClanWarInfo = ColorSchema.parse(
        1150, 581, 1244, 667, "EBFDFE", "30|-9|EBFDFE,15|-20|FFFFFF,5|-31|FFFFFF,-6|-43|FFFFFF,-22|-40|FFFFFF,-13|-27|FFFFFF,7|-33|FFFFFF,-17|-10|EBFDFE,24|-22|FFFFFF", 0, 0.9, "对战详细信息"
    )
    val OurTeam = ColorSchema.parse(
        201, 104, 730, 161, "F4F2EC", "-4|-6|F3EFE2,2|-7|FAF8F3,7|-3|F7F4EE,6|0|F5F3ED,-464|7|1C3D68,-456|-1|B89581,-450|-5|B28C74,-449|-5|B38C74,-445|-9|AF8971", 0, 0.9, "我方战队"
    )
    val MyPositionGreen = ColorSchema.parse(
        140, 185, 160, 625, "81CCB1", "4|0|81CCB1,6|1|81CCB1,10|2|81CCB1,9|5|81CCB1,3|5|81CCB1,-2|5|81CCB1,-2|8|80CBB0,2|7|80CBB0,4|5|81CCB1", 0, 0.97
    )
    val ClanWarSwordIcon = ColorSchema.parse(
        51, 398, 86, 426, "1D15ED", "3|-4|1D15ED,6|-6|1D15ED,10|-8|1D15ED,13|-1|FFFFFF,16|-4|FFFFFF,11|0|FFFFFF,16|8|1D15ED,18|4|1D15ED,20|2|1D15ED", 0, 0.9, "部落战剑标志"
    )
    val ClanWarAttack = ColorSchema.parse(
        750, 527, 874, 570, "51C6FF", "25|0|51C6FF,50|0|51C6FF,75|0|51C6FF,100|0|51C6FF,0|21|1356EE,25|21|1356EE,50|21|1356EE,75|21|1356EE,100|21|1356EE", 0, 0.9, "部落战橙色进攻"
    )
    val RecommendedOpponent = ColorSchema.parse(
        311, 232, 1092, 708, "2C98FD", "0|1|2D99FD,0|2|2C99FE,0|3|2C98FC,0|4|2C98FD,0|5|2D99FD,0|6|2C99FE,0|7|2C98FD,0|8|2C98FD,0|9|2C99FE", 0, 0.92, "推荐目标"
    )
}
