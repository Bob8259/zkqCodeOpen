@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseAttackColors {
    val BuilderBaseGold: ColorSchema
    val BuilderBaseExiler: ColorSchema
    val AttackNow: ColorSchema
    val TrainTroopsWarning: ColorSchema
    val CancelAttackSearch: ColorSchema
    val ExitBattleButton: ColorSchema
    val NightWitch: ColorSchema
    val TroopSkills: ColorSchema
    val MachineSkills: ColorSchema
    val BuilderBaseBarbarian: ColorSchema
    val TroopsWithSkills: ColorSchema
    val TroopsWithOutSkills: ColorSchema

    // Star detection for stop/exit conditions
    val BuilderBaseStarsAvailable: ColorSchema
    val BuilderBaseOneStar1: ColorSchema
    val BuilderBaseOneStar2: ColorSchema

    // Night world 2x battle speed-up button (appears after troops deployed)
    val BuilderBaseBattleSpeedUp: ColorSchema
}

object BuilderBaseAttackColors : IBuilderBaseAttackColors {
    override val BuilderBaseGold = ColorSchema.parse(
        1000, 20, 1270, 80, "72DCF4", "0|1|72DCF4,0|2|72DCF4,0|3|72DCF4,0|4|72DCF4,0|5|72DCF4", 0, 0.95
    )
    override val BuilderBaseExiler = ColorSchema.parse(
        1000, 50, 1270, 140, "DF93B2", "-1|1|DF93B2,-1|2|DF93B2,-1|3|DF93B2,-1|4|DF93B2,-1|5|DF93B2", 0, 0.95
    )
    override val AttackNow = ColorSchema.parse(
        825, 431, 1075, 518, "86E9BA", "29|1|85E8B9,64|0|86E9BA,120|-2|89E9BC,158|-5|8CEABE,14|51|3AD48B,54|46|3AD48B,91|51|3AD48B,158|53|3AD38B,188|46|3AD48B", 0, 0.9, "夜世界立即进攻"
    )
    override val TrainTroopsWarning = ColorSchema.parse(
        491, 198, 596, 216, "2021FE", "1|0|2122FE,5|0|2122FE,9|0|2021FE,9|3|3335F6,6|3|3335F6,3|3|292AFC,1|3|3435F6,0|3|3335F6,-1|3|3335F6", 0, 0.95, "练兵警告"
    )
    override val CancelAttackSearch = ColorSchema.parse(
        569, 611, 714, 649, "7D77FE", "29|0|7D77FE,71|-3|8079FF,87|0|7D77FE,116|0|7D77FE,0|19|110FDB,29|19|110FDB,58|19|110FDB,87|19|110FDB,116|19|110FDB", 0, 0.9, "取消搜索"
    )
    override val ExitBattleButton = ColorSchema.parse(
        33, 491, 153, 518, "5F5DF4", "24|0|5F5DF4,18|2|5F5DF2,72|0|5F5DF4,96|0|5F5DF4,0|13|0E0DCE,24|13|0E0DCE,46|18|0E0DCF,72|13|0E0DCE,96|13|0E0DCE", 0, 0.9, "红色退出对战"
    )
    override val NightWitch = ColorSchema.parse(
        190, 587, 800, 712, "191814", "9|0|141413,19|0|292E32,29|0|2E3438,38|0|A5A4CB,0|17|836F5D,9|17|745F48,19|17|2E3438,29|17|AEAFDE,38|17|9497DA", 0, 0.9
    )
    override val TroopSkills = ColorSchema.parse(
        189, 570, 1241, 600, "FF44C9", "7|0|FF44C9,14|0|FF44C9,20|0|FF44C9,27|0|FF44C9,0|7|FF69D1,7|7|FF69D1,14|7|FF69D1,20|7|FF69D1,27|7|FF69D1", 0, 0.9, "开部队技能"
    )
    override val MachineSkills = ColorSchema.parse(
        139, 553, 160, 563, "FF35CF", "4|0|FF35CF,9|0|FF35CF,13|0|FF35CF,17|0|FF35CF,0|5|FF49D4,4|5|FF49D4,9|5|FF49D4,13|5|FF49D4,17|5|FF49D4", 0, 0.8, "机器技能"
    )
    override val BuilderBaseBarbarian = ColorSchema.parse(
        193, 585, 1261, 623, "FF763A", "7|0|FF763A,15|0|FF773B,23|0|FF783C,30|0|FF793C,0|6|FF773A,7|6|FF793C,15|6|FF7B3E,23|6|FF7D40,30|6|FF7E41", 0, 0.9, "夜世界野蛮人"
    )

    // Troops with/without skills indicators
    override val TroopsWithSkills = ColorSchema.parse(
        80, 570, 1210, 630, "FE41C8", "7|0|FE41C8,14|0|FE41C8,21|0|FE41C8,28|0|FE41C8,0|9|FF74D4,7|9|FF74D4,14|9|FF74D4,21|9|FF74D4,28|9|FF74D4", 1, 0.9, "带技能部队"
    )
    override val TroopsWithOutSkills = ColorSchema.parse(
        80, 570, 1210, 630, "FF763A", "7|0|FF763A,17|1|FF763A,22|2|FF773B,26|2|FF773B,24|8|FF7C3F,16|12|FF7E41,8|13|FF7C3F,3|12|FF793C,1|9|FF773A", 1, 0.9, "无技能部队"
    )

    // Star detection for stop/exit conditions
    override val BuilderBaseStarsAvailable = ColorSchema.parse(
        23, 574, 142, 614, "FFFFFF", "1|12|FFFFFF,5|24|858174,19|19|286690,85|16|2A6993,93|4|2D6F96,-9|12|A09B91,-3|22|F1EFE9,0|13|FFFFFF,8|19|BFB9AF", 0, 0.9, "夜世界胜利之星"
    )
    override val BuilderBaseOneStar1 = ColorSchema.parse(
        543, 262, 735, 455, "71EAFF", "-33|29|73EAFF,-17|61|3CADF2,20|63|2E9FE9,33|29|2696E7,-12|25|73EAFF,9|24|47BDFF,-20|46|6BE7FF,21|44|2C9CE9,4|38|36A6EF", 0, 0.9, "夜世界一星1"
    )
    override val BuilderBaseOneStar2 = ColorSchema.parse(
        1068, 521, 1102, 557, "DFFFFF", "-5|19|1167C5,4|20|002098,4|13|2C9EEA,-1|10|58D0FF,-3|8|74E9FF,-9|10|003BAB,4|21|011B93,10|9|207FD5,-5|5|BDF6FF", 0, 0.9, "夜世界一星2"
    )

    override val BuilderBaseBattleSpeedUp = ColorSchema.parse(
        1197, 374, 1263, 446, "7FF4D8", "13|0|7FF4D8,26|0|7FF4D8,39|0|7FF4D8,52|0|7FF4D8,0|36|2DC77E,13|36|2DC77E,26|36|2DC77E,39|36|2DC77E,52|36|2DC77E", 0, 0.9, "夜世界对战加速按钮"
    )
}
