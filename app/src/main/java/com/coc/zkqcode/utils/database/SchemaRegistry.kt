package com.coc.zkqcode.utils.database

enum class Scope {
    GLOBAL,
    ACCOUNT,
    PROFILE
}

data class ConfigModule(
    val name: String,
    val scope: Scope,
    val settings: List<SettingDef>
)

object SchemaRegistry {
    val ALL_MODULES = listOf(
        ConfigModule("GLOBAL_SETTINGS", Scope.GLOBAL, Schema.GLOBAL_SETTINGS.all),
        ConfigModule("ACCOUNT_SETTINGS", Scope.ACCOUNT, Schema.ACCOUNT_SETTINGS.all),
        ConfigModule("MAIN_BASE_SETTINGS", Scope.PROFILE, Schema.MAIN_BASE_SETTINGS.all),
        ConfigModule(
            "MAIN_BASE_TROOPS_AND_SPELLS",
            Scope.PROFILE,
            Schema.MAIN_BASE_TROOPS_AND_SPELLS.all
        ),
        ConfigModule("MAIN_BASE_BUILDINGS", Scope.PROFILE, Schema.MAIN_BASE_BUILDINGS.all),
        ConfigModule("MAIN_BASE_PETS", Scope.PROFILE, Schema.MAIN_BASE_PETS.all),
        ConfigModule("NIGHT_BASE_SETTINGS", Scope.PROFILE, Schema.NIGHT_BASE_SETTINGS.all),
        ConfigModule("NIGHT_BASE_TROOPS", Scope.PROFILE, Schema.NIGHT_BASE_TROOPS.all),
        ConfigModule(
            "MAIN_BASE_BUILDING_PRIORITIES",
            Scope.PROFILE,
            Schema.MAIN_BASE_BUILDING_PRIORITIES.all
        ),
        ConfigModule("NIGHT_BASE_BUILDINGS", Scope.PROFILE, Schema.NIGHT_BASE_BUILDINGS.all),
        ConfigModule(
            "NIGHT_BASE_BUILDINGS_PRIORITY",
            Scope.PROFILE,
            Schema.NIGHT_BASE_BUILDINGS_PRIORITY.all
        ),
    )
}
