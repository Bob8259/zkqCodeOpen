@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object BuilderBaseTroops {
    val RAGED_BARBARIAN = SettingDef("raged_barbarian", "狂暴野蛮人", 1, "MAIN_BASE_SETTINGS")
    val SNEAKY_ARCHER = SettingDef("sneaky_archer", "隐秘弓箭手", 1, "MAIN_BASE_SETTINGS")
    val BETA_MINION = SettingDef("beta_minion", "异变亡灵", 1, "MAIN_BASE_SETTINGS")
    val BOMBER = SettingDef("bomber", "夜世界炸弹兵", 1, "MAIN_BASE_SETTINGS")
    val BABY_DRAGON = SettingDef("baby_dragon", "夜世界龙宝", 1, "MAIN_BASE_SETTINGS")
    val CANNON_CART = SettingDef("cannon_cart", "加农炮战车", 1, "MAIN_BASE_SETTINGS")
    val POWER_PEKKA = SettingDef("power_pekka", "雷霆皮卡", 1, "MAIN_BASE_SETTINGS")
    val BOXER_GIANT = SettingDef("boxer_giant", "巨人拳击手", 1, "MAIN_BASE_SETTINGS")
    val NIGHT_WITCH = SettingDef("night_witch", "暗夜女巫", 1, "MAIN_BASE_SETTINGS")
    val DROP_SHIP = SettingDef("drop_ship", "骷髅气球", 1, "MAIN_BASE_SETTINGS")
    val HOG_GLIDER = SettingDef("hog_glider", "飞猪骑士", 1, "MAIN_BASE_SETTINGS")
    val ELECTROFIRE_WIZARD = SettingDef("electrofire_wizard", "电火法师", 1, "MAIN_BASE_SETTINGS")

    val all = listOf(
        RAGED_BARBARIAN,
        BOXER_GIANT,
        BOMBER,
        CANNON_CART,
        DROP_SHIP,
        HOG_GLIDER,
        SNEAKY_ARCHER,
        BETA_MINION,
        BABY_DRAGON,
        NIGHT_WITCH,
        POWER_PEKKA,
        ELECTROFIRE_WIZARD
    )
}
