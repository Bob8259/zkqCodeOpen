package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.ClanGameColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.GameUpdateColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IClanGameColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IGameUpdateColors
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseTournamentColors
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTournamentColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseUpgradeColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IClanGameColors by ClanGameColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial,
    IMainBaseTraining by MainBaseTraining, IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors,
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors, IBuilderBaseGearUpColors by BuilderBaseGearUpColors, IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors,
    IBuilderBaseClockTowerColors by BuilderBaseClockTowerColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors,
    IUniversalUpgradeColors by UniversalUpgradeColors, IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IMainBaseResourcesColors by MainBaseResourcesColors, IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors,
    IMainBaseClanWarColors by MainBaseClanWarColors, IMainBaseHelperColors by MainBaseHelperColors, IMainBaseHeroHallColors by MainBaseHeroHallColors, IMainBaseSmallTutorialColors by MainBaseSmallTutorialColors,
    IMainBaseMagicItemColors by MainBaseMagicItemColors, IClanCapitalColors by ClanCapitalColors, IMainBaseShopColors by MainBaseShopColors, IMainBaseTournamentColors by MainBaseTournamentColors,
    IGameUpdateColors by GameUpdateColors {
    //Capital Colours
    val CollectCapitalCoins = ColorSchema.parse(
        145, 390, 320, 460, "8EE9BF", "14|6|86E8BA,0|27|3BD48B,-3|38|3BD48B,125|3|88EABA,127|34|E019B7,132|36|DD17B3,125|37|E019B4,-7|43|3BD48B,-2|11|7DE7B4", 0, 0.9, "领都城币"
    )
    val GemForCapital = ColorSchema.parse(
        1195, 20, 1270, 240, "5BDEB8", "5|0|6CEAD1,10|0|73EDD5,15|0|7BF0DB,20|0|82F3E1,0|11|7DEED3,5|11|84F0D6,10|11|8AF2DC,15|11|63EAC3,20|11|7CF2D8", 0, 0.9, "宝石，用于检测是否进入都城"
    )
    val MiniPurpleCoinIcon = ColorSchema.parse(
        590, 100, 910, 560, "E514C4", "1|0|E219B9,3|0|DF1AB5,3|1|E018B6,3|2|DF18B4,3|3|DF17B4,2|3|DF17B4,1|3|DF17B5,0|3|E111B8,0|4|CE279D", 0, 0.96, "紫色小都城币"
    )
    val SufficientCapitalCoin = ColorSchema.parse(
        1194, 25, 1222, 60, "90EDFD", "0|1|8BEDFD,0|2|86EBFD,0|3|82E9FD,0|4|7CE8FD,0|5|77E7FD,0|6|70E5FD,0|7|69E3FD,0|8|17D5FF,0|9|0DD2FF", 0, 0.9, "都城币足够"
    )

    val BadgeEventContinueButton = ColorSchema.parse(
        573, 627, 709, 677, "86EABA", "27|0|86EABA,54|0|87EABA,81|0|88EABA,108|0|86EABA,0|25|3BD48B,27|25|3BD48B,54|25|3BD48B,81|25|3BD48B,108|25|3BD48B", 0, 0.9, "奖章活动继续按钮"
    )
    val BadgeEventPageMarker = ColorSchema.parse(
        1098, 34, 1141, 79, "8584FF", "9|0|FFFFFF,18|0|727572,26|0|E0E0E0,35|0|8584FF,0|23|231FF6,9|23|4F4C4F,18|23|131313,26|23|111112,35|23|231FF6", 0, 0.9, "奖章活动页面标记"
    )
    val BadgeEventClaimReward = ColorSchema.parse(
        146, 590, 1139, 654, "9BF5C7", "-50|-2|9BF8CC,-55|31|48E198,-2|33|43E195,55|37|43D68F,59|3|95F8C7,35|2|95F8C7,35|27|48E198,-16|2|98F8CA,-26|25|48E198", 0, 0.9, "奖章活动领取奖励"
    )

    // Event Shop colors
    val EventShopPageMarker = ColorSchema.parse(
        150, 54, 397, 105, "7E838C", "49|0|7E838C,99|0|7E838C,148|0|7E838C,197|0|7E838C,0|25|636973,49|25|636973,99|25|636973,148|25|636973,197|25|636973", 0, 0.9, "活动商店页面标记"
    )
    val EventShopAlreadyPurchased = ColorSchema.parse(
        394, 264, 596, 317, "0D52A7", "41|0|0D52A7,81|0|0D5CAE,121|0|0D59AA,162|0|0D5AAC,0|27|0D5AAF,41|27|0D56A9,89|32|0D57A8,121|27|0D57A9,162|27|0D5AA9", 0, 0.9, "活动商店新装备已购买标记"
    )
    val EventShopConfirmBuy = ColorSchema.parse(
        524, 405, 662, 503, "84E5BA", "27|0|84E5BA,55|0|84E5BA,83|0|84E5BA,110|0|84E5BA,0|49|2BCE86,27|49|2BCE86,55|49|2BCE86,83|49|2BCE86,110|49|2BCE86", 0, 0.9, "活动商店确认购买按钮"
    )

    //Build Colors
    val GemInBuildingList = ColorSchema.parse(
        0, 0, 0, 0, "8DFDE7", "-2|0|86FAE0,-4|0|7EF7DC,-4|-3|4DD8A1,-2|-5|45D097,2|-4|80FCE9,3|0|9DFFF1,3|3|75FBD9,2|4|76FAD5,-2|4|7BF8CE", 0, 0.95, "建筑列表宝石"
    )
}
