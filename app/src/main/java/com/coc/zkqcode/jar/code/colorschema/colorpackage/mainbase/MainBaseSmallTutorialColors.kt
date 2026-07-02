@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseSmallTutorialColors {
    val MainBaseSmallTutorial: ColorSchema
    val OuterPetIcon: ColorSchema
    val ClanCastleAddReinforcement: ColorSchema
    val RemoteGuardsIcon: ColorSchema
    val MeleeGuardsIcon: ColorSchema
}

object MainBaseSmallTutorialColors : IMainBaseSmallTutorialColors {
    // Down arrow used by early main-base tutorial prompts
    override val MainBaseSmallTutorial = ColorSchema.parse(
        164, 77, 1064, 605, "21ABFE", "7|0|1FB9FE,14|0|1EBBFE,20|0|1EB5FE,27|0|21A8FE,0|21|2CC8F9,7|21|28C7F9,14|21|26C5F9,20|21|25C2F8,27|21|28C2F9", 1, 0.9, "教程朝下箭头"
    )
    // Pet entry icon shown during the hero hall related tutorial flow
    override val OuterPetIcon = ColorSchema.parse(
        167, 503, 1115, 628, "32AFD2", "8|0|42BFDC,16|0|3DB9D7,23|0|39AECF,31|0|0D6EB4,0|10|32AFD2,8|10|3FBCDA,16|10|187AAA,23|10|36B0D0,31|10|2FA6CA", 0, 0.9, "宠物店按钮"
    )
    // Clan castle tutorial marker used when adding reinforcements
    override val ClanCastleAddReinforcement = ColorSchema.parse(
        171, 510, 1122, 634, "67778F", "9|0|7F8EAA,18|0|8C94A5,26|0|ABB6BE,35|0|E2D1B9,0|20|545A67,9|20|6C7388,18|20|A4B4C6,26|20|DCE1E5,35|20|E4D1BB", 0, 0.9, "部落城堡教程"
    )
    // TH18 remote guard tutorial icon
    override val RemoteGuardsIcon = ColorSchema.parse(
        165, 502, 1133, 632, "2F4B72", "7|0|5C4E55,13|0|546370,19|0|1E81D6,26|0|525B66,0|18|634C4F,7|18|ACC6D4,13|18|C8E4F1,19|18|C6E5F5,26|18|97ADBF", 0, 0.9, "远程守卫图标"
    )
    // TH18 melee guard tutorial icon
    override val MeleeGuardsIcon = ColorSchema.parse(
        165, 502, 1133, 632, "56281F", "6|0|FFD451,12|0|FFE13E,17|0|BCFFC4,23|0|43BFFF,0|17|8E9AAA,6|17|7C899A,12|17|748294,17|17|91AABA,23|17|9BB5C2", 0, 0.9, "近战守卫图标"
    )
}
