package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.ClanGameColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IClanGameColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UniversalUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseGearUpColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResearchColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.BuilderBaseUpgradeColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseAttackColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseClockTowerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase.IBuilderBaseGearUpColors
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseShopColors
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseShopColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseSmallTutorialColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseUpgradeColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IClanGameColors by ClanGameColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial,
    IMainBaseTraining by MainBaseTraining, IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors,
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseGearUpColors by BuilderBaseGearUpColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors,
    IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors,
    IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors,
    IMainBaseClanWarColors by MainBaseClanWarColors, IMainBaseHelperColors by MainBaseHelperColors, IMainBaseHeroHallColors by MainBaseHeroHallColors, IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors,
    IMainBaseMagicItemColors by MainBaseMagicItemColors, IClanCapitalColors by ClanCapitalColors, IMainBaseShopColors by MainBaseShopColors {
    //Place Item Colours
    val ItemsToBePlaced = ColorSchema.parse(
        563, 461, 719, 496, "B7FEFF", "0|1|B7FEFF,0|2|B7FEFF,5|12|B7FEFE,5|13|B7FEFE,-24|10|B7FFFF,-117|8|B4FBFB,-115|8|B4FBFC,-112|8|B7FEFE,-110|8|B7FFFF", 0, 0.95, "待放置的物品"
    )

    //Builder Base Tutorial Colours
    val SailUP = ColorSchema.parse(
        691, 530, 726, 578, "3F41B7", "7|0|C4DEF2,14|0|3E41B3,21|0|CBE2F4,28|0|4143AA,0|24|4B6A9C,7|24|5D80AB,14|24|756D74,21|24|776A6B,28|24|9F9591", 0, 0.9, "启航"
    )
    val BuilderMaster2 = ColorSchema.parse(
        978, 484, 1126, 690, "143177", "29|0|123279,59|0|1C4593,89|0|3E537F,118|0|3A4E79,0|103|0E1D41,29|103|102045,59|103|273F70,89|103|08122F,118|103|79522B", 0, 0.9, "睡觉建筑大师2"
    )

    //Clan Capital
    val StartRaid = ColorSchema.parse(
        1151, 592, 1254, 692, "64D4B4", "4|16|64D2B1,4|47|28BC8D,40|51|28BB8D,72|48|28BC8D,82|45|28BD8E,84|25|28C192,84|11|64D3B2,83|6|64D4B3,84|-3|64D5B5", 0, 0.9, "发起突袭"
    )

    //MainBaseAttackColors
    val SetBaseIcon2 = ColorSchema.parse(
        509, 133, 580, 181, "7ED3A3", "3|3|72CB98,9|7|7FD4A4,21|4|7AD1A4,6|-11|B78D7A,11|-7|FFEFE6,15|-14|EDE1DC,10|-15|FFEAE1,1|-18|FFF0E7,1|-15|FFF6EF", 0, 0.93, "布阵按钮2"
    )
    val JoinTournament = ColorSchema.parse(
        435, 496, 705, 568, "D8963B", "54|0|D8963B,108|0|D8963B,162|0|D8963B,216|0|D8963B,0|36|D8963B,54|36|D8963B,108|36|D8963B,162|36|D8963B,216|36|D8963B", 0, 0.9, "锦标赛"
    )
    val PlayTournament = ColorSchema.parse(
        434, 496, 703, 571, "2DADF9", "28|0|2DADF9,54|-2|2DADF9,176|-4|2DADF9,206|-4|2DADF9,228|-4|2DADF9,7|34|2CADF9,28|35|2CADF9,178|37|2CADF9,235|33|2CADF9", 0, 0.9, "排位赛"
    )
    val ConfirmTournamentAttack = ColorSchema.parse(
        669, 442, 855, 515, "77F4D8", "37|0|78F5D8,74|0|78F5D7,111|0|78F5D7,148|0|78F5D7,0|37|1EBD6E,37|37|1FBD6E,74|37|1FBD6E,111|37|1FBD6E,148|37|1FBD6E", 0, 0.9, "确认排位赛进攻"
    )
    val WaitForTournament = ColorSchema.parse(
        434, 498, 706, 571, "9D9D9D", "20|0|9D9D9D,36|16|9D9D9D,180|18|9D9D9D,202|18|9D9D9D,213|11|9D9D9D,222|8|9D9D9D,218|26|9D9D9D,203|38|9D9D9D,192|37|9D9D9D", 0, 0.9, "排位赛等待"
    )
    val GreenSword = ColorSchema.parse(
        491, 539, 542, 575, "15D85A", "3|-4|0DFFC6,9|-8|15CF5F,14|-13|15CF60,9|-14|30FFC2,6|-11|29FFB8,3|-7|22FFC4,2|-6|21FFC2,1|-2|10E15E,-2|-1|16D959", 0, 0.9, "绿色的剑"
    )
}