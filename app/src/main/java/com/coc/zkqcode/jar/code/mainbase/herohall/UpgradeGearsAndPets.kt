package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.ui.schema.details.MainBasePets
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeGearsAndPets(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) && !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key) && !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS_AND_PETS, InGamesVars.currentAccountNumber
    )

    // Guard: only attempt gear upgrade once per 8 hours (480 minutes)
    if (!checkMemoryFile(storageKey, 480)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号8小时内已检测装备和战宠升级")
        return true
    }

    ShowMessage("准备升级装备和战宠")
    val result: Boolean = withHeroHall { upgradeGearsAction() }

    // Always record completion and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return result
}

private suspend fun checkPets() {
    val petColorMap = listOf(
        MainBasePets.LASSI to MyColors.Lassi,
        MainBasePets.ELECTRO_OWL to MyColors.ElectroOwl,
        MainBasePets.MIGHTY_YAK to MyColors.MightyYak,
        MainBasePets.UNICORN to MyColors.Unicorn,
        MainBasePets.FROSTY to MyColors.Frosty,
        MainBasePets.DIGGY to MyColors.Diggy,
        MainBasePets.POISON_LIZARD to MyColors.PoisonLizard,
        MainBasePets.PHOENIX to MyColors.Phoenix,
        MainBasePets.SPIRIT_FOX to MyColors.SpiritFox,
        MainBasePets.ANGRY_JELLY to MyColors.AngryJelly,
        MainBasePets.SNEEZY to MyColors.Sneezy,
        MainBasePets.GREEDY_RAVEN to MyColors.GreedyRaven
    )

    val enabledPets = petColorMap.filter { getBooleanConfigRuntime(it.first.key) }

    if (enabledPets.isEmpty()) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，没有启用的战宠")
        return
    }

    ShowMessage("账号${InGamesVars.currentAccountNumber}，已启用 ${enabledPets.size} 个战宠")

    repeat(3) {
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
                    ShowMessage("账号${InGamesVars.currentAccountNumber}，跳过 ${pet.displayName}: 资源不足")
                    continue
                }

                TouchActions.tap(result.x, result.y, delayTime = 500)
                val confirmUpgrade = findMultiColorsUntil(schemas = listOf(MyColors.ConfirmUpgradePet), duration = 1000)
                if (confirmUpgrade == null) {
                    ShowMessage("账号${InGamesVars.currentAccountNumber}，跳过 ${pet.displayName}: 无法升级")
                    continue
                }

                ShowMessage("账号${InGamesVars.currentAccountNumber}，升级 ${pet.displayName}")
                TouchActions.tap(confirmUpgrade.x, confirmUpgrade.y)
                clickRightBottom(1)
                return
            }
        }

        TouchActions.swipe(800, 600, 180, 600)
        delayWithMultiplier(100)
    }
}

private suspend fun upgradeGearsAction() {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key) || getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key)) {
        TouchActions.tap(685, 590, delayTime = 600)//Open Smith
        val upgradeGear = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGearArrow, MyColors.NewGear), duration = 800)
        if (upgradeGear != null) {
            TouchActions.tap(upgradeGear.x, upgradeGear.y, delayTime = 500)
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
                        TouchActions.tap(gear.x, gear.y, delayTime = 500)
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
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)) {
        val petsIcon = findMultiColors(MyColors.PetsIconInHeroHall)
        if (petsIcon != null) {
            TouchActions.tap(petsIcon.x, petsIcon.y, delayTime = 500)
            val petsShopBanner = findMultiColorsUntil(schemas = listOf(MyColors.PetsShopInnerBanner), duration = 1000)
            if (petsShopBanner != null) {
                ShowMessage("已进入宠物店")
                delayWithMultiplier(200)
                checkPets()
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