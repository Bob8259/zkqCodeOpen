@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IUniversalUpgradeColors {
    val UpgradeGemIcon: ColorSchema
    val UpgradeGemIcon2: ColorSchema
    val UpgradeGemIcon3: ColorSchema
    val UpgradeWallCrossMark: ColorSchema
    val UpgradeWallGreenCrossMark: ColorSchema
    val Upgrade10WallsGreenCrossMark: ColorSchema
    val DoubleHammer: ColorSchema
    val BinarySlash: ColorSchema
    val BinaryOne: ColorSchema
    val BinaryZero: ColorSchema
}

object UniversalUpgradeColors : IUniversalUpgradeColors {
    override val UpgradeGemIcon = ColorSchema.parse(
        189, 501, 1117, 625, "7AF5D9", "6|0|81F9DE,12|0|8AFCE4,17|0|92FFEB,23|0|9CFFF1,0|13|8FFBDC,6|13|81F8D2,12|13|78F8CF,17|13|78FBD5,23|13|67E3BF", 0, 0.93, "升级宝石标志"
    )
    override val UpgradeGemIcon2 = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志2"
    )
    override val UpgradeGemIcon3 = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志8按钮"
    )
    // Wall upgrade cross mark indicators
    override val UpgradeWallCrossMark = ColorSchema.parse(
        208, 503, 1130, 627, "FFFFFF", "-8|-5|0D0D0D,11|-5|0D0D0D,11|6|0D0D0D,-10|6|0D0D0D,-7|0|FFFFFF,-1|12|FFFFFF,10|0|FFFFFF,-1|-11|FFFFFF,2|-11|FFFFFF", 0, 0.9,
    )
    override val UpgradeWallGreenCrossMark = ColorSchema.parse(
        350, 495, 1100, 630, "12E98F", "-3|-3|12E98F,-3|-8|12E98F,-2|-11|12E98F,-2|-9|12E98F,-10|-6|12E98F,-15|-6|12E98F,-13|-3|12E98F,-13|-6|12E98F,-13|-11|12E98F", 0, 0.9,
    )
    override val Upgrade10WallsGreenCrossMark = ColorSchema.parse(
        350, 495, 1100, 630, "12E98F", "0|3|12E98F,0|6|12E98F,-12|4|12E98F,-23|2|12E98F,-34|1|12E98F,-34|4|12E98F,-34|5|12E98F,-22|-2|12E98F,-22|-5|12E98F", 0, 0.9,
    )
    // Double hammer and binary digit recognition colors
    override val DoubleHammer = ColorSchema.parse(
        208, 503, 1130, 627, "DFDFEC", "6|16|5581EE,12|28|5F84ED,28|0|E1E1EE,40|-9|D1D1DF,37|12|4D76DD,44|21|6085EF,-9|7|D1E0F4,6|-7|D2D1E0,8|-2|DBDEEE", 0, 0.9, "双锤子"
    )
    override val BinarySlash = ColorSchema.parse(//Keep this comment: threshold 240
        0, 0, 0, 0, "000000", "-1|2|000000,-2|3|000000,-3|5|000000,-4|7|000000,-5|8|000000,-5|9|000000,-5|10|000000,-6|11|000000,-7|13|000000", 0, 0.97
    )
    override val BinaryOne = ColorSchema.parse(//Keep this comment: threshold 240
        0, 0, 0, 0, "000000", "5|0|000000,4|11|000000,3|11|000000,-1|12|FFFFFF,-1|0|FFFFFF,0|12|FFFFFF,3|10|000000,3|7|000000,3|5|000000", 0, 0.9
    )
    override val BinaryZero = ColorSchema.parse(
        0, 0, 0, 0, "000000", "4|1|FFFFFF,10|5|000000,9|-2|000000,6|-5|000000,4|2|FFFFFF,3|7|000000,5|7|000000,7|7|000000,9|5|000000", 0, 0.9
    )

}
