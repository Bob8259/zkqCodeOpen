@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IEventColors {
    val BadgeEventContinueButton: ColorSchema
    val BadgeEventPageMarker: ColorSchema
    val BadgeEventClaimReward: ColorSchema
    val EventShopPageMarker: ColorSchema
    val EventShopAlreadyPurchased: ColorSchema
    val EventShopConfirmBuy: ColorSchema
    val ExtraReward: ColorSchema
    val BadgeEventEnterButton: ColorSchema
    val BadgeEventEnterButton2: ColorSchema
}

object EventColors : IEventColors {
    override val BadgeEventContinueButton = ColorSchema.parse(
        573, 627, 709, 677, "86EABA", "27|0|86EABA,54|0|87EABA,81|0|88EABA,108|0|86EABA,0|25|3BD48B,27|25|3BD48B,54|25|3BD48B,81|25|3BD48B,108|25|3BD48B", 0, 0.9, "奖章活动继续按钮"
    )

    override val BadgeEventPageMarker = ColorSchema.parse(
        1098, 34, 1141, 79, "8584FF", "9|0|FFFFFF,18|0|727572,26|0|E0E0E0,35|0|8584FF,0|23|231FF6,9|23|4F4C4F,18|23|131313,26|23|111112,35|23|231FF6", 0, 0.9, "奖章活动页面标记"
    )

    override val BadgeEventClaimReward = ColorSchema.parse(
        146, 590, 1139, 654, "9BF5C7", "-50|-2|9BF8CC,-55|31|48E198,-2|33|43E195,55|37|43D68F,59|3|95F8C7,35|2|95F8C7,35|27|48E198,-16|2|98F8CA,-26|25|48E198", 0, 0.9, "奖章活动领取奖励"
    )

    // Event Shop colors
    override val EventShopPageMarker = ColorSchema.parse(
        150, 54, 397, 105, "7E838C", "49|0|7E838C,99|0|7E838C,148|0|7E838C,197|0|7E838C,0|25|636973,49|25|636973,99|25|636973,148|25|636973,197|25|636973", 0, 0.9, "活动商店页面标记"
    )

    override val EventShopAlreadyPurchased = ColorSchema.parse(
        394, 264, 596, 317, "0D52A7", "41|0|0D52A7,81|0|0D5CAE,121|0|0D59AA,162|0|0D5AAC,0|27|0D5AAF,41|27|0D56A9,89|32|0D57A8,121|27|0D57A9,162|27|0D5AA9", 0, 0.9, "活动商店新装备已购买标记"
    )

    override val EventShopConfirmBuy = ColorSchema.parse(
        524, 405, 662, 503, "84E5BA", "27|0|84E5BA,55|0|84E5BA,83|0|84E5BA,110|0|84E5BA,0|49|2BCE86,27|49|2BCE86,55|49|2BCE86,83|49|2BCE86,110|49|2BCE86", 0, 0.9, "活动商店确认购买按钮"
    )

    //Badge Event Reward
    override val ExtraReward = ColorSchema.parse(
        242, 426, 1118, 526, "74EECC", "21|0|74EECC,42|0|6BDBBC,63|0|74EECC,84|0|74EECC,0|16|2DC87F,21|16|2DC87F,42|16|2DC87F,63|16|2DC87F,84|16|2DC87F", 0, 0.9, "额外奖励"
    )

    override val BadgeEventEnterButton = ColorSchema.parse(
        829, 134, 1058, 676, "80F4DA", "29|1|80F4D9,69|5|7CF3D5,108|8|79F2D2,102|32|2ECE86,79|41|2FBD74,131|-7|2615E8,131|-11|2216F3,147|-5|2815E3,145|-10|2216F1", 0, 0.9, "进入活动按钮"
    )

    override val BadgeEventEnterButton2 = ColorSchema.parse(
        829, 134, 1058, 676, "7EF3D7", "37|-5|82F5DB,85|-6|83F5DC,115|8|73EFCC,109|42|31B46B,138|-24|1818FF,140|-24|1818FF,141|-24|1818FF,142|-24|1818FF,143|-24|1818FF", 0, 0.9, "进入活动按钮2"
    )
}
