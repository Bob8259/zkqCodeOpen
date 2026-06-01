@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseTournamentColors {
    val SetBaseIcon2: ColorSchema
    val SetBaseIcon3: ColorSchema
    val JoinTournament: ColorSchema
    val PlayTournament: ColorSchema
    val ConfirmTournamentAttack: ColorSchema
    val WaitForTournament: ColorSchema
    val GreenSword: ColorSchema
    val PreparingTournament: ColorSchema
    val PreparingTournament2: ColorSchema
    val TournamentNotAvailable: ColorSchema
}

object MainBaseTournamentColors : IMainBaseTournamentColors {
    // Set base icon (variant 2)
    override val SetBaseIcon2 = ColorSchema.parse(
        509, 133, 580, 181, "7ED3A3", "3|3|72CB98,9|7|7FD4A4,21|4|7AD1A4,6|-11|B78D7A,11|-7|FFEFE6,15|-14|EDE1DC,10|-15|FFEAE1,1|-18|FFF0E7,1|-15|FFF6EF", 0, 0.93, "布阵按钮2"
    )
    // Set base icon (variant 3)
    override val SetBaseIcon3 = ColorSchema.parse(
        508, 127, 579, 182, "884575", "14|0|CCBBB6,29|0|C7A18F,43|0|512A44,57|0|88406E,0|27|8B598C,14|27|4C8F6B,29|27|7FD4A4,43|27|101311,57|27|8D5B8D", 0, 0.9, "布阵按钮3"
    )
    // Join tournament button
    override val JoinTournament = ColorSchema.parse(
        435, 496, 705, 568, "D8963B", "54|0|D8963B,108|0|D8963B,162|0|D8963B,216|0|D8963B,0|36|D8963B,54|36|D8963B,108|36|D8963B,162|36|D8963B,216|36|D8963B", 0, 0.9, "锦标赛"
    )
    // Play tournament button
    override val PlayTournament = ColorSchema.parse(
        434, 496, 703, 571, "2DADF9", "28|0|2DADF9,54|-2|2DADF9,176|-4|2DADF9,206|-4|2DADF9,228|-4|2DADF9,7|34|2CADF9,28|35|2CADF9,178|37|2CADF9,235|33|2CADF9", 0, 0.9, "排位赛"
    )
    // Confirm tournament attack button
    override val ConfirmTournamentAttack = ColorSchema.parse(
        669, 442, 855, 515, "77F4D8", "37|0|78F5D8,74|0|78F5D7,111|0|78F5D7,148|0|78F5D7,0|37|1EBD6E,37|37|1FBD6E,74|37|1FBD6E,111|37|1FBD6E,148|37|1FBD6E", 0, 0.9, "确认排位赛进攻"
    )
    // Wait for tournament button
    override val WaitForTournament = ColorSchema.parse(
        434, 498, 706, 571, "9D9D9D", "20|0|9D9D9D,36|16|9D9D9D,180|18|9D9D9D,202|18|9D9D9D,213|11|9D9D9D,222|8|9D9D9D,218|26|9D9D9D,203|38|9D9D9D,192|37|9D9D9D", 0, 0.9, "排位赛等待"
    )
    // Green sword indicator
    override val GreenSword = ColorSchema.parse(
        491, 539, 542, 575, "15D85A", "3|-4|0DFFC6,9|-8|15CF5F,14|-13|15CF60,9|-14|30FFC2,6|-11|29FFB8,3|-7|22FFC4,2|-6|21FFC2,1|-2|10E15E,-2|-1|16D959", 0, 0.9, "绿色的剑"
    )
    // Preparing tournament indicator
    override val PreparingTournament = ColorSchema.parse(
        419, 117, 752, 623, "828282", "10|14|828282,12|-2|828282,184|-6|828282,196|12|828282,-22|-381|F6FCFD,-18|-373|E2DCC4,-12|-370|BBA57B,-15|-364|D8E0E3,-17|-362|F6FDFD", 0, 0.9, "排位赛准备中"
    )
    // Preparing tournament indicator (variant 2)
    override val PreparingTournament2 = ColorSchema.parse(
        418, 484, 725, 589, "878787", "14|20|828282,16|30|828282,29|53|828282,46|26|828282,207|3|838383,217|29|828282,228|41|828282,242|52|828282,244|54|828282", 0, 0.9, "排位赛准备中2"
    )
    // Tournament not available indicator
    override val TournamentNotAvailable = ColorSchema.parse(
        630, 342, 673, 390, "756B6A", "9|0|796F6F,18|0|7E7274,26|0|958B90,35|0|7E7273,0|24|706566,9|24|3F3339,18|24|3E3338,26|24|3F3338,35|24|706766", 0, 0.9, "排位赛未解锁"
    )
}
