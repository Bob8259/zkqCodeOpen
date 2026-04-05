package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory

suspend fun mainBaseCheckTutorials(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.MAIN_BASE_CHECK_TUTORIALS, InGamesVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，今天已检查常见教程")
        return true
    }

    ShowMessage("准备检测常见教程")
    zoomSmallMainBase()

    // Check the current view first, then scan the lower area after swiping.
    handleTutorialArrowIfNeeded()
    swipe(911, 134, 0, 720)
    handleTutorialArrowIfNeeded()
    // Record the completed daily check so we only scan once per day.
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}

private suspend fun handleTutorialArrowIfNeeded() {
    val tutorialArrow = findMultiColorsUntil(
        schemas = listOf(MyColors.MainBaseSmallTutorial),
        duration = 1500
    ) ?: return

    TouchActions.tap(tutorialArrow.x + 20, tutorialArrow.y + 60, delayTime = 800)

    if (findMultiColors(MyColors.OuterPetIcon) != null) {
        petsTutorial()
        return
    }

    if (findMultiColors(MyColors.ClanCastleAddReinforcement) != null) {
        clanCastleTutorialHelper()
    }
    delayWithMultiplier(1000)
    clickRightBottom(10)
}

private suspend fun clanCastleTutorialHelper() {
    val clanCastle = findMultiColors(MyColors.ClanCastleAddReinforcement)
    if (clanCastle != null) {
        TouchActions.tap(clanCastle.x, clanCastle.y, delayTime = 500)
    }
    repeat(10) {
        TouchActions.tap(900, 130, delayTime = 50)//Edit troop. To skip tutorial.
    }
}

private suspend fun petsTutorial() {
    val startTime = System.currentTimeMillis()
    val timeoutMillis = 60_000L

    while (true) {
        // Exit if the pet tutorial does not complete within one minute.
        if (System.currentTimeMillis() - startTime >= timeoutMillis) {
            return
        }
        val remainingSeconds = (timeoutMillis - (System.currentTimeMillis() - startTime)) / 1000
        ShowMessage("宠物店教程中，若${remainingSeconds}秒后未完成则强制退出")
        val petsTutorial = findMultiColors(MyColors.OuterPetIcon)
        if (petsTutorial != null) {
            TouchActions.tap(petsTutorial.x, petsTutorial.y, delayTime = 500)
        }
        val speakingVillager = findMultiColorsUntil(schemas = listOf(MyColors.SpeakingVillager, MyColors.SpeakingVillager2), duration = 200)
        if (speakingVillager != null) {
            TouchActions.tap(speakingVillager.x, speakingVillager.y, delayTime = 500)
        }
        val innerBanner = findMultiColors(MyColors.PetsShopInnerBanner)
        if (innerBanner != null) {
            completePetsTutorialSelection()
            return
        }
        delayWithMultiplier(500)
    }
}

private suspend fun completePetsTutorialSelection() {
    val tapSequence = listOf(
        Triple(542, 50, 300), // Tap the banner first to avoid tutorial UI bugs.
        Triple(229, 485, 500), // Choose a hero.
        Triple(67, 376, 500) // King Barbarian.
    )

    tapSequence.forEach { (x, y, delayTime) ->
        TouchActions.tap(x, y, delayTime = delayTime)
    }
    clickRightBottom(5)
}