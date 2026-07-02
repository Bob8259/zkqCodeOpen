@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBasePassRewardColors {
    val PassRewardClaimButton: ColorSchema
    val PassRewardLeftButton: ColorSchema
    val PassRewardRightButton: ColorSchema
    val PassRewardChoiceButton: ColorSchema
    val PassRewardSellGemsButton: ColorSchema
    val PassRewardChestContinue: ColorSchema
}

object MainBasePassRewardColors : IMainBasePassRewardColors {
    override val PassRewardClaimButton = ColorSchema.parse(
        325, 376, 1272, 434,
        "FFFFFF",
        "-7|0|E79A2E,7|0|E79A2E,0|8|7D511F,0|-8|FFB734,-7|-8|FFB734,6|-7|FFB734,6|5|CD7F2B,-7|5|CD7F2B,-4|7|C17429",
        0, 0.9, "令牌奖励领取按钮"
    )

    override val PassRewardLeftButton = ColorSchema.parse(
        358, 265, 427, 337,
        "31C282",
        "-9|-12|61E3B4,8|-12|4FDEA8,15|-26|65E7BD,8|-13|50E0AA,7|-4|33D18B,1|3|37C07F,-6|-4|33C785,-8|-15|75E3B2,-11|-11|58E4B0",
        0, 0.9, "令牌奖励左按钮"
    )

    override val PassRewardRightButton = ColorSchema.parse(
        1186, 267, 1253, 336,
        "53DFAB",
        "3|8|32C482,10|10|31C483,15|7|33D08A,15|1|4DDDA7,19|-5|55E1AF,24|-14|6BE9C0,6|12|31BB7A,11|12|31BE7E,9|8|32C987",
        0, 0.9, "令牌奖励右按钮"
    )

    override val PassRewardChoiceButton = ColorSchema.parse(
        410, 547, 867, 608,
        "8DEABE",
        "25|0|8DEABE,50|0|8DEABE,74|0|8DEABE,99|0|8DEABE,0|27|36C07D,25|27|36C07D,50|27|36C07D,74|27|36C07D,99|27|36C07D",
        0, 0.9, "令牌奖励抉择按钮"
    )

    override val PassRewardSellGemsButton = ColorSchema.parse(
        551, 507, 734, 585,
        "7BF2D4",
        "37|0|7BF1D3,74|0|7BF1D3,110|0|7BF1D3,147|0|7BF1D3,0|39|2FBA70,37|39|2FBA71,74|39|2FBA71,110|39|2FBA71,147|39|30BA71",
        0, 0.9, "令牌奖励变卖宝石按钮"
    )

    override val PassRewardChestContinue = ColorSchema.parse(
        553, 566, 736, 628,
        "8AE9BD",
        "37|0|8AE9BC,74|0|8AEABD,110|0|8AE9BC,147|0|8AE9BC,0|31|3AD48B,37|31|3AD48B,74|31|3AD48B,110|31|3AD48B,147|31|3AD48B",
        0, 0.9, "令牌奖励宝箱继续按钮"
    )
}
