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

}
