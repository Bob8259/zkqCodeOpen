@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseAttackColors {
    val SetBaseIcon: ColorSchema
    val InnerSetBase: ColorSchema
    val SearchOpponents: ColorSchema
    val AttackButton: ColorSchema
    val NextOpponent: ColorSchema
    val GoldColor: ColorSchema
    val ElixirColor: ColorSchema
    val DarkElixirColor: ColorSchema
    val DarkElixirIcon: ColorSchema
    val InsufficientGold: ColorSchema
    val DragonAtDeploymentBar: ColorSchema

    // Troop deploy bar color variants
    val BarbarianAtDeploymentBar: ColorSchema
    val BarbarianAtDeploymentBar2: ColorSchema
    val GiantAtDeploymentBar: ColorSchema
    val GiantAtDeploymentBar2: ColorSchema
    val ArcherAtDeploymentBar: ColorSchema
    val ArcherAtDeploymentBar2: ColorSchema
    val ArcherAtDeploymentBar3: ColorSchema
    val ArcherAtDeploymentBar4: ColorSchema
    val DragonAtDeploymentBar2: ColorSchema

    // Battle and hero colors
    val EndBattle: ColorSchema
    val KingBarbarian: ColorSchema
    val QueenArcher: ColorSchema
    val QueenArcher2: ColorSchema
    val MinionPrince: ColorSchema
    val MinionPrince2: ColorSchema
    val GrandWarden: ColorSchema
    val GrandWarden2: ColorSchema
    val GrandWarden3: ColorSchema
    val RoyalChampion: ColorSchema
    val RoyalChampion2: ColorSchema
    val DragonDuke: ColorSchema
    val TroopColorAtDeploymentBar: ColorSchema
    val SpellColorAtDeploymentBar: ColorSchema
    val SuperTroopColorAtDeploymentBar: ColorSchema
    val SpecialTroopColorAtDeploymentBar: ColorSchema
}

object MainBaseAttackColors : IMainBaseAttackColors {
    override val SetBaseIcon = ColorSchema.parse(
        546, 78, 573, 118, "1AABFE", "5|0|1AB4FE,11|0|1AB8FE,16|0|1AB5FE,21|0|1AAEFE,0|20|16B4FA,5|20|12B6FA,11|20|11B7FB,16|20|11B6FB,21|20|10B3FA", 0, 0.93, "布阵按钮"
    )
    override val InnerSetBase = ColorSchema.parse(
        341, 46, 504, 146, "4B5261", "33|0|4B5261,66|0|4B5261,98|0|4B5261,131|0|4B5261,0|50|C6D2D9,33|50|C6D2D9,66|50|C6D2D9,98|50|C6D2D9,131|50|C6D2D9", 0, 0.97
    )
    override val SearchOpponents = ColorSchema.parse(
        89, 501, 350, 570, "2DADF9", "26|-9|3CB7FC,14|13|2CADF9,4|32|2CADF9,202|-5|2FB0FA,226|0|2DADF9,236|13|2CADF9,237|20|2CADF9,227|32|2CADF9,211|45|2CADF9", 0, 0.95, "搜索对手"
    )
    override val AttackButton = ColorSchema.parse(
        1045, 624, 1245, 662, "9BFDCF", "13|-5|A1FED2,24|-4|A1FED2,16|1|9AFCCE,8|20|4EE79F,121|-5|A1FED2,140|-3|A0FED1,146|3|97FCCC,132|20|4EE79F,137|20|4EE79F", 0, 0.9, "进攻！"
    )
    override val NextOpponent = ColorSchema.parse(
        1072, 472, 1265, 560, "36BFFD", "18|0|36BFFD,38|0|36BFFD,44|0|36BFFD,72|1|36BFFD,-56|53|0D59E8,-53|57|0D55E6,-47|58|0D54E6,-32|68|0D50E4,-27|72|0D50E4", 0, 0.95
    )
    override val GoldColor = ColorSchema.parse(
        998, 19, 1215, 136, "0DC0E7", "0|1|0DC0E7,0|2|0DC0E7,0|3|0DC0E7,0|4|0DC0E7,0|5|0DC0E7,0|6|0DC0E7,0|7|0DC0E7", 0, 0.97,
    )
    override val ElixirColor = ColorSchema.parse(
        998, 19, 1215, 136, "C027C0", "0|1|C027C0,0|2|C027C0,0|3|C027C0,0|4|C027C0,0|5|C027C0,0|6|C027C0,0|7|C027C0", 0, 0.97,
    )
    override val DarkElixirColor = ColorSchema.parse(
        1062, 133, 1091, 197, "330D27", "0|1|330D27,0|2|330D27,0|3|330D27,0|4|330D27,0|5|330D27,0|6|330D27,0|7|330D27", 0, 0.97,
    )
    override val DarkElixirIcon = ColorSchema.parse(
        1214, 136, 1255, 233, "4A3445", "5|0|443241,9|0|554050,13|0|695162,18|0|685062,0|13|342E37,5|13|38303C,9|13|3A313D,13|13|3B313E,18|13|3B303D", 0, 0.9,
    )
    override val InsufficientGold = ColorSchema.parse(
        679, 440, 699, 470, "79F7DD", "4|0|7BF8DE,8|0|80F9E2,12|0|87FCE7,16|0|8CFFEB,0|15|96FEE5,4|15|83FAD7,8|15|71F5CB,12|15|8BFDE0,16|15|7FFCDB", 0, 0.9, "搜索金币不足"
    )
    override val DragonAtDeploymentBar = ColorSchema.parse(
        85, 589, 1189, 717, "DB5C6E", "14|6|4F2D8C,27|10|EB6B79,24|22|BF4F5E,15|30|2E268C,25|37|5537A9,27|45|5C336D,25|47|552268,2|23|7393F7,-10|19|883542", 0, 0.9, "部署飞龙"
    )

