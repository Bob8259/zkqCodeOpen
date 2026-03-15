package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
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
    // Main Base Attack Color
    val EndBattle = ColorSchema.parse(
        114, 526, 152, 556, "5F5DF4", "7|0|5F5DF4,15|0|5F5DF4,23|0|5F5DF4,30|0|5F5DF4,0|15|0E0DCE,7|15|0E0DCE,15|15|0E0DCE,23|15|0E0DCE,30|15|0E0DCE", 0, 0.9,
    )
    val KingBarbarian = ColorSchema.parse(
        80, 590, 1200, 720, "24388B", "8|0|5272BF,16|0|6284D8,23|0|729AEF,31|0|44A7EA,0|14|283A6C,8|14|213365,16|14|1D2E5C,23|14|1A2954,31|14|1F2D65", 0, 0.9, "野蛮人之王"
    )
    val QueenArcher = ColorSchema.parse(
        80, 590, 1200, 720, "DAB9DA", "9|0|83AAF6,19|0|883044,28|0|AC3F66,37|0|B34169,0|15|1C0814,9|15|253B6F,19|15|8A2E4E,28|15|37111F,37|15|872D4E", 0, 0.9, "弓箭女皇"
    )
    val MinionPrince = ColorSchema.parse(
        80, 590, 1200, 720, "1C1B1B", "8|0|82B2DE,17|0|FFF77C,25|0|E2B22D,33|0|CE9C0E,0|11|221201,8|11|3F3322,17|11|311D05,25|11|965A00,33|11|8B5810", 0, 0.9, "亡灵王子"
    )
    val GrandWarden = ColorSchema.parse(
        80, 590, 1200, 720, "57054A", "11|0|742377,23|0|831B79,35|0|A264C8,46|0|D14DC9,0|11|922A82,11|11|A0419C,23|11|69125F,35|11|AF29A4,46|11|410E3C", 0, 0.9, "大守护者"
    )
    val GrandWarden2 = ColorSchema.parse(
        80, 590, 1200, 720, "882673", "11|0|902086,23|0|6D1162,35|0|BC10AC,46|0|C234B7,0|11|881E71,11|11|8F2D88,23|11|9F2494,35|11|C843C0,46|11|CD47C5", 0, 0.9, "大守护者2"
    )
    val RoyalChampion = ColorSchema.parse(
        80, 590, 1200, 720, "467BDC", "7|0|133070,15|0|0F2D62,22|0|23280B,29|0|4B5D71,0|14|3871D3,7|14|112A5B,15|14|386BCB,22|14|3D63C2,29|14|2D3C74", 0, 0.9, "飞盾战神"
    )
    val DragonDuke = ColorSchema.parse(
        80, 590, 1200, 720, "000023", "7|0|1A134C,15|0|120E53,23|0|070828,30|0|000021,0|15|58506E,7|15|F2F8FF,15|15|303B7F,23|15|A5A3DB,30|15|E9ECFF", 0, 0.9, "飞龙公爵"
    )
    val TroopColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "D08E4C", "5|0|D18F4D,11|0|D1904E,16|0|D19150,21|0|D29250,0|5|BE8444,5|5|BF8544,11|5|C08646,16|5|C18848,21|5|C1894A", 0, 0.9, "部队颜色"
    )
    val SpellColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "D95371", "8|0|DA5372,17|0|DA5372,25|0|DA5372,33|0|DA5372,0|5|C64A61,8|5|C64A61,17|5|C64B62,25|5|C74B63,33|5|C74B63", 0, 0.9, "法术颜色"
    )
}
