@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseHelperColors {
    val ResearchHelperIcon: ColorSchema
    val ResearchHelperIcon2: ColorSchema
    val BuilderHelperIcon: ColorSchema
    val InnerResearchHelper: ColorSchema
    val InnerBuilderHelper: ColorSchema
    val LockedResearchHelper: ColorSchema
    val LockedBuilderHelper: ColorSchema
    val AssignHelper: ColorSchema
}

object MainBaseHelperColors : IMainBaseHelperColors {
    // Outer button used to open the research helper flow
    override val ResearchHelperIcon = ColorSchema.parse(
        171, 492, 1054, 631, "353F5C", "5|0|48547E,11|0|374570,16|0|333F5F,21|0|6686CF,0|21|CEBFC8,5|21|32416B,11|21|6E89BC,16|21|435379,21|21|4E669E", 0, 0.9, "实验助手外部按钮"
    )
    override val ResearchHelperIcon2 = ColorSchema.parse(
        322, 498, 1032, 633, "24273D", "5|0|3C466D,9|0|222945,13|0|1E2339,18|0|334161,0|19|F5DCE5,5|19|394C7E,9|19|5D78B1,13|19|5972AD,18|19|546DA9", 0, 0.9, "实验助手外部按钮2"
    )

    // Outer button used to open the builder helper flow
    override val BuilderHelperIcon = ColorSchema.parse(
        171, 492, 1054, 631, "6FB9F3", "6|2|5A9DD2,16|4|9EA4B5,24|5|6EB1E8,25|17|2A4AA0,17|24|85A0ED,12|18|ABD4FF,7|29|88B2EF,10|32|8FBCF0,21|32|95C3F5", 0, 0.9, "工人助手外部按钮"
    )

    // Research helper icon shown inside the helper dialog
    override val InnerResearchHelper = ColorSchema.parse(
        134, 566, 571, 659, "2F344B", "6|0|465681,13|0|2F344B,19|0|333C55,25|0|36405E,0|26|7B7486,6|26|2D364E,13|26|4F679D,19|26|53679A,25|26|516596", 0, 0.9, "里面的实验助手"
    )

    // Builder helper icon shown inside the helper dialog
    override val InnerBuilderHelper = ColorSchema.parse(
        126, 557, 581, 662, "3896FF", "6|7|368FFF,17|25|A0E9FF,7|35|ABEEFF,9|21|B0F6FF,29|49|5779AB,37|46|524A4D,33|50|5D80B5,30|50|5071A6,33|48|4F74AE", 0, 0.9, "里面的建筑助手"
    )

    // Disabled research helper state shown before unlocking
    override val LockedResearchHelper = ColorSchema.parse(
        134, 566, 571, 659, "30324E", "11|0|121828,23|9|6B83B3,9|12|576FA8,1|13|657FB7,30|44|0EC3FD,40|46|0E243A,51|42|0EC2FD,51|49|0EC0FB,31|53|0EC1FC", 0, 0.9, "锁住的实验助手"
    )

    // Disabled builder helper state shown before unlocking
    override val LockedBuilderHelper = ColorSchema.parse(
        126, 557, 581, 662, "78AAFF", "2|17|96C4FF,-9|11|788CDD,-2|-14|326DE3,19|3|375BC9,13|22|AFF1FF,28|31|0EC1FC,37|34|0E243A,47|31|0EC0FC,47|37|0EBEFB", 0, 0.9, "锁住的工人学徒"
    )

    // Confirmation button used to assign the selected helper
    override val AssignHelper = ColorSchema.parse(
        1031, 284, 1126, 314, "89E9BC", "19|0|89E9BC,38|0|6EB494,57|0|89E9BC,76|0|89E9BC,0|15|3AD48B,19|15|3AD48B,67|14|3AD48B,57|15|3AD48B,76|15|3AD48B", 0, 0.9, "指派助手"
    )
}
