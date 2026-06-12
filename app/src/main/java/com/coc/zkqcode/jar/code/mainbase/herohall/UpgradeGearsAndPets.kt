package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.mainbase.others.upgradeGearsAction
import com.coc.zkqcode.jar.ui.schema.details.MainBasePets
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeGearsAndPets(): Boolean {
    val gearMasterOn = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_GEAR_MASTER.key)
    var shouldUpgradeGears = if (gearMasterOn) {
        getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key) || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key)
    } else {
        false
    }
    var shouldUpgradePets = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)

    // Gears require TH8+, pets require TH14+
    if (InGameVars.townHallLevel < 8) shouldUpgradeGears = false
    if (InGameVars.townHallLevel < 14) {
        shouldUpgradePets = false
        if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)) {
            accountMessage("战宠需要14本才能升级，当前${InGameVars.townHallLevel}本，跳过")
        }
    }

    if (!shouldUpgradeGears && !shouldUpgradePets) return true

    val gearStorageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS, InGameVars.currentAccountNumber
    )

    // Guard only gear checks so pets can still be upgraded during the cooldown window.
    val canUpgradeGears = if (shouldUpgradeGears) {
        if (!checkMemoryFile(gearStorageKey, 480)) {
            accountMessage("该账号8小时内已检测装备升级")
            false
        } else {
            true
        }
    } else {
        false
    }

    val actionName = when {
        canUpgradeGears && shouldUpgradePets -> "装备和战宠"
        canUpgradeGears -> "装备"
        shouldUpgradePets -> "战宠"
        else -> return true
    }
    accountMessage("准备升级$actionName")
    val result: Boolean = withHeroHall { upgradeGearsAndPetsAction(canUpgradeGears, shouldUpgradePets) }

    // Record only gear checks so the cooldown never blocks pet upgrades.
    if (canUpgradeGears) {
        writeMemory(gearStorageKey, (System.currentTimeMillis() / 60_000).toString())
    }
    return result
}

private data class PetUpgradableItem(
    val pet: com.coc.zkqcode.jar.ui.schema.SettingDef,
    val displayName: String,
    val priority: Int,
    val x: Int,
    val y: Int,
    val screenIndex: Int
)

