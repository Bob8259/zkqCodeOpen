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
    val MagicItemIcon2 = ColorSchema.parse(
        190, 513, 1106, 631, "2E316C", "13|0|76ADDF,27|0|90C7F4,41|0|C1DEDB,54|0|868684,0|17|2E3163,13|17|93CFFC,27|17|8DC3F0,41|17|EF9553,54|17|FA9B56", 0, 0.9, "魔法物品按钮7按钮"
    )
    val BuilderPotion = ColorSchema.parse(
        280, 170, 1000, 520, "E4B749", "13|3|C19832,13|18|C29734,39|-19|7DD7F6,36|-15|7DD7F6,34|-6|7DD7F5,2|-46|9AD7FF,8|-55|95D0FF,13|-51|92CCFD,13|-34|394B59", 0, 0.9, "工人药水"
    )
    val ResearchPotion = ColorSchema.parse(
        280, 170, 1000, 520, "430F49", "-16|-6|700D98,0|-17|68506C,17|-6|524440,26|-17|8FF3FF,28|-1|5C6B7F,-2|-58|94CFFF,-8|-2|580D67,-7|8|570D65,-15|-8|6B0D8F", 0, 0.9, "研究药水"
    )
    val ResourcePotion = ColorSchema.parse(
        280, 170, 1000, 520, "F850DF", "10|11|F22FD2,3|15|F549D7,-7|2|FB5CE8,8|-14|FDEBF8,30|-11|A3FFFF,40|-5|71B7E7,31|1|629ECE,5|-52|90C9FB,17|-49|95CFFF", 0, 0.9, "资源药水"
    )
    val WallRings = ColorSchema.parse(
        280, 170, 1000, 520, "7CD2F5", "3|16|3F6A9C,-38|3|6197B6,-43|-20|63A4D6,-27|-26|4B80B6,-5|-30|476990,10|-23|665669,19|-15|5C8ABD,21|0|4577AB,14|18|3D5C86", 0, 0.9, "壁垒之戒"
    )
    val ClockTowerPotion = ColorSchema.parse(
        280, 170, 1000, 520, "FBCAE1", "-8|-2|FFDBE8,-14|-19|FFDAE7,-6|-19|FDCFE3,4|-6|F9C3DE,33|-15|8DEDFF,36|-9|7AD6F5,28|-13|3F5B7C,4|-56|94CFFF,12|-62|9AD7FF", 0, 0.9, "钟楼药水"
    )
    val UsePotionButton = ColorSchema.parse(
        750, 546, 978, 623, "75E4AF", "45|0|75E4AF,91|0|75E4AF,137|0|75E4AF,182|0|75E4AF,0|39|3AD48B,45|39|3AD48B,91|39|3AD48B,137|39|3AD48B,182|39|3AD48B", 0, 0.9
    )
    val SellPotionButton = ColorSchema.parse(
        299, 548, 530, 623, "221EF7", "46|0|221EF7,93|0|221EF7,139|0|221EF7,185|0|221EF7,0|37|221EF7,46|37|221EF7,93|37|221EF7,139|37|221EF7,185|37|221EF7", 0, 0.9
    )

    // Main Base Hero Hall Color
    val OpenClanCastle2 = ColorSchema.parse(
        174, 548, 1113, 628, "4778AD", "7|0|5FAFD5,14|0|3C4050,20|0|3DAFD1,27|0|4B7DB5,0|15|4374A9,7|15|297E9B,14|15|377590,20|15|78CEEB,27|15|4577AD", 0, 0.9, "领取宝库按钮10按钮"
    )
    val OpenClanCastle3 = ColorSchema.parse(
        173, 526, 1091, 628, "2F557B", "8|0|437A93,17|0|3297B2,25|0|44BFE7,33|0|19374A,0|19|406CA0,8|19|3698C3,17|19|1A191C,25|19|3799C3,33|19|406C9F", 0, 0.9, "领取宝库按钮8按钮"
    )
}