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
    //Main Base Shop Colours
    val ShopButton = ColorSchema.parse(
        1138, 605, 1262, 697, "1B87C6", "13|0|2196DE,26|0|41669B,38|0|2EAFF9,51|0|4FD1FC,0|15|1DA8EF,13|15|335381,26|15|1C6494,38|15|50D4FF,51|15|3DD2FF", 0, 0.9, "商店按钮"
    )
    val OldVersion = ColorSchema.parse(
        55, 6, 171, 54, "9FFCFF", "0|6|26A5F3,9|8|43D1F5,16|-6|EEF4E6,32|-7|DDFAF6,40|-7|DDFAF6,45|-8|DFFCF7,56|-7|DDFAF6,45|19|7AC6AF,55|20|7BC5B0", 0, 0.9, "切换旧版商店"
    )
    val LeaguePageButton = ColorSchema.parse(
        46, 22, 1166, 109, "8FF5FB", "5|0|8DF5FB,11|0|87F4FB,16|0|85E1F4,21|0|A6F5FA,0|13|1176AF,5|13|021920,11|13|02161B,16|13|091E22,21|13|5DE2F8", 0, 0.9, "联赛页面按钮"
    )
    val LeaguePageButton2 = ColorSchema.parse(
        127, 50, 1121, 146, "A9F8FC", "5|0|A8F8FD,11|0|A3F8FD,17|0|9BFAFE,22|0|B5F7FB,0|17|1588BD,5|17|021E25,11|17|031F25,17|17|051F25,22|17|41C1E4", 0, 0.9, "联赛页面按钮2"
    )
    val ClanLeagueConfirmBuy = ColorSchema.parse(
        502, 387, 791, 526, "ABFFFF", "5|0|ACFFFF,11|0|A6FFFF,17|0|A0FEFF,22|0|9EE2EB,0|13|217AA0,5|13|51C8E6,11|13|53CEEC,17|13|45C7E7,22|13|41C5E5", 0, 0.9, "联赛商店确认购买"
    )
    val LeagueShopResearchPotion1 = ColorSchema.parse(
        28, 258, 1267, 637, "500D5A", "-20|-8|760DA3,-20|-21|6F0D95,-11|-27|610D7A,10|-28|636E69,23|-24|715A55,34|-15|3C577B,21|8|43103E,6|-82|94CFFF,5|-83|94CFFF", 0, 0.9, "国际服联赛研究药水"
    )
    val LeagueShopResearchPotion2 = ColorSchema.parse(
        28, 258, 1267, 637, "510D5B", "-8|0|660D87,-14|-15|730D9C,-8|-23|79129C,4|-19|755878,10|-19|541062,20|-9|604E49,32|-3|8A8D96,6|-55|94CFFF,-2|-59|8BC2F0", 0, 0.9, "国服联赛研究药水"
    )
    val LeagueShopBuilderPotion1 = ColorSchema.parse(
        28, 258, 1267, 637, "D7B04B", "-6|19|D2A542,-24|6|F0C24D,-33|-16|F1C651,-23|-21|E7BF57,27|-12|7DD7F6,31|-17|7DD7F6,-9|-69|93CEFF,3|-80|9AD7FF,-7|-61|98D5FF", 0, 0.9, "国际服联赛工人药水"
    )
    val LeagueShopBuilderPotion2 = ColorSchema.parse(
        28, 258, 1267, 637, "E4C25F", "7|14|CCA438,-9|13|F1C54E,-3|-2|E6C056,8|-7|F2E0BB,28|-5|7DD7F6,31|2|7DD6F5,9|-29|374855,3|-43|8CC1F0,10|-45|93CEFF", 0, 0.9, "国服联赛工人药水"
    )
}
