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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors,
    IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors {
    //Main Base Clan Colors
    val ClanChatIcon = ColorSchema.parse(
        39, 310, 66, 327, "FFFFFF", "5|0|FFFFFF,11|0|FFFFFF,16|0|FFFFFF,21|0|FFFFFF,0|9|FFFFFF,5|9|FFFFF9,11|9|E5E3DE,16|9|FFFFFF,21|9|FFFFFF", 0, 0.9, "部落聊天框"
    )
    val DonationButton = ColorSchema.parse(
        409, 79, 430, 642, "69E9C1", "3|0|69E9C1,5|0|69E9C1,7|0|69E9C1,10|0|69E9C1,0|10|2CCD84,3|10|2CCD84,5|10|2CCD84,7|10|2CCD84,10|10|2CCD84", 0, 0.9, "增援按钮"
    )
    val DonateSuperTroops = ColorSchema.parse(
        500, 50, 1180, 690, "0E0D81", "-40|-10|0E0D81,-40|-8|0E0D81,-40|-5|0E0D81,-40|-3|0E0D81,35|-15|0E0D81,35|-13|0E0D81,35|-12|0E0D81,35|-10|0E0D81,35|-9|0E0D81", 0, 0.97, "捐超级兵"
    )
    val DonateNormalTroops = ColorSchema.parse(
        500, 50, 1180, 690, "B87940", "-37|-6|B87940,-37|-8|B87940,-37|-10|B87940,-37|-14|B87940,38|-11|B87940,38|-13|B87940,38|-15|B87940,38|-17|B87940,37|-14|B87940", 0, 0.97, "捐普通兵"
    )
    val DonateSpells = ColorSchema.parse(
        500, 50, 1180, 690, "C1476F", "-38|-5|C1476F,-38|-8|C1476F,-38|-10|C1476F,-38|-12|C1476F,36|-6|C1476F,36|-9|C1476F,36|-10|C1476F,36|-12|C1476F,36|-14|C1476F", 0, 0.97, "捐法术"
    )
    val PreviousDonation = ColorSchema.parse(
        417, 64, 479, 118, "FFFFFF", "-6|0|12CE97,-6|3|13C78D,-6|8|18A761,7|10|18A661,7|7|16B979,7|3|13C78D,7|2|13CA91,7|1|13CC94,7|0|12CE97", 0, 0.9, "上一个捐赠"
    )

}