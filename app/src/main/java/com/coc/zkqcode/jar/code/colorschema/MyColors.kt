package com.coc.zkqcode.jar.code.colorschema

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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHelperColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors,
    IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors,
    IMainBaseResearchLevelColors by MainBaseResearchLevelColors, IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors,
    IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseHelperColors by MainBaseHelperColors, IMainBaseHeroHallColors by MainBaseHeroHallColors,
    IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors {
    // Main Base Magic Item Colors
    val MagicItemIcon = ColorSchema.parse(
        167, 502, 1124, 628, "2B2E66", "13|0|6FA7D9,26|0|89BEEB,38|0|B8D7D3,51|0|BEDDD8,0|21|282C60,13|21|9BD5FF,26|21|89BDEA,38|21|DF8442,51|21|FBA05D", 0, 0.9, "魔法道具图标"
    )
    val BuilderPotion = ColorSchema.parse(
        280, 170, 1000, 520, "E4B749", "13|3|C19832,13|18|C29734,39|-19|7DD7F6,36|-15|7DD7F6,34|-6|7DD7F5,2|-46|9AD7FF,8|-55|95D0FF,13|-51|92CCFD,13|-34|394B59", 0, 0.9, "工人药水"
    )
    val ResearchPotion = ColorSchema.parse(
        280, 170, 1000, 520, "430F49", "-16|-6|700D98,0|-17|68506C,17|-6|524440,26|-17|8FF3FF,28|-1|5C6B7F,-2|-58|94CFFF,-8|-2|580D67,-7|8|570D65,-15|-8|6B0D8F", 0, 0.9, "研究药水"
    )
}