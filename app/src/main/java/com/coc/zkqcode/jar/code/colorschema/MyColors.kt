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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors,
    IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseHeroHallColors by MainBaseHeroHallColors,
    IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors {
    //Hero Hall Colors, related to clan castle
    val OpenClanCastle = ColorSchema.parse(
        196, 509, 1082, 628, "497CB2", "9|0|6DC7E5,18|0|3D404F,27|0|3DAFD1,36|0|4B7DB6,0|21|4579AE,9|21|3699C7,18|21|141A20,27|21|86D9F3,36|21|4678AF", 0, 0.9, "检测宝库"
    )

    //Night Base Clock Tower
    val ClockTower = ColorSchema.parse(
        221, 491, 1078, 633, "FFFBFF", "5|0|FFF1FF,10|0|0EB0D7,15|0|FFF0FF,20|0|FFFFFF,0|13|FFF4FF,5|13|FFFCFF,10|13|131314,15|13|FEEFFE,20|13|FFF8FF", 0, 0.9, "免费提速"
    )

    // Main Base Collect Resource Colors
    val MainBaseCollectElixir1 = ColorSchema.parse(
        115, 85, 1130, 610, "D55BE1", "-4|-3|CD40D8,0|-4|D750E7,3|-7|E138F0,5|-2|E061ED,0|1|D458DD,-3|1|C74BCC,-12|-5|AAD5CF,-10|-10|B8DBD7,11|4|89C3BB", 0, 0.92
    )
    val MainBaseCollectElixir2 = ColorSchema.parse(
        115, 85, 1130, 610, "D254E1", "0|-2|D55BE4,-2|-8|DF3DEE,2|-5|FFFEFF,4|-3|D840ED,-2|-1|D458DE,-3|-5|D65FE9,-13|-13|B8DCD7,10|-11|B6DBD6,10|0|91C8C0", 0, 0.92
    )
    val MainBaseCollectGold1 = ColorSchema.parse(
        115, 85, 1130, 610, "49ECFF", "-3|0|4AE4FF,0|-2|4AF6FF,4|0|18E3FF,3|5|06C1FF,0|5|03BCF5,-4|4|48DAFF,12|12|7FBEB5,13|-7|B9DCD7,-9|-7|B9DCD7", 0, 0.92
    )
    val MainBaseCollectGold2 = ColorSchema.parse(
        115, 85, 1130, 610, "0DD0FF", "1|2|09C8FF,-3|0|4AE3FF,-3|-3|49EFFF,2|-3|1AE9FF,3|0|16DCFF,-3|4|03BAF2,-13|-8|B9DCD7,10|-9|B9DCD7,10|9|83C0B7", 0, 0.92
    )

}