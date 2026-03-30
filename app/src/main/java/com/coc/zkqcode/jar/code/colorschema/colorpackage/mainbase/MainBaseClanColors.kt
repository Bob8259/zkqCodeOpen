@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseClanColors {
    val IUnderstand: ColorSchema
    val JoinClanButton: ColorSchema
    val ExitClanButton: ColorSchema
    val ApplyClanSetting: ColorSchema
    val SearchOptions: ColorSchema
    val NotJoinClanFlag: ColorSchema
}

object MainBaseClanColors : IMainBaseClanColors {
    // "I Understand" confirmation button when joining a clan
    override val IUnderstand = ColorSchema.parse(
        128, 456, 358, 532, "78F1D0", "2|-27|383B41,48|-27|383B41,94|-27|383B41,140|-27|383B41,-44|11|55E0AD,2|11|55E0AD,48|11|54DEAC,94|11|55E0AD,140|11|55E0AD", 0, 0.9, "加部落我了解"
    )
    // Button to join a clan
    override val JoinClanButton = ColorSchema.parse(
        962, 475, 1122, 511, "71EDC9", "32|0|71EDC9,111|0|71EDC9,96|0|71EDC9,128|0|71EDC9,0|18|2EC178,32|18|2EC178,64|18|2EC178,96|18|2EC178,128|18|2EC178", 0, 0.9, "加入部落按钮"
    )
    // Button to exit / leave a clan
    override val ExitClanButton = ColorSchema.parse(
        966, 475, 1121, 505, "615DF8", "31|0|615DF8,62|0|5653DA,93|0|615DF8,124|0|615DF8,0|15|0E0DCF,31|15|0E0DCF,88|6|5F5DF2,93|15|0E0DCF,124|15|0E0DCF", 0, 0.9,
    )
    // Apply clan settings confirmation button
    override val ApplyClanSetting = ColorSchema.parse(
        560, 519, 767, 574, "59D89F", "41|0|59D89F,83|0|59D89F,124|0|59D89F,165|0|59D89F,0|27|2CCD84,41|27|2CCD84,83|27|2CCD84,124|27|2CCD84,165|27|2CCD84", 0, 0.9, "应用部落设置"
    )
    // Search options indicator when a clan has been found
    override val SearchOptions = ColorSchema.parse(
        768, 200, 938, 231, "0B3763", "34|0|0B3763,68|0|7AF1D2,102|0|7AF1D2,136|0|7AF1D2,0|15|0B3F6B,34|15|0B3F6B,68|15|2CCD84,102|15|2CCD84,136|15|2CCD84", 0, 0.9, "设置选项，已搜索到部落"
    )
    // Flag indicating the player has not joined a clan
    override val NotJoinClanFlag = ColorSchema.parse(
        40, 302, 63, 318, "BFD9F2", "5|0|C7DFF3,10|0|D9EFF5,14|0|0D40E3,19|0|0D4BE6,0|8|BFD9F2,5|8|C7DFF3,10|8|D9EFF5,14|8|0D40E3,19|8|0D4BE7", 0, 0.9
    )
}