private suspend fun checkPets() {
    // Build the color map once (lazy val at file level not used to keep changes minimal)
    val petColorMap = listOf(
        MainBasePets.LASSI to MyColors.Lassi,
        MainBasePets.LASSI to MyColors.Lassi2,
        MainBasePets.LASSI to MyColors.Lassi3,
        MainBasePets.ELECTRO_OWL to MyColors.ElectroOwl,
        MainBasePets.ELECTRO_OWL to MyColors.ElectroOwl2,
        MainBasePets.MIGHTY_YAK to MyColors.MightyYak,
        MainBasePets.MIGHTY_YAK to MyColors.MightyYak2,
        MainBasePets.UNICORN to MyColors.Unicorn,
        MainBasePets.FROSTY to MyColors.Frosty,
        MainBasePets.FROSTY to MyColors.Frosty2,
        MainBasePets.DIGGY to MyColors.Diggy,
        MainBasePets.DIGGY to MyColors.Diggy2,
        MainBasePets.POISON_LIZARD to MyColors.PoisonLizard,
        MainBasePets.POISON_LIZARD to MyColors.PoisonLizard2,
        MainBasePets.PHOENIX to MyColors.Phoenix,
        MainBasePets.PHOENIX to MyColors.Phoenix2,
        MainBasePets.SPIRIT_FOX to MyColors.SpiritFox,
        MainBasePets.ANGRY_JELLY to MyColors.AngryJelly,
        MainBasePets.ANGRY_JELLY to MyColors.AngryJelly2,
        MainBasePets.SNEEZY to MyColors.Sneezy,
        MainBasePets.SNEEZY to MyColors.Sneezy2,
        MainBasePets.GREEDY_RAVEN to MyColors.GreedyRaven
    )

    // Collect enabled pet definitions (deduplicated)
    val enabledPetDefs = MainBasePets.all.filter { getBooleanConfigRuntime(it.key) }

    if (enabledPetDefs.isEmpty()) {
        accountMessage("没有启用的战宠")
        return
    }

    accountMessage("已启用 ${enabledPetDefs.size} 个战宠")

    // ========== Phase 1: Scout all 4 pages, collect upgradeable targets ==========
    val upgradeableItems = mutableListOf<PetUpgradableItem>()
    val completedPets = mutableSetOf<String>() // Track already-processed pets to avoid duplicates

    for (screenIdx in 0 until 4) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndRestart("checkPets Phase 1: Screen Capture Failed.")

        for (petDef in enabledPetDefs) {
            if (petDef.key in completedPets) continue

            // Try all color schemas for this pet
            val petSchemas = petColorMap.filter { it.first.key == petDef.key }.map { it.second }
            var found = false
            for (colorSchema in petSchemas) {
                val result = findMultiColors(schema = colorSchema, byteBuffer = screenBuffer, increment = 1)
                if (result != null) {
                    // Check resource sufficiency
                    val insufficientLeft = result.x
                    val insufficientTop = result.y
                    val insufficientRight = minOf(result.x + 100, screenBuffer.width - 1)
                    val insufficientBottom = minOf(result.y + 40, screenBuffer.height - 1)

                    val insufficientSchema = ColorSchema.rescope(
                        MainBaseResearchColors.MainBaseResearchInsufficientColors, insufficientLeft, insufficientTop, insufficientRight, insufficientBottom
                    )

                    if (findMultiColors(byteBuffer = screenBuffer, schema = insufficientSchema, increment = 1) != null) {
                        accountMessage("跳过 ${petDef.displayName}: 资源不足")
                        completedPets.add(petDef.key)
                        found = true
                        break
                    }

                    // Read priority from config
                    val priorityKey = "${petDef.key}_pet_priority"
                    val priority = getConfigRuntime(priorityKey).toIntOrNull() ?: Int.MAX_VALUE

                    completedPets.add(petDef.key)
                    upgradeableItems.add(PetUpgradableItem(
                        pet = petDef,
                        displayName = petDef.displayName,
                        priority = priority,
                        x = result.x,
                        y = result.y,
                        screenIndex = screenIdx
                    ))
                    accountMessage("发现可升级: ${petDef.displayName}, 第${screenIdx}页")
                    found = true
                    break
                }
            }
        }

        // Swipe to next page (except after the last page)
        if (screenIdx < 3) {
            TouchActions.swipe(800, 600, 180, 600, delayAfter = 100)
        }
    }

    // ========== Phase 2: Select highest priority item and upgrade ==========
    if (upgradeableItems.isEmpty()) {
        accountMessage("未找到任何可升级的战宠")
        return
    }

    // Sort by priority (ascending: lower number = higher priority)
    upgradeableItems.sortBy { it.priority }

    // Print priority summary
    val summaryLines = upgradeableItems.mapIndexed { index, item ->
        "${index + 1}. ${item.displayName}"
    }
    accountMessage("战宠升级\n优先级列表 :\n${summaryLines.joinToString("\n")}")
    delayWithMultiplier(2000)

    val target = upgradeableItems.first()

    // ========== Phase 3: Navigate back page by page, re-scan and upgrade ==========
    // After Phase 1 we are at screen 3 (the last page).
    // Swipe backwards one page at a time, re-capturing and re-verifying at each page.
    val targetSchemas = petColorMap.filter { it.first.key == target.pet.key }.map { it.second }
    var foundAndUpgraded = false
    for (currentPage in 3 downTo 0) {
        val buffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndRestart("checkPets Phase 3: Screen capture failed at page $currentPage")

        for (targetSchema in targetSchemas) {
            val result = findMultiColors(schema = targetSchema, byteBuffer = buffer, increment = 1) ?: continue

            // Re-verify resource sufficiency
            val insLeft = result.x
            val insTop = result.y
            val insRight = minOf(result.x + 100, buffer.width - 1)
            val insBottom = minOf(result.y + 40, buffer.height - 1)
            val insSchema = ColorSchema.rescope(
                MainBaseResearchColors.MainBaseResearchInsufficientColors, insLeft, insTop, insRight, insBottom
            )
            if (findMultiColors(byteBuffer = buffer, schema = insSchema, increment = 1) != null) {
                accountMessage("跳过 ${target.displayName}: 资源不足（阶段三重检）")
                continue
            }

            // Perform the upgrade
            TouchActions.tap(result.x, result.y)
            val confirmUpgrade = findMultiColorsUntil(schemas = listOf(MyColors.ConfirmUpgradePet), duration = 400)
            if (confirmUpgrade != null) {
                accountMessage("升级 ${target.displayName}")
                TouchActions.tap(confirmUpgrade.x, confirmUpgrade.y)
                clickRightBottom(1)
                foundAndUpgraded = true
                break
            } else {
                accountMessage("跳过 ${target.displayName}: 无法升级（阶段三重检）")
            }
        }

        if (foundAndUpgraded) break

        // Target not found on this page — swipe back one more page
        if (currentPage > 0) {
            TouchActions.swipe(180, 600, 800, 600, delayAfter = 100)
        }
    }

    if (!foundAndUpgraded) {
        accountMessage("阶段三未能定位到 ${target.displayName}，跳过")
    }
}

