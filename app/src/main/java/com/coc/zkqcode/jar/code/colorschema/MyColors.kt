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

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IClanGameColors by ClanGameColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial,
    IMainBaseTraining by MainBaseTraining, IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors,
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseGearUpColors by BuilderBaseGearUpColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors,
    IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors,
    IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors,
    IMainBaseClanWarColors by MainBaseClanWarColors, IMainBaseHelperColors by MainBaseHelperColors, IMainBaseHeroHallColors by MainBaseHeroHallColors, IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors,
    IMainBaseMagicItemColors by MainBaseMagicItemColors, IClanCapitalColors by ClanCapitalColors {
    //Main Base Shop Colours
    val Merchant = ColorSchema.parse(
        207, 486, 286, 583, "2B5B50", "16|0|58A2A9,32|0|6B88DF,47|0|5A7DD7,63|0|7993E3,0|49|193C43,16|49|235052,32|49|3A707B,47|49|163FA5,63|49|203170", 0, 0.9, "商店商人"
    )
    val GemInShop = ColorSchema.parse(
        307, 644, 326, 662, "50DAA3", "4|0|78F4DA,8|0|7EF8E1,11|0|85FCE5,15|0|8DFFEC,0|9|8BFADF,4|9|92FDE3,8|9|88FBDD,11|9|77F7D3,15|9|8AFEE3", 0, 0.9, "商店内宝石"
    )
    val RaidMedalInShop = ColorSchema.parse(
        307, 644, 326, 662, "A47253", "4|0|FFFFFF,8|0|FFF8EB,11|0|D3B99C,15|0|CDB292,0|9|DECCB2,4|9|E0CEB6,8|9|BB9677,11|9|C6A88A,15|9|B89576", 0, 0.9, "商店内突袭币"
    )
    val ConfirmBuyWithRaidMedal = ColorSchema.parse(
        582, 407, 756, 509, "84E4B9", "32|1|84E4B9,51|9|84E4B9,60|23|84E4B9,54|73|2CCD84,15|85|24C06E,7|38|F1E5D4,15|33|CDB294,25|37|A7785B,11|43|C9AC8E", 0, 0.9, "确认突袭币购买"
    )
    val GoldOre = ColorSchema.parse(
        350, 10, 1120, 685, "0DF0FF", "-4|0|0DEBFA,-12|0|13D1F4,9|-15|8DD5F9,17|-4|0DAAFD,22|4|27C9F9,12|8|0DD6FD,5|15|0DBFFF,2|20|0DB0FB,4|25|337EFE", 0, 0.9, "黄矿"
    )
    val InsufficientMedal = ColorSchema.parse(
        0, 0, 0, 0, "7F88FF", "0|2|7F88FF,0|4|7F88FF,-1|2|7F88FF,-1|1|7F88FF,-1|0|7F88FF,-1|-1|7F88FF,-1|-2|7F88FF,0|-3|7F88FF,1|-3|7F88FF", 0, 0.98
    )

    val PurpleOre = ColorSchema.parse(
        350, 10, 1120, 685, "EC7DC2", "11|13|5B27A1,0|29|CD45A8,-7|9|D5A6FA,1|-3|DA5CA5,17|4|FFCFFF,25|7|F17DE1,23|18|C852BB,6|29|D03BCB,30|8|D668C8", 0, 0.9, "商店紫矿"
    )
    val ClockTowerPotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "F6BAD8", "6|12|F2AFD3,-17|6|FFDCE7,-6|-9|FBD2EA,11|-9|D3FFFF,21|-4|36507B,29|3|7EDCF9,1|18|F6BAD9,-3|-52|94CFFF,5|-44|99D6FF", 0, 0.9, "商店内钟楼药水"
    )
    val RingOfWall = ColorSchema.parse(
        350, 10, 1120, 685, "354C74", "-22|0|3F323B,-42|0|7DB8D0,-53|-12|86BDE0,-46|-24|5891C8,-34|-28|4D82B9,-20|-28|4D7EB0,-5|-27|7C4E52,3|-19|6B5B70,20|0|4779AC", 0, 0.9, "商店内壁垒之戒"
    )
    val ResearchPotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "440F48", "-16|-3|660D89,-20|-24|730DA1,-2|-22|62216E,15|-24|8ABFCB,25|-23|6FBAE5,14|-11|6D5953,9|-9|5E4245,-5|-64|94D0FF,2|-66|90C9F9", 0, 0.9, "商店内研究药水"
    )
    val ResourcePotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "F02FD0", "-15|6|FA5BE5,-16|-12|FC60E8,-4|-16|F9DDEE,10|-15|835C54,23|-10|97FFFF,25|-6|79C7EE,0|-35|FCC0EE,-4|-54|94CFFF,12|-56|99D6FF", 0, 0.9, "商店内资源药水"
    )
}