    // Troop deploy bar color variants
    override val BarbarianAtDeploymentBar = ColorSchema.parse(
        85, 589, 1189, 717, "2FB2F1", "9|17|5C96F9,16|24|5AE6FE,8|36|202780,8|23|4DD4FC,9|12|223E85,4|8|2EA2DC,9|8|2EA6E2,12|24|52DCFD,24|36|62E1FD", 0, 0.9, "部署野蛮人"
    )

    // Additional barbarian deploy bar color variant
    override val BarbarianAtDeploymentBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "36B6F1", "8|0|3FCCFB,16|0|48C5F8,18|44|689BF1,37|47|5877C1,0|16|868FDA,8|16|5A89F3,16|16|619DFB,24|16|689DF1,32|16|73B2FB", 0, 0.9, "部署野蛮人2"
    )
    override val GiantAtDeploymentBar = ColorSchema.parse(
        85, 589, 1189, 717, "82B5FC", "13|15|6A9FF3,24|8|77AAF8,24|-12|4A71B3,8|-21|56A1FC,-15|-21|3C91FC,-23|-2|465B92,-12|3|5983D1,-3|12|3E5497,22|31|6CACF8", 0, 0.9, "部署巨人"
    )

    // Additional giant deploy bar color variant
    override val GiantAtDeploymentBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "154AA2", "10|0|0944A8,21|0|3454A8,29|40|7AAFF9,42|31|669AF0,0|16|608EE0,10|16|5463A2,21|16|A3C8FD,31|16|6EA0F3,41|16|76ACF6", 0, 0.9, "部署巨人2"
    )
    override val ArcherAtDeploymentBar = ColorSchema.parse(
        85, 589, 1189, 717, "662DBC", "5|12|91A8FB,4|24|5368AD,-8|21|6070B9,-22|5|210E4D,-18|-14|7D3BBC,-8|-18|6F30C0,4|-19|672CC1,7|-14|431A8A,6|-6|331270", 0, 0.9, "部署弓箭手"
    )

    // Additional archer deploy bar color variant
    override val ArcherAtDeploymentBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "8E43C3", "9|0|6D2DB9,17|0|662BBA,25|0|662DBB,20|37|431D87,24|58|199D7D,9|15|535A84,17|15|481A75,25|15|652BBD,34|15|672BC2", 0, 0.9, "部署弓箭手2"
    )

    // Third archer deploy bar color variant
    override val ArcherAtDeploymentBar3 = ColorSchema.parse(
        85, 589, 1189, 717, "8A40B3", "9|0|7633BA,18|0|6329BD,26|0|652CBD,35|0|692BC3,0|17|3F1A81,9|17|6B71AD,18|17|2B3665,26|17|5D65AF,35|17|566AB7", 0, 0.9, "部署弓箭手3"
    )

    // Fourth archer deploy bar color variant
    override val ArcherAtDeploymentBar4 = ColorSchema.parse(
        85, 589, 1189, 717, "34125E", "9|0|8E44C4,18|0|632CBD,26|0|652CBD,35|0|682CC6,0|17|29105A,9|17|6069A2,18|17|34426E,26|17|717BCB,35|17|7A93E9", 0, 0.9, "部署弓箭手4"
    )

    // Dragon deploy bar color variant
    override val DragonAtDeploymentBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "C75161", "9|0|B86A75,18|0|562D87,27|26|6035E8,30|44|C0505B,0|12|2A0578,9|12|6A243A,18|12|712C4E,27|12|D35868,36|12|AE4857", 0, 0.9, "部署飞龙2"
    )

    // Battle and hero colors
    override val EndBattle = ColorSchema.parse(
        114, 526, 152, 556, "5F5DF4", "7|0|5F5DF4,15|0|5F5DF4,23|0|5F5DF4,30|0|5F5DF4,0|15|0E0DCE,7|15|0E0DCE,15|15|0E0DCE,23|15|0E0DCE,30|15|0E0DCE", 0, 0.9,
    )
    override val KingBarbarian = ColorSchema.parse(
        80, 590, 1200, 720, "24388B", "8|0|5272BF,16|0|6284D8,23|0|729AEF,31|0|44A7EA,0|14|283A6C,8|14|213365,16|14|1D2E5C,23|14|1A2954,31|14|1F2D65", 0, 0.9, "野蛮人之王"
    )
    override val QueenArcher = ColorSchema.parse(
        80, 590, 1200, 720, "DAB9DA", "9|0|83AAF6,19|0|883044,28|0|AC3F66,37|0|B34169,0|15|1C0814,9|15|253B6F,19|15|8A2E4E,28|15|37111F,37|15|872D4E", 0, 0.9, "弓箭女皇"
    )

    // Second queen archer deploy bar color variant
    override val QueenArcher2 = ColorSchema.parse(
        80, 590, 1200, 720, "576EB9", "11|0|8898F2,22|0|5F87E0,32|0|AB3C64,43|0|B24169,0|11|6E82B3,11|11|304986,22|11|913457,32|11|A23B60,43|11|6D253E", 0, 0.9, "弓箭女皇2"
    )
    override val MinionPrince = ColorSchema.parse(
        80, 590, 1200, 720, "1C1B1B", "8|0|82B2DE,17|0|FFF77C,25|0|E2B22D,33|0|CE9C0E,0|11|221201,8|11|3F3322,17|11|311D05,25|11|965A00,33|11|8B5810", 0, 0.9, "亡灵王子"
    )

    // Second minion prince deploy bar color variant
    override val MinionPrince2 = ColorSchema.parse(
        80, 590, 1200, 720, "A47033", "9|0|433F40,18|0|98D5FF,27|0|FDD44B,36|0|DAA700,0|13|1E0E01,9|13|2E1B01,18|13|574712,27|13|1D0000,36|13|804D07", 0, 0.9, "亡灵王子2"
    )
    override val GrandWarden = ColorSchema.parse(
        80, 590, 1200, 720, "57054A", "11|0|742377,23|0|831B79,35|0|A264C8,46|0|D14DC9,0|11|922A82,11|11|A0419C,23|11|69125F,35|11|AF29A4,46|11|410E3C", 0, 0.9, "大守护者"
    )
    override val GrandWarden2 = ColorSchema.parse(
        80, 590, 1200, 720, "882673", "11|0|902086,23|0|6D1162,35|0|BC10AC,46|0|C234B7,0|11|881E71,11|11|8F2D88,23|11|9F2494,35|11|C843C0,46|11|CD47C5", 0, 0.9, "大守护者2"
    )

    // Third grand warden deploy bar color variant
    override val GrandWarden3 = ColorSchema.parse(
        80, 590, 1200, 720, "771262", "11|0|9439A1,23|0|651360,35|0|81BAFB,46|0|CB43C0,0|14|942C84,11|14|993E99,23|14|1E030F,35|14|C941C1,46|14|CE45C6", 0, 0.9, "大守护者3"
    )
    override val RoyalChampion = ColorSchema.parse(
        80, 590, 1200, 720, "467BDC", "7|0|133070,15|0|0F2D62,22|0|23280B,29|0|4B5D71,0|14|3871D3,7|14|112A5B,15|14|386BCB,22|14|3D63C2,29|14|2D3C74", 0, 0.9, "飞盾战神"
    )

    // Second royal champion deploy bar color variant
    override val RoyalChampion2 = ColorSchema.parse(
        80, 590, 1200, 720, "497DDE", "8|0|628BF1,16|0|2F65C4,24|0|2B65C3,32|0|2B58A9,0|11|366ED0,8|11|123069,16|11|3368C8,24|11|395FBA,32|11|394173", 0, 0.9, "飞盾战神2"
    )
    override val DragonDuke = ColorSchema.parse(
        80, 590, 1200, 720, "000023", "7|0|1A134C,15|0|120E53,23|0|070828,30|0|000021,0|15|58506E,7|15|F2F8FF,15|15|303B7F,23|15|A5A3DB,30|15|E9ECFF", 0, 0.9, "飞龙公爵"
    )
    override val TroopColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "D08E4C", "5|0|D18F4D,11|0|D1904E,16|0|D19150,21|0|D29250,0|5|BE8444,5|5|BF8544,11|5|C08646,16|5|C18848,21|5|C1894A", 0, 0.95, "部队颜色"
    )
    override val SpellColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "DA5372", "3|0|DA5372,6|0|DA5372,9|0|DA5372,12|0|DA5372,0|7|BF475E,3|7|BF485F,6|7|BF475E,9|7|BF475E,12|7|BE485E", 0, 0.95, "法术颜色"
    )
    override val SuperTroopColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "3E38D1", "3|0|3E38D2,6|0|3E38D2,9|0|3E38D2,12|0|3E38D2,0|5|3832B4,3|5|3832B5,6|5|3832B5,9|5|3932B5,12|5|3832B6", 0, 0.95, "超级兵颜色"
    )
    override val SpecialTroopColorAtDeploymentBar = ColorSchema.parse(
        80, 590, 1200, 720, "F7E2D1", "3|0|F7E2D1,6|0|F7E2D1,9|0|F7E2D1,12|0|F7E2D1,0|7|DDC5B2,3|7|DDC5B3,6|7|DFC6B3,9|7|DEC7B3,12|7|DEC6B3", 0, 0.95, "活动兵颜色"
    )
}
