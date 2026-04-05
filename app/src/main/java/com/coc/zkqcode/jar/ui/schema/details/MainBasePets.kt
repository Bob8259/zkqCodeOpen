@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBasePets {
    // Keep the pet order aligned with the in-game upgrade list.
    val LASSI = SettingDef("lassi", "莱希", 1, "MAIN_BASE_PETS")
    val ELECTRO_OWL = SettingDef("electro_owl", "闪枭", 1, "MAIN_BASE_PETS")
    val MIGHTY_YAK = SettingDef("mighty_yak", "大牦", 1, "MAIN_BASE_PETS")
    val UNICORN = SettingDef("unicorn", "独角", 1, "MAIN_BASE_PETS")
    val FROSTY = SettingDef("frosty", "冰牙", 1, "MAIN_BASE_PETS")
    val DIGGY = SettingDef("diggy", "地兽", 1, "MAIN_BASE_PETS")
    val POISON_LIZARD = SettingDef("poison_lizard", "猛蜥", 1, "MAIN_BASE_PETS")
    val PHOENIX = SettingDef("phoenix", "凤凰", 1, "MAIN_BASE_PETS")
    val SPIRIT_FOX = SettingDef("spirit_fox", "灵狐", 1, "MAIN_BASE_PETS")
    val ANGRY_JELLY = SettingDef("angry_jelly", "愤怒水母", 1, "MAIN_BASE_PETS")
    val SNEEZY = SettingDef("sneezy", "阿啾", 1, "MAIN_BASE_PETS")
    val GREEDY_RAVEN = SettingDef("greedy_raven", "贪婪渡鸦", 1, "MAIN_BASE_PETS")

    val all = listOf(
        LASSI,
        ELECTRO_OWL,
        MIGHTY_YAK,
        UNICORN,
        FROSTY,
        DIGGY,
        POISON_LIZARD,
        PHOENIX,
        SPIRIT_FOX,
        ANGRY_JELLY,
        SNEEZY,
        GREEDY_RAVEN
    )
}
