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
    // Hero Hall Operation Colors
    val OpenHeroHall = ColorSchema.parse(
        160, 490, 1120, 630, "4EB7F9", "7|0|5FC5FD,14|0|98EBFF,20|0|F9FFFF,27|0|2D5B89,0|17|0E90D2,7|17|3EB6F2,14|17|51BCF2,20|17|4C8DBD,27|17|0E75AA", 0, 0.9, "英雄殿堂标志"
    )
    val RedExclamationMark = ColorSchema.parse(
        606, 559, 637, 585, "5B57F4", "0|2|5B57F4,0|3|5B57F4,0|4|5B57F4,0|5|5B57F4,14|6|5F5BF7,14|5|5F5BF7,14|4|5F5BF6,14|3|5F5BF6,14|2|5F5BF6", 0, 0.9, "红色可摆放战旗感叹号"
    )
    val PlaceBannerButton = ColorSchema.parse(
        627, 465, 775, 514, "7EE6B4", "29|0|7EE6B4,59|0|7EE6B4,89|0|7EE6B4,118|0|7EE6B4,0|25|3AD48B,29|25|3AD48B,59|25|3AD48B,89|25|3AD48B,118|25|3AD48B", 0, 0.9, "放置英雄战旗"
    )
    val UpgradeGearArrow = ColorSchema.parse(
        114, 269, 1191, 302, "2EC87D", "0|-2|36D288,2|-2|36D288,0|-1|2ECB80,3|-1|2ECB80,3|1|2EC87D,3|3|2EC87D,2|3|2EC87D,2|-4|41E39E,1|-4|41E39E", 0, 0.9, "升级装备箭头"
    )
    val NewGear = ColorSchema.parse(
        114, 269, 1191, 302, "2ECB88", "0|1|2ECD91,0|2|2ECE93,0|3|2ED196,0|4|41EABA,0|5|42EABB,24|1|2ECD91,24|2|2ECE93,24|3|2ED196,24|4|41EABA", 0, 0.9, "新装备"
    )
    val SmithOreIcon = ColorSchema.parse(
        405, 627, 418, 643, "B47F28", "3|0|F3A930,6|0|FFC44B,8|0|FFC752,11|0|FFCB59,0|8|B07C0D,3|8|C38E0D,6|8|D1980D,8|8|CB940D,11|8|FFF19E", 0, 0.9, "铁匠铺标志"
    )
    val PetsIconInHeroHall = ColorSchema.parse(
        758, 586, 786, 605, "45C2DF", "5|0|3DAAC9,11|0|1E8EB9,17|0|1672A0,22|0|289CC2,0|9|39B9DA,5|9|42BEDC,11|9|3DB9D7,17|9|32A9CA,22|9|2AA2C5", 0, 0.9, "英雄殿堂宠物标志"
    )

    // Main Base Clan Colors
    val RequestReinforcement = ColorSchema.parse(
        284, 650, 482, 714, "3AD48B", "-27|-2|2D2D31,-8|-17|FFFFFF,-9|-33|ECECEC,2|-24|949494,14|-29|F5F5F5,15|-19|FFFFFF,-42|-24|97A4B1,14|-21|FFFFFF,12|-26|FDFDFD", 0, 0.9, "求援按钮"
    )
}