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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.IMainBaseHeroHallColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseHeroHallColors

object MyColors : IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial, IMainBaseTraining by MainBaseTraining,
    IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors, IBuilderBaseTrainingColors by BuilderBaseTrainingColors,
    IBuilderBaseUpgradeColors by BuilderBaseUpgradeColors, IBuilderBaseResearchColors by BuilderBaseResearchColors, IBuilderBaseAttackColors by BuilderBaseAttackColors, IUniversalUpgradeColors by UniversalUpgradeColors,
    IMainBaseUpgradeColors by MainBaseUpgradeColors, IMainBaseResearchColors by MainBaseResearchColors, IMainBaseResearchLevelColors by MainBaseResearchLevelColors,
    IClanCapitalTutorialColors by ClanCapitalTutorialColors, IMainBaseAttackColors by MainBaseAttackColors, IMainBaseClanColors by MainBaseClanColors, IMainBaseHeroHallColors by MainBaseHeroHallColors {
    //Main Base Pets Colors
    val Lassi = ColorSchema.parse(
        175, 385, 1110, 665, "FA51FA", "15|-34|CEA79B,37|-33|AA604D,55|-7|4E322D,81|-6|694337,88|-57|DF6B8F,93|-75|FF85FF,135|-55|834D3F,135|-28|E88E6D,116|-17|C06350", 0, 0.9, "莱希"
    )
    val ElectroOwl = ColorSchema.parse(
        175, 385, 1110, 665, "E2CBB6", "22|-18|E3CDB7,48|-46|BC9562,101|-9|E3CBB5,106|-40|B79F6C,93|-76|BAB653,105|-83|857C4C,116|-19|7C503D,149|-12|8B5649,141|-5|8C584C", 0, 0.0, "闪枭"
    )
    val MightyYak = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "-12|-43|82AEC5,64|-38|255BA1,56|-63|215DA4,66|-62|2A63AF,77|-41|2765B5,80|-16|2868B9,96|-7|2869BF,101|-15|2B7ADB,122|-41|2975DA", 0, 0.0, "大牦"
    )
    val Unicorn = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "45|-8|C6B6B8,55|-24|D1C5CC,65|-50|D5CCD5,84|-62|D3CED9,105|-72|D7CACC,126|-56|E4DCE0,102|-39|D5DEEF,87|-15|CBC3CD,112|0|63CFFF", 0, 0.0, "独角"
    )
    val Frosty = ColorSchema.parse(
        175, 385, 1110, 665, "947366", "11|-17|89695C,42|-26|5C8593,72|-14|B8A5A5,101|-13|968D96,84|-20|D3BCBF,114|-7|507D98,77|-39|7E6467,91|-65|362E2E,109|-37|8F8486", 0, 0.0, "冰牙"
    )
    val Diggy = ColorSchema.parse(
        175, 385, 1110, 665, "AC5D7D", "7|-25|E87E92,28|-47|BBD5F6,44|-22|FE8696,80|-2|795D67,115|-12|8D6072,135|-36|D36E99,99|-49|FFA0B9,88|-61|FF9872,88|-89|FFB874", 0, 0.0, "地兽"
    )
    val PoisonLizard = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "38|-26|44DBFF,76|-59|3A86FB,94|-81|52A9FF,96|-47|4397FF,83|-39|4082FF,96|-29|44A9FF,116|-39|4AA2FF,116|-70|0D0F52,116|-94|6FD2FF", 0, 0.0, "猛蜥"
    )
    val Phoenix = ColorSchema.parse(
        175, 385, 1110, 665, "964EFF", "18|-33|AC4AFF,46|-14|48B6FF,67|-52|8956FF,84|-61|53FFFF,104|-43|576276,130|-46|546173,117|-28|35444D,75|-61|50C0DC,68|-108|C14FFF", 0, 0.0, "凤凰"
    )
    val SpiritFox = ColorSchema.parse(
        175, 385, 1110, 665, "E3B23B", "28|-7|BE973C,60|-1|E8C48A,88|-1|FFFFCD,70|-33|FFE19E,58|-49|D6CC91,80|-70|8E5B1F,102|-59|0E0E0E,94|-47|FFFFCA,91|-11|FFF5AC", 0, 0.0, "灵狐"
    )
    val AngryJelly = ColorSchema.parse(
        175, 385, 1110, 665, "C271FF", "16|-21|DA7EFF,42|-42|FFACFF,59|-29|DE94FF,89|1|A941FF,127|-11|5228E0,133|-38|A54AF4,130|-66|CD53E4,72|-87|FFC7FF,69|-66|FFA8FF", 0, 0.0, "愤怒水母"
    )
    val Sneezy = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "34|-36|7F5652,69|-41|C3A19C,92|-61|C9B6E8,113|-32|846CA1,128|-68|947BBE,141|-61|8A6160,121|-42|C8AFDD,89|-12|0D0D0D,66|-32|BA948D", 0, 0.0, "阿啾"
    )
    val GreedyRaven = ColorSchema.parse(
        175, 385, 1110, 665, "787272", "-1|-16|807C77,7|-37|205274,23|-39|43294C,42|-11|613761,63|-37|462C46,68|-54|582E54,82|-100|F8ABFD,103|-87|3E3E45,128|-77|312F2E", 0, 0.0, "贪婪渡鸦"
    )
    val ConfirmUpgradePet = ColorSchema.parse(
        806, 591, 993, 664, "89F8D5", "37|0|89F8D5,63|14|87EFCC,112|0|89F8D5,149|0|89F8D5,0|37|4AD3A2,103|13|87F0CD,127|9|88F2D0,112|37|4AD3A2,149|5|88F5D2", 0, 0.9, "确认升级战宠"
    )

    //Main Base Small Tutorial Colors (Not current Tutorial Colors)
    val MainBaseSmallTutorial = ColorSchema.parse(
        164, 77, 1064, 605, "21ABFE", "7|0|1FB9FE,14|0|1EBBFE,20|0|1EB5FE,27|0|21A8FE,0|21|2CC8F9,7|21|28C7F9,14|21|26C5F9,20|21|25C2F8,27|21|28C2F9", 0, 0.9, "教程朝下箭头"
    )
    val OuterPetIcon = ColorSchema.parse(
        167, 503, 1115, 628, "32AFD2", "8|0|42BFDC,16|0|3DB9D7,23|0|39AECF,31|0|0D6EB4,0|10|32AFD2,8|10|3FBCDA,16|10|187AAA,23|10|36B0D0,31|10|2FA6CA", 0, 0.9, "宠物店按钮"
    )
    val ClanCastleAddReinforcement = ColorSchema.parse(
        171, 510, 1122, 634, "67778F", "9|0|7F8EAA,18|0|8C94A5,26|0|ABB6BE,35|0|E2D1B9,0|20|545A67,9|20|6C7388,18|20|A4B4C6,26|20|DCE1E5,35|20|E4D1BB", 0, 0.9, "部落城堡教程"
    )
    val RemoteGuardsIcon = ColorSchema.parse(
        165, 502, 1133, 632, "2F4B72", "7|0|5C4E55,13|0|546370,19|0|1E81D6,26|0|525B66,0|18|634C4F,7|18|ACC6D4,13|18|C8E4F1,19|18|C6E5F5,26|18|97ADBF", 0, 0.9, "远程守卫图标"
    )
    val MeleeGuardsIcon = ColorSchema.parse(
        165, 502, 1133, 632, "56281F", "6|0|FFD451,12|0|FFE13E,17|0|BCFFC4,23|0|43BFFF,0|17|8E9AAA,6|17|7C899A,12|17|748294,17|17|91AABA,23|17|9BB5C2", 0, 0.9, "近战守卫图标"
    )
}