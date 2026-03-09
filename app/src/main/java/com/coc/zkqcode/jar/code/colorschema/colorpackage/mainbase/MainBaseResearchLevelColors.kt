@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseResearchLevelColors {
    // Level number color schemas used to detect which upgrade level a research item is at.
    // Add a new property for each level that needs to be detected.
    val RESEARCH_LEVEL_1: ColorSchema
    val RESEARCH_LEVEL_2: ColorSchema
    val RESEARCH_LEVEL_3: ColorSchema
    val RESEARCH_LEVEL_4: ColorSchema
    val RESEARCH_LEVEL_5: ColorSchema
    val RESEARCH_LEVEL_6: ColorSchema
    val RESEARCH_LEVEL_7: ColorSchema
    val RESEARCH_LEVEL_8: ColorSchema
    val RESEARCH_LEVEL_9: ColorSchema
    val RESEARCH_LEVEL_10: ColorSchema
    val RESEARCH_LEVEL_11: ColorSchema
    val RESEARCH_LEVEL_12: ColorSchema
    val RESEARCH_LEVEL_13: ColorSchema
}

object MainBaseResearchLevelColors : IMainBaseResearchLevelColors {
    // Detects a level-1 badge: white text on a light background
    override val RESEARCH_LEVEL_1: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "0|1|FFFFFF,0|2|FFFFFF,0|3|FFFFFF,0|4|FFFFFF,0|5|FFFFFF,0|6|FFFFFF,0|7|FFFFFF,0|8|FFFFFF,0|9|F4F4F4", 0, 0.93
    )

    // Detects a level-2 badge: near-white base with light gray surrounding pixels
    override val RESEARCH_LEVEL_2: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FBFBFB", "1|-2|F0F0F0,3|-2|E5E5E5,6|-2|F8F8F8,7|0|FEFEFE,1|6|FAFAFA,2|8|F6F6F6,4|8|F6F6F6,6|8|F6F6F6,7|8|F7F7F7", 0, 0.93
    )

    // Detects a level-3 badge: light gray base with medium gray and white surrounding pixels
    override val RESEARCH_LEVEL_3: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "F4F4F4", "2|-2|CBCBCB,5|-2|D4D4D4,7|0|FFFFFF,4|4|D6D6D6,5|4|F0F0F0,7|6|FEFEFE,5|9|F6F6F6,3|9|E7E7E7,1|8|F5F5F5", 0, 0.93
    )

    // Detects a level-4 badge: near-white base with off-white and light gray surrounding pixels
    override val RESEARCH_LEVEL_4: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FDFDFD", "0|-2|FCFCFC,-1|-4|FFFFFF,-1|-7|FEFEFE,-7|-3|FEFEFE,-6|-1|F0F0F0,-4|-1|F4F4F4,-4|-8|FAFAFA,-3|-8|EFEFEF,-2|-8|FCFCFC", 0, 0.93
    )

    // Detects a level-5 badge: light gray base with white and off-white surrounding pixels
    override val RESEARCH_LEVEL_5: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "EFEFEF", "-1|0|ECECEC,-3|0|EAEAEA,-5|0|EAEAEA,-6|2|F8F8F8,0|6|FFFFFF,0|8|FEFEFE,-1|10|F8F8F8,-2|11|E0E0E0,-4|11|E9E9E9", 0, 0.93
    )

    // Detects a level-6 badge: light gray base with dark gray and white surrounding pixels
    override val RESEARCH_LEVEL_6: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "F7F7F7", "-3|-1|C6C6C6,-6|0|EFEFEF,-7|2|FFFFFF,-7|5|FFFFFF,-7|7|FDFDFD,-2|9|F4F4F4,0|8|FDFDFD,-2|4|E1E1E1,-4|4|CFCFCF", 0, 0.93
    )

    // Detects a level-7 badge: medium gray base with white and off-white surrounding pixels
    override val RESEARCH_LEVEL_7: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "E3E3E3", "2|0|E2E2E2,3|0|E4E4E4,4|0|E6E6E6,4|2|FFFFFF,4|4|F7F7F7,3|6|F5F5F5,3|7|F9F9F9,3|8|EFEFEF,2|9|F8F8F8", 0, 0.93
    )

    // Detects a level-8 badge: near-white base with mixed gray surrounding pixels
    override val RESEARCH_LEVEL_8: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FBFBFB", "-2|3|FCFCFC,-4|3|DBDBDB,-6|3|F8F8F8,-8|6|FFFFFF,-7|8|F7F7F7,-5|9|F3F3F3,-4|9|F3F3F3,-1|7|FEFEFE,-1|6|FDFDFD", 0, 0.93
    )

    // Detects a level-9 badge: white base with off-white surrounding pixels
    override val RESEARCH_LEVEL_9: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "-2|0|E9E9E9,-7|-2|FFFFFF,-6|4|F0F0F0,-3|5|D3D3D3,0|4|F8F8F8,0|2|FDFDFD,1|1|F4F4F4,1|-1|FEFEFE,0|-3|FEFEFE", 0, 0.93
    )

    // Detects a level-10 badge: white base with cream and near-white surrounding pixels
    override val RESEARCH_LEVEL_10: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "0|3|FFFFFF,0|5|FFFFFF,7|9|F3F0E9,11|7|FFFFFF,12|3|FFFFFF,11|-1|FFFFFF,4|0|FFFFFD,4|2|FFFFFF,4|4|FFFFFF", 0, 0.93
    )

    // Detects a level-11 badge: white base with uniform white surrounding pixels in a grid pattern
    override val RESEARCH_LEVEL_11: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "0|1|FFFFFF,0|3|FFFFFF,0|5|FFFFFF,0|7|FFFFFF,5|7|FFFFFF,5|6|FFFFFF,5|4|FFFFFF,5|2|FFFFFF,5|0|FFFFFF", 0, 0.93
    )

    // Detects a level-12 badge: white base with light gray accents and near-white surrounding pixels
    override val RESEARCH_LEVEL_12: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "0|1|FFFFFF,0|3|FFFFFF,0|5|FFFFFF,10|7|F4F4F4,6|7|F4F4F4,5|5|FCFCFC,11|0|FCFCFC,9|-4|D9D9D9,7|-4|DCDCDC", 0, 0.93
    )

    // Detects a level-13 badge: white base with medium gray and near-white surrounding pixels
    override val RESEARCH_LEVEL_13: ColorSchema = ColorSchema.parse(
        195, 350, 1090, 650, "FFFFFF", "0|2|FFFFFF,0|4|FFFFFF,0|5|FFFFFF,0|6|FFFFFF,4|-1|DFDFDF,11|-2|FCFCFC,11|4|FBFBFB,10|7|EEEEEE,6|7|F2F2F2", 0, 0.93
    )
}
