package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.ui.schema.details.MainBasePets
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeGearsAndPets(): Boolean {
    val shouldUpgradeGears = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key)
    val shouldUpgradePets = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)

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

private suspend fun checkPets() {
    val petColorMap = listOf(
        MainBasePets.LASSI to MyColors.Lassi,
        MainBasePets.LASSI to MyColors.Lassi2,
        MainBasePets.ELECTRO_OWL to MyColors.ElectroOwl,
        MainBasePets.MIGHTY_YAK to MyColors.MightyYak,
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

    val enabledPets = petColorMap.filter { getBooleanConfigRuntime(it.first.key) }

    if (enabledPets.isEmpty()) {
        accountMessage("没有启用的战宠")
        return
    }

    accountMessage("已启用 ${enabledPets.size} 个战宠")

    repeat(4) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("checkPets: Screen Capture Failed.")

        for ((pet, colorSchema) in enabledPets) {
            val result = findMultiColors(schema = colorSchema, byteBuffer = screenBuffer, increment = 1)
            if (result != null) {
                val insufficientLeft = result.x
                val insufficientTop = result.y
                val insufficientRight = minOf(result.x + 100, screenBuffer.width - 1)
                val insufficientBottom = minOf(result.y + 40, screenBuffer.height - 1)

                val insufficientSchema = ColorSchema.rescope(
                    MainBaseResearchColors.MainBaseResearchInsufficientColors, insufficientLeft, insufficientTop, insufficientRight, insufficientBottom
                )

                if (findMultiColors(byteBuffer = screenBuffer, schema = insufficientSchema, increment = 1) != null) {
                    accountMessage("跳过 ${pet.displayName}: 资源不足")
                    continue
                }

                TouchActions.tap(result.x, result.y)
                val confirmUpgrade = findMultiColorsUntil(schemas = listOf(MyColors.ConfirmUpgradePet), duration = 400)
                if (confirmUpgrade == null) {
                    accountMessage("跳过 ${pet.displayName}: 无法升级")
                    continue
                }

                accountMessage("升级 ${pet.displayName}")
                TouchActions.tap(confirmUpgrade.x, confirmUpgrade.y)
                clickRightBottom(1)
                return
            }
        }

        TouchActions.swipe(800, 600, 180, 600, delayAfter = 100)
    }
}

private suspend fun upgradeGearsAndPetsAction(shouldUpgradeGears: Boolean, shouldUpgradePets: Boolean) {
    if (shouldUpgradeGears) {
        TouchActions.tap(685, 590)//Open Smith
        val upgradeGear = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGearArrow, MyColors.NewGear), duration = 800)
        if (upgradeGear != null) {
            TouchActions.tap(upgradeGear.x, upgradeGear.y)
            repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
            TouchActions.tap(1220, 635, delayTime = 300)
        }
        run upgradeGears@{
            if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key)) {
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
        }
        backToHeroHall()
    }
    if (shouldUpgradePets) {
        val petsIcon = findMultiColorsUntil(schemas = listOf(MyColors.PetsIconInHeroHall, MyColors.PetsIconInHeroHall2), duration = 200)
        if (petsIcon != null) {
            TouchActions.tap(petsIcon.x, petsIcon.y)
            val outerPetIcon = findMultiColorsUntil(schemas = listOf(MyColors.PetShopIcon, MyColors.PetShopIcon2), duration = 300)
            if (outerPetIcon != null) {
                TouchActions.tap(outerPetIcon.x, outerPetIcon.y)
            }
            val petsShopBanner = findMultiColorsUntil(schemas = listOf(MyColors.PetsShopInnerBanner), duration = 1000)
            if (petsShopBanner != null) {
                accountMessage("已进入宠物店")
                delayWithMultiplier(200)
                val alreadyBeingUpgraded = findMultiColors(MyColors.AlreadyBeingUpgraded)
                if (alreadyBeingUpgraded == null) checkPets()
            }
        }
    }
}

private suspend fun backToHeroHall() {
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
