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
    val DragonAtDeployBar: ColorSchema

    // Troop deploy bar color variants
    val BarbarianAtDeployBar: ColorSchema
    val BarbarianAtDeployBar2: ColorSchema
    val GiantAtDeployBar: ColorSchema
    val GiantAtDeployBar2: ColorSchema
    val ArcherAtDeployBar: ColorSchema
    val ArcherAtDeployBar2: ColorSchema
    val DragonAtDeployBar2: ColorSchema
}

object MainBaseAttackColors : IMainBaseAttackColors {
    override val SetBaseIcon = ColorSchema.parse(
        546, 78, 573, 118, "1AABFE", "5|0|1AB4FE,11|0|1AB8FE,16|0|1AB5FE,21|0|1AAEFE,0|20|16B4FA,5|20|12B6FA,11|20|11B7FB,16|20|11B6FB,21|20|10B3FA", 0, 0.9, "布阵按钮"
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
        1063, 23, 1115, 54, "0DC0E7", "0|1|0DC0E7,0|2|0DC0E7,0|3|0DC0E7,0|4|0DC0E7,0|5|0DC0E7,0|6|0DC0E7,0|7|0DC0E7", 0, 0.95,
    )
    override val ElixirColor = ColorSchema.parse(
        1063, 23, 1115, 54, "C027C0", "0|1|C027C0,0|2|C027C0,0|3|C027C0,0|4|C027C0,0|5|C027C0,0|6|C027C0,0|7|C027C0", 0, 0.95,
    )
    override val DarkElixirColor = ColorSchema.parse(
        1063, 23, 1115, 54, "330D27", "0|1|330D27,0|2|330D27,0|3|330D27,0|4|330D27,0|5|330D27,0|6|330D27,0|7|330D27", 0, 0.95,
    )
    override val DarkElixirIcon = ColorSchema.parse(
        1224, 168, 1246, 193, "4A3445", "5|0|443241,9|0|554050,13|0|695162,18|0|685062,0|13|342E37,5|13|38303C,9|13|3A313D,13|13|3B313E,18|13|3B303D", 0, 0.9,
    )
    override val InsufficientGold = ColorSchema.parse(
        679, 440, 699, 470, "79F7DD", "4|0|7BF8DE,8|0|80F9E2,12|0|87FCE7,16|0|8CFFEB,0|15|96FEE5,4|15|83FAD7,8|15|71F5CB,12|15|8BFDE0,16|15|7FFCDB", 0, 0.9, "搜索金币不足"
    )
    override val DragonAtDeployBar = ColorSchema.parse(
        85, 589, 1189, 717, "DB5C6E", "14|6|4F2D8C,27|10|EB6B79,24|22|BF4F5E,15|30|2E268C,25|37|5537A9,27|45|5C336D,25|47|552268,2|23|7393F7,-10|19|883542", 0, 0.9, "部署飞龙"
    )

    // Troop deploy bar color variants
    override val BarbarianAtDeployBar = ColorSchema.parse(
        85, 589, 1189, 717, "2FB2F1", "9|17|5C96F9,16|24|5AE6FE,8|36|202780,8|23|4DD4FC,9|12|223E85,4|8|2EA2DC,9|8|2EA6E2,12|24|52DCFD,24|36|62E1FD", 0, 0.9, "部署野蛮人"
    )
    // Additional barbarian deploy bar color variant
    override val BarbarianAtDeployBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "36B6F1", "8|0|3FCCFB,16|0|48C5F8,18|44|689BF1,37|47|5877C1,0|16|868FDA,8|16|5A89F3,16|16|619DFB,24|16|689DF1,32|16|73B2FB", 0, 0.9, "部署野蛮人"
    )
    override val GiantAtDeployBar = ColorSchema.parse(
        85, 589, 1189, 717, "82B5FC", "13|15|6A9FF3,24|8|77AAF8,24|-12|4A71B3,8|-21|56A1FC,-15|-21|3C91FC,-23|-2|465B92,-12|3|5983D1,-3|12|3E5497,22|31|6CACF8", 0, 0.9, "部署巨人"
    )
    // Additional giant deploy bar color variant
    override val GiantAtDeployBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "154AA2", "10|0|0944A8,21|0|3454A8,29|40|7AAFF9,42|31|669AF0,0|16|608EE0,10|16|5463A2,21|16|A3C8FD,31|16|6EA0F3,41|16|76ACF6", 0, 0.9, "部署巨人"
    )
    override val ArcherAtDeployBar = ColorSchema.parse(
        85, 589, 1189, 717, "662DBC", "5|12|91A8FB,4|24|5368AD,-8|21|6070B9,-22|5|210E4D,-18|-14|7D3BBC,-8|-18|6F30C0,4|-19|672CC1,7|-14|431A8A,6|-6|331270", 0, 0.9, "部署弓箭手"
    )
    // Additional archer deploy bar color variant
    override val ArcherAtDeployBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "8E43C3", "9|0|6D2DB9,17|0|662BBA,25|0|662DBB,20|37|431D87,24|58|199D7D,9|15|535A84,17|15|481A75,25|15|652BBD,34|15|672BC2", 0, 0.9, "部署弓箭手"
    )
    // Dragon deploy bar color variant
    override val DragonAtDeployBar2 = ColorSchema.parse(
        85, 589, 1189, 717, "C75161", "9|0|B86A75,18|0|562D87,27|26|6035E8,30|44|C0505B,0|12|2A0578,9|12|6A243A,18|12|712C4E,27|12|D35868,36|12|AE4857", 0, 0.9, "部署飞龙"
    )

}
