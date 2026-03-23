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

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors,
    IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors {
    // Main Base Clan Colors
    val IUnderstand = ColorSchema.parse(
        128, 456, 358, 532, "78F1D0", "2|-27|383B41,48|-27|383B41,94|-27|383B41,140|-27|383B41,-44|11|55E0AD,2|11|55E0AD,48|11|54DEAC,94|11|55E0AD,140|11|55E0AD", 0, 0.9, "加部落我了解"
    )
    val JoinClanButton = ColorSchema.parse(
        962, 475, 1122, 511, "71EDC9", "32|0|71EDC9,111|0|71EDC9,96|0|71EDC9,128|0|71EDC9,0|18|2EC178,32|18|2EC178,64|18|2EC178,96|18|2EC178,128|18|2EC178", 0, 0.9, "加入部落按钮"
    )
    val ExitClanButton = ColorSchema.parse(
        966, 475, 1121, 505, "615DF8", "31|0|615DF8,62|0|5653DA,93|0|615DF8,124|0|615DF8,0|15|0E0DCF,31|15|0E0DCF,88|6|5F5DF2,93|15|0E0DCF,124|15|0E0DCF", 0, 0.9,
    )
    val ApplyClanSetting = ColorSchema.parse(
        560, 519, 767, 574, "59D89F", "41|0|59D89F,83|0|59D89F,124|0|59D89F,165|0|59D89F,0|27|2CCD84,41|27|2CCD84,83|27|2CCD84,124|27|2CCD84,165|27|2CCD84", 0, 0.9, "应用部落设置"
    )
    val SearchOptions = ColorSchema.parse(
        768, 200, 938, 231, "0B3763", "34|0|0B3763,68|0|7AF1D2,102|0|7AF1D2,136|0|7AF1D2,0|15|0B3F6B,34|15|0B3F6B,68|15|2CCD84,102|15|2CCD84,136|15|2CCD84", 0, 0.9, "设置选项，已搜索到部落"
    )
    val NotJoinClanFlag = ColorSchema.parse(
        40, 302, 63, 318, "BFD9F2", "5|0|C7DFF3,10|0|D9EFF5,14|0|0D40E3,19|0|0D4BE6,0|8|BFD9F2,5|8|C7DFF3,10|8|D9EFF5,14|8|0D40E3,19|8|0D4BE7", 0, 0.9
    )

    //UI Colors
    val ExclusiveGift = ColorSchema.parse(
        1080, 113, 1119, 153, "8A83FF", "8|0|FFFFFF,16|0|FFFFFF,23|0|FFFFFF,31|0|8A83FF,12|8|FFFFFF,32|13|1611EC,13|30|2D25CD,-5|18|1A16ED,31|20|1511E8", 0, 0.9, "垃圾专属礼包"
    )
    val MorePointCoupon = ColorSchema.parse(
        879, 195, 926, 241, "8B83FF", "9|0|FFFFFF,19|0|FFFFFF,18|8|FFFFFF,34|11|716BFD,42|15|2621F0,38|23|1712EB,19|23|0D0D0D,28|23|FAF6F6,37|23|1611EA", 0, 0.9, "更多点券"
    )
}