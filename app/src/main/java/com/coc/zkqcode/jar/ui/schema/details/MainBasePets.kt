@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBasePets {
    // Keep the pet order aligned with the in-game upgrade list.
    val LASSI = SettingDef("lassi", "莱希", "L.A.S.S.I", 1, "MAIN_BASE_PETS")
    val ELECTRO_OWL = SettingDef("electro_owl", "闪枭", "Electro Owl", 1, "MAIN_BASE_PETS")
    val MIGHTY_YAK = SettingDef("mighty_yak", "大牦", "Mighty Yak", 1, "MAIN_BASE_PETS")
    val UNICORN = SettingDef("unicorn", "独角", "Unicorn", 1, "MAIN_BASE_PETS")
    val FROSTY = SettingDef("frosty", "冰牙", "Frosty", 1, "MAIN_BASE_PETS")
    val DIGGY = SettingDef("diggy", "地兽", "Diggy", 1, "MAIN_BASE_PETS")
    val POISON_LIZARD = SettingDef("poison_lizard", "猛蜥", "Poison Lizard", 1, "MAIN_BASE_PETS")
    val PHOENIX = SettingDef("phoenix", "凤凰", "Phoenix", 1, "MAIN_BASE_PETS")
    val SPIRIT_FOX = SettingDef("spirit_fox", "灵狐", "Spirit Fox", 1, "MAIN_BASE_PETS")
    val ANGRY_JELLY = SettingDef("angry_jelly", "愤怒水母", "Angry Jelly", 1, "MAIN_BASE_PETS")
    val SNEEZY = SettingDef("sneezy", "阿啾", "Sneezy", 1, "MAIN_BASE_PETS")
    val GREEDY_RAVEN = SettingDef("greedy_raven", "贪婪渡鸦", "Greedy Raven", 1, "MAIN_BASE_PETS")

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

// Pet priority definitions — mirrors MainBasePets.all order
object MainBasePetPriorities {
    val LASSI = SettingDef("lassi_pet_priority", "莱希", "L.A.S.S.I", 1, "MAIN_BASE_PET_PRIORITIES")
    val ELECTRO_OWL = SettingDef("electro_owl_pet_priority", "闪枭", "Electro Owl", 2, "MAIN_BASE_PET_PRIORITIES")
    val MIGHTY_YAK = SettingDef("mighty_yak_pet_priority", "大牦", "Mighty Yak", 3, "MAIN_BASE_PET_PRIORITIES")
    val UNICORN = SettingDef("unicorn_pet_priority", "独角", "Unicorn", 4, "MAIN_BASE_PET_PRIORITIES")
    val FROSTY = SettingDef("frosty_pet_priority", "冰牙", "Frosty", 5, "MAIN_BASE_PET_PRIORITIES")
    val DIGGY = SettingDef("diggy_pet_priority", "地兽", "Diggy", 6, "MAIN_BASE_PET_PRIORITIES")
    val POISON_LIZARD = SettingDef("poison_lizard_pet_priority", "猛蜥", "Poison Lizard", 7, "MAIN_BASE_PET_PRIORITIES")
    val PHOENIX = SettingDef("phoenix_pet_priority", "凤凰", "Phoenix", 8, "MAIN_BASE_PET_PRIORITIES")
    val SPIRIT_FOX = SettingDef("spirit_fox_pet_priority", "灵狐", "Spirit Fox", 9, "MAIN_BASE_PET_PRIORITIES")
    val ANGRY_JELLY = SettingDef("angry_jelly_pet_priority", "愤怒水母", "Angry Jelly", 10, "MAIN_BASE_PET_PRIORITIES")
    val SNEEZY = SettingDef("sneezy_pet_priority", "阿啾", "Sneezy", 11, "MAIN_BASE_PET_PRIORITIES")
    val GREEDY_RAVEN = SettingDef("greedy_raven_pet_priority", "贪婪渡鸦", "Greedy Raven", 12, "MAIN_BASE_PET_PRIORITIES")

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
