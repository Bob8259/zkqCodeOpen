package com.coc.zkqcode.jar.code.universal.smalltools.data

/**
 * Centralized storage key management for persistent data.
 * All storage keys used with readMemory/writeMemory should be defined here.
 */
object StorageKeys {
    // Global keys (no account suffix)
    const val ACCOUNT_NUMBER = "accountNumber"

    // Per-account keys (use withAccountNumber to add account suffix)
    const val MAIN_BASE_REMOVE_OBSTACLES = "MainBaseRemoveObstacles"
    const val BUILDER_BASE_REMOVE_OBSTACLES = "BuilderBaseRemoveObstacles"
    const val CHECK_NEW_BUILDING_ARROWS = "CheckNewBuildingArrows"
    const val MAIN_BASE_CHECK_TUTORIALS = "MainBaseCheckTutorials"
    const val MAIN_BASE_TRAIN_TROOPS = "MainBaseTrainTroops"
    const val BUILDER_BASE_TRAIN_TROOPS = "BuilderBaseTrainTroops"
    const val CLICK_OTTOS_POST = "ClickOttosPost"
    const val TURN_ON_CLOCK_TOWER = "TurnOnClockTower"
    const val JOIN_CLAN = "JoinClan"
    const val PLACE_HERO_BANNERS = "PlaceHeroBanners"
    const val COLLECT_CLAN_CASTLE_RESOURCES = "CollectClanCastleResources"
    const val USE_MAGIC_ITEMS = "UseMagicItems"
    const val USE_HELPERS = "UseHelpers"
    const val UPGRADE_GEARS = "UpgradeGears"
    const val CHECK_SHOP = "CheckShop"
    const val CHECK_LEAGUE_SHOP = "CheckLeagueShop"
    const val CLAN_CAPITAL_TRAIN_TROOPS = "ClanCapitalTrainTroops"
    const val CLAN_CAPITAL_ATTACK = "ClanCapitalAttack"
    const val CLAN_WAR_LAST_STRATEGY = "ClanWarLastStrategy"
    const val CLAN_GAME_ACCEPTED_TASK_BASE = "ClanGameAcceptedTaskBase"
    const val CLAN_GAME_FINISHED_MONTH = "ClanGameFinishedMonth"
    const val CLAN_GAME_REWARD_CLAIMED_PERIOD = "ClanGameRewardClaimedPeriod"

    // Per-account keys for dynamic adjustment resource thresholds
    const val DYNAMIC_GOLD = "DynamicGold"
    const val DYNAMIC_ELIXIR = "DynamicElixir"
    const val DYNAMIC_DARK_ELIXIR = "DynamicDarkElixir"

    // Per-account key for base/reinforcement adjustment
    const val ADJUST_BASE_AND_REINFORCEMENTS = "AdjustBaseAndReinforcements"

    // Periodic screenshot upload timestamp
    const val SEARCH_SCREENSHOT_UPLOAD = "SearchScreenshotUpload"

    /**
     * Generate a storage key with account number suffix.
     * Used for per-account data that needs to be tracked separately.
     */
    fun withAccountNumber(key: String, accountNumber: Int): String {
        return "${key}${accountNumber}"
    }
}
