package com.coc.zkqcode.jar.code.universal.smalltools

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
    const val MAIN_BASE_TRAIN_TROOPS = "MainBaseTrainTroops"
    const val BUILDER_BASE_TRAIN_TROOPS = "BuilderBaseTrainTroops"
    const val CLICK_OTTOS_POST = "ClickOttosPost"

    /**
     * Generate a storage key with account number suffix.
     * Used for per-account data that needs to be tracked separately.
     */
    fun withAccountNumber(key: String, accountNumber: Int): String {
        return "${key}${accountNumber}"
    }
}
