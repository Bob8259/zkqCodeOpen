package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseAttackColors {
    val BuilderBaseGold: ColorSchema
    val BuilderBaseExiler: ColorSchema
    val AttackNow: ColorSchema
    val TrainTroopsWarning: ColorSchema
    val CancelAttackSearch: ColorSchema
    val SwitchTroopButton: ColorSchema
    val ExitBattleButton: ColorSchema
    val NightWitch: ColorSchema
    val TroopSkills: ColorSchema
    val MachineSkills: ColorSchema
    val BuilderBaseBarbarian: ColorSchema
}

object BuilderBaseAttackColors : IBuilderBaseAttackColors {
    override val BuilderBaseGold = ColorSchema.parse(
        1004, 26, 1026, 60, "0DC0E7", "0|1|0DC0E7,0|2|0DC0E7,0|3|0DC0E7,0|4|0DC0E7,0|5|0DC0E7,0|6|0DC0E7,0|7|0DC0E7,0|8|0DC0E7,0|9|0DC0E7", 0, 0.98
    )
    override val BuilderBaseExiler = ColorSchema.parse(
        1008, 93, 1025, 128, "C54579", "0|1|C54579,0|2|C54579,0|3|C54579,0|4|C54579,0|5|C54579,0|6|C54579,0|7|C54579,0|8|C54579,0|9|C54579", 0, 0.98
    )
    override val AttackNow = ColorSchema.parse(
        825, 431, 1075, 518, "86E9BA", "29|1|85E8B9,64|0|86E9BA,120|-2|89E9BC,158|-5|8CEABE,14|51|3AD48B,54|46|3AD48B,91|51|3AD48B,158|53|3AD38B,188|46|3AD48B", 0, 0.9, "夜世界立即进�?
    )
    override val TrainTroopsWarning = ColorSchema.parse(
        491, 198, 596, 216, "2021FE", "1|0|2122FE,5|0|2122FE,9|0|2021FE,9|3|3335F6,6|3|3335F6,3|3|292AFC,1|3|3435F6,0|3|3335F6,-1|3|3335F6", 0, 0.95, "练兵警告"
    )
    override val CancelAttackSearch = ColorSchema.parse(
        569, 611, 714, 649, "7D77FE", "29|0|7D77FE,71|-3|8079FF,87|0|7D77FE,116|0|7D77FE,0|19|110FDB,29|19|110FDB,58|19|110FDB,87|19|110FDB,116|19|110FDB", 0, 0.9, "取消搜索"
    )
    override val SwitchTroopButton = ColorSchema.parse(
        116, 663, 1100, 709, "F3F3F3", "2|0|F3F3F3,9|-4|86E0B4,20|-2|F4F4F4,20|6|E2DEDC,8|14|22A565,-7|13|23AA67,0|-1|F3F3F3,9|-4|86E0B4,9|-6|86E0B4", 0, 0.9, "切换部队标志"
    )
    override val ExitBattleButton = ColorSchema.parse(
        33, 491, 153, 518, "5F5DF4", "24|0|5F5DF4,18|2|5F5DF2,72|0|5F5DF4,96|0|5F5DF4,0|13|0E0DCE,24|13|0E0DCE,46|18|0E0DCF,72|13|0E0DCE,96|13|0E0DCE", 0, 0.9, "红色退出对�?
    )
    override val NightWitch = ColorSchema.parse(
        190, 587, 800, 712, "191814", "9|0|141413,19|0|292E32,29|0|2E3438,38|0|A5A4CB,0|17|836F5D,9|17|745F48,19|17|2E3438,29|17|AEAFDE,38|17|9497DA", 0, 0.9, "部署暗夜女巫"
    )
    override val TroopSkills = ColorSchema.parse(
        189, 570, 1241, 600, "FF44C9", "7|0|FF44C9,14|0|FF44C9,20|0|FF44C9,27|0|FF44C9,0|7|FF69D1,7|7|FF69D1,14|7|FF69D1,20|7|FF69D1,27|7|FF69D1", 0, 0.9, "开部队技�?
    )
    override val MachineSkills = ColorSchema.parse(
        139, 553, 160, 563, "FF35CF", "4|0|FF35CF,9|0|FF35CF,13|0|FF35CF,17|0|FF35CF,0|5|FF49D4,4|5|FF49D4,9|5|FF49D4,13|5|FF49D4,17|5|FF49D4", 0, 0.9, "机器技�?
    )
    override val BuilderBaseBarbarian = ColorSchema.parse(
        193, 585, 1261, 623, "FF763A", "7|0|FF763A,15|0|FF773B,23|0|FF783C,30|0|FF793C,0|6|FF773A,7|6|FF793C,15|6|FF7B3E,23|6|FF7D40,30|6|FF7E41", 0, 0.9, "夜世界野蛮人"
    )
}