private suspend fun upgradeGearsAndPetsAction(shouldUpgradeGears: Boolean, shouldUpgradePets: Boolean) {
    if (shouldUpgradeGears) {
        val shouldUpgradeAllGear = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key)
        val shouldUpgradeWear = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key)
        val shouldUpgradeCustom = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key)

        if (shouldUpgradeAllGear) {
            // Old workflow: open smith, scan rows for arrows, upgrade, swipe, repeat
            TouchActions.tap(685, 590) // Open Smith
            val upgradeGear = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGearArrow, MyColors.NewGear), duration = 800)
            if (upgradeGear != null) {
                TouchActions.tap(upgradeGear.x, upgradeGear.y)
                repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
                TouchActions.tap(1220, 635, delayTime = 300)
            }
            run upgradeGears@{
                suspend fun tryUpgradeInVisibleRows(): Boolean {
                    val firstRowArrow = ColorSchema.rescope(MyColors.UpgradeGearArrow, 80, 425, 1210, 460)
                    val firstRowNewGear = ColorSchema.rescope(MyColors.NewGear, 80, 425, 1210, 460)
                    val secondRowArrow = ColorSchema.rescope(MyColors.UpgradeGearArrow, 90, 510, 1220, 540)
                    val secondRowNewGear = ColorSchema.rescope(MyColors.NewGear, 90, 510, 1220, 540)
                    val gear = findMultiColorsUntil(
                        schemas = listOf(firstRowArrow, firstRowNewGear, secondRowArrow, secondRowNewGear), duration = 800
                    )
                    if (gear != null) {
                        TouchActions.tap(gear.x, gear.y)
                        repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
                        TouchActions.tap(1220, 635, delayTime = 300)
                        return true
                    }
                    return false
                }

                // Scan both visible rows in one pass using four rescoped schemas.
                if (tryUpgradeInVisibleRows()) return@upgradeGears

                // Swipe once, then run the same one-pass scan again.
                TouchActions.swipe(1220, 510, 0, 510, delayTime = 300)
                if (tryUpgradeInVisibleRows()) return@upgradeGears
            }
            backToHeroHall()
        }

        // New workflow: per-hero iteration with equipped and/or custom gear modes
        if (shouldUpgradeWear || shouldUpgradeCustom) {
            upgradeGearsAction(shouldUpgradeWear, shouldUpgradeCustom)
        }
    }
    if (shouldUpgradePets) {
        accountMessage("正在进入宠物店...")
        val petsIcon = findMultiColorsUntil(schemas = listOf(MyColors.PetsIconInHeroHall, MyColors.PetsIconInHeroHall2), duration = 200)
        if (petsIcon != null) {
            TouchActions.tap(petsIcon.x, petsIcon.y)
            val outerPetIcon = findMultiColorsUntil(schemas = listOf(MyColors.PetShopIcon, MyColors.PetShopIcon2, MyColors.PetShopIcon3), duration = 300)
            if (outerPetIcon != null) {
                TouchActions.tap(outerPetIcon.x, outerPetIcon.y)
            }
            val petsShopBanner = findMultiColorsUntil(schemas = listOf(MyColors.PetsShopInnerBanner), duration = 1000)
            if (petsShopBanner != null) {
                accountMessage("已进入宠物店")
                delayWithMultiplier(200)
                val alreadyBeingUpgraded = findMultiColors(MyColors.AlreadyBeingUpgraded)
                if (alreadyBeingUpgraded == null) {
                    checkPets()
                } else {
                    accountMessage("已有战宠正在升级中，跳过")
                }
            } else {
                accountMessage("未检测到宠物店横幅，跳过战宠升级")
            }
        } else {
            accountMessage("未检测到宠物图标，跳过战宠升级")
        }
    }
}

internal suspend fun backToHeroHall() {
    // Stop retrying after 10 seconds to avoid getting stuck in an endless back loop.
    val deadline = System.currentTimeMillis() + 10_000
    while (System.currentTimeMillis() < deadline) {
        val smithIcon = findMultiColors(MyColors.SmithOreIcon)
        if (smithIcon != null) {
            TouchActions.tap(1222, 80)
        }
        val petsIcon = findMultiColors(MyColors.PetsIconInHeroHall)
        if (petsIcon != null) {
            return
        }
        TouchActions.tap(1216, 622)
        delayWithMultiplier(500)
    }
}
