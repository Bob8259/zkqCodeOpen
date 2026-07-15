package com.coc.zkqcode.jar.code.mainbase.attack


import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.random.Random

// Per-troop deployment drag timeout in milliseconds
private const val DEPLOY_TIMEOUT_MS = 10_000L

// Drag start position (top of deploy zone)
private const val DRAG_START_X = 600F
private const val DRAG_START_Y = 50F

// Drag end position (bottom of deploy zone)
private const val DRAG_END_X = 85F
private const val DRAG_END_Y = 430F

// Duration (ms) for each individual smooth drag sweep
private const val DRAG_SWEEP_MS = 600
private const val DRAG_SWEEP_SLOW_MS = 3000
private const val MAX_CN_DEPLOY_ROUNDS = 20
private const val MAX_CN_SPELL_BATCHES = 20

suspend fun mainBaseDeployTroops() {
    // Record the start time of the battle
    zoomSmallMainBase(isForAttack = true)

    // Read user-configurable deployment switches so that heroes, spells, siege machines
    // and clan-castle reinforcements can be toggled independently.
    val deployHeroes = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_HEROES_ENABLED.key)
    val deploySpells = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_SPELLS_ENABLED.key)
    val deploySiegeMachines = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_SIEGE_MACHINES_ENABLED.key)
    val deployReinforcements = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_REINFORCEMENTS_ENABLED.key)

    // Upload a screenshot every 3 hours for monitoring
//    uploadSearchScreenshot()
    if (deploySiegeMachines) deploySiegeMachine(useReinforcements = deployReinforcements)
    repeat(3) {
        // Deploy each troop type if detected in the deployment bar
        deployIfPresent(DRAG_SWEEP_MS, MyColors.DragonAtDeploymentBar, MyColors.DragonAtDeploymentBar2)
        deployIfPresent(DRAG_SWEEP_MS, MyColors.GiantAtDeploymentBar, MyColors.GiantAtDeploymentBar2)
        deployIfPresent(DRAG_SWEEP_SLOW_MS, MyColors.BarbarianAtDeploymentBar, MyColors.BarbarianAtDeploymentBar2)
        deployIfPresent(DRAG_SWEEP_SLOW_MS, MyColors.ArcherAtDeploymentBar, MyColors.ArcherAtDeploymentBar2, MyColors.ArcherAtDeploymentBar3, MyColors.ArcherAtDeploymentBar4)
        if (deployHeroes) deployHeroes()
        deployOthers(useSpells = deploySpells)
    }
}

private suspend fun deploySiegeMachine(useReinforcements: Boolean = true) {
    val siegeMachine = findMultiColors(MyColors.SiegeMachineAtDeploymentBar)
    if (siegeMachine != null) {
        TouchActions.tap(siegeMachine.x + 10, siegeMachine.y + 20, delayTime = 100)
        if (useReinforcements) {
            // Tap the clan-castle reinforcements icon to release the donated troops
            TouchActions.tap(350, 250, delayTime = 100)
        }
        TouchActions.tap(DRAG_START_X.toInt(), DRAG_START_Y.toInt(), delayTime = 100)
    }
}

private suspend fun deployOthers(useSpells: Boolean = true) {
    //Deploy other troops and spells
    LogHelper.showDebugInfo("Start to deploy others")
    repeat(15) {
        val troops = findMultiColorsUntil(
            schemas = listOf(MyColors.TroopColorAtDeploymentBar, MyColors.TroopColorAtDeploymentBar2, MyColors.SuperTroopColorAtDeploymentBar, MyColors.SpecialTroopColorAtDeploymentBar), duration = 20
        )
        if (troops != null) {
            TouchActions.tap(troops.x, troops.y, delayTime = 300)
            repeat(3) {
                TouchActions.tap(310, 247, delayTime = 100)
                TouchActions.tap(386, 222, delayTime = 100)
            }
        }
        if (useSpells) {
            val spells = findMultiColorsUntil(schemas = listOf(MyColors.SpellColorAtDeploymentBar), duration = 20)
            if (spells != null) {
                TouchActions.tap(spells.x, spells.y, delayTime = 300)
                TouchActions.tap(520, 240, delayTime = 300)
                TouchActions.tap(465, 320, delayTime = 300)
                TouchActions.tap(370, 355, delayTime = 300)

            }
        }
    }
    LogHelper.showDebugInfo("Finish deploying others")
}

private suspend fun deployHeroes() {
    // All 6 hero colour schemas to check in the deployment bar
    val heroes = listOf(
        MyColors.KingBarbarian,
        MyColors.QueenArcher,
        MyColors.QueenArcher2,
        MyColors.QueenArcher3,
        MyColors.QueenArcher4,
        MyColors.MinionPrince,
        MyColors.MinionPrince2,
        MyColors.GrandWarden,
        MyColors.GrandWarden2,
        MyColors.GrandWarden3,
        MyColors.GrandWarden4,
        MyColors.GrandWarden5,
        MyColors.RoyalChampion,
        MyColors.RoyalChampion2,
        MyColors.DragonDuke,
        MyColors.DragonDuke2
    )
    for (hero in heroes) {
        // Take a fresh screenshot for each hero to get the latest state of the bar
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: continue
        val found = findMultiColors(schema = hero, byteBuffer = screenBuffer)
        if (found != null) {
            // Tap the hero icon in the deployment bar to select it
            TouchActions.tap(found.x, found.y, delayTime = 300)
            // Tap the deploy zone to place the hero on the battlefield
            TouchActions.tap(350, 250, delayTime = 100)
            TouchActions.tap(DRAG_START_X.toInt(), DRAG_START_Y.toInt(), delayTime = 100)
            TouchActions.tap(found.x, found.y, delayTime = 100)
        }
    }
}

/**
 * Detects whether [schemas] are visible in the deployment bar and, if so,
 * deploys all units of that type via continuous back-and-forth dragging.
 */
private suspend fun deployIfPresent(dragSweepMs: Int, vararg schemas: ColorSchema) {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    for (schema in schemas) {
        val troop = findMultiColors(schema = schema, byteBuffer = screenBuffer)
        if (troop != null) {
            dragUntilDeployed(troop.x, troop.y, dragSweepMs, schemas)
            return
        }
    }
}

/**
 * Selects the troop at ([x], [y]) in the deployment bar, then holds one finger down
 * and alternates between [DRAG_START_X],[DRAG_START_Y] and [DRAG_END_X],[DRAG_END_Y]
 * until [schemas] are no longer detected (all units deployed) or [DEPLOY_TIMEOUT_MS]
 * has elapsed for this troop.
 */
private suspend fun dragUntilDeployed(x: Int, y: Int, dragSweepMs: Int, schemas: Array<out ColorSchema>) {
    // Select the troop in the deployment bar
    TouchActions.tap(x, y)
    delayWithMultiplier(300)

    val startTime = System.currentTimeMillis()
    // Hold the finger down; it will stay down for the entire drag loop
    TouchActions.touchDown(DRAG_START_X, DRAG_START_Y, 1)
    try {
        delayWithMultiplier(600)
        while (true) {
            var currentDragSweepMs = dragSweepMs + Random.nextInt(-500, 500)
            // Drag forward: deploy position
            TouchActions.moveSmoothly(DRAG_START_X, DRAG_START_Y, DRAG_END_X, DRAG_END_Y, currentDragSweepMs, 1)
            delayWithMultiplier(300)

            // Check whether the troop is still present in the deployment bar
            val elapsed = System.currentTimeMillis() - startTime
            val stillPresent = schemas.any { findMultiColors(schema = it) != null }
            if (!stillPresent || elapsed >= DEPLOY_TIMEOUT_MS) break
            currentDragSweepMs = dragSweepMs + Random.nextInt(-500, 500)
            // Drag back: ready for another forward sweep
            TouchActions.moveSmoothly(DRAG_END_X, DRAG_END_Y, DRAG_START_X, DRAG_START_Y, currentDragSweepMs, 1)

            // Check timeout again after the return sweep before the next forward drag
            if (System.currentTimeMillis() - startTime >= DEPLOY_TIMEOUT_MS) break

        }
    } finally {
        // Always release the finger, even if cancelled
        withContext(NonCancellable) {
            TouchActions.touchUp(1)
        }
    }
}

// ========== CN server (国服) specific deployment ==========

private data class DeployPath(
    val startX: Float, val startY: Float,
    val endX: Float, val endY: Float,
    val name: String = ""
)

private data class DetectedTroop(
    val x: Int, val y: Int,
    val schemas: List<ColorSchema>,
    val dragSweepMs: Int
)

// CN 4 deployment paths covering all 4 edges of the battlefield
private val CN_P1_A = DeployPath(561f, 76f, 138f, 390f, "P1A")
private val CN_P1_B = DeployPath(679f, 103f, 1080f, 404f, "P1B")
private val CN_P2_C = DeployPath(96f, 226f, 530f, 544f, "P2C")
private val CN_P2_D = DeployPath(1097f, 213f, 663f, 542f, "P2D")

// CN troop bar schemas. Spells are detected and deployed separately.
private val cnTroopSchemas = listOf(
    MyColors.TroopColorAtDeploymentBar,
    MyColors.SuperTroopColorAtDeploymentBar,
    MyColors.SpecialTroopColorAtDeploymentBar
)

private suspend fun cnScanBar(): List<DetectedTroop> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: return emptyList()

    val results = mutableListOf<DetectedTroop>()

    for (schema in cnTroopSchemas) {
        var searchX1 = schema.x1
        while (searchX1 < schema.x2) {
            val narrowed = ColorSchema.rescope(schema, searchX1, schema.y1, schema.x2, schema.y2)
            val found = findMultiColors(schema = narrowed, byteBuffer = screenBuffer)
            if (found == null) break
            results.add(DetectedTroop(found.x, found.y, listOf(schema), DRAG_SWEEP_MS))
            searchX1 = found.x + 40
        }
    }

    // Dedup: sort by x, then remove entries within 40px of the previous kept one.
    // 40px ≈ half a deployment slot width — catches same-icon multi-schema matches
    // while keeping genuinely adjacent different troop types separate.
    val sorted = results.sortedBy { it.x }
    val deduped = mutableListOf<DetectedTroop>()
    for (troop in sorted) {
        if (deduped.isEmpty() || kotlin.math.abs(troop.x - deduped.last().x) >= 40) {
            deduped.add(troop)
        }
    }
    return deduped
}

private suspend fun cnHasAnyTroop(): Boolean {
    return cnTroopSchemas.any { findMultiColors(schema = it) != null }
}

private suspend fun cnDeploySpells() {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_SPELLS_ENABLED.key)) return

    repeat(MAX_CN_SPELL_BATCHES) {
        // Refresh the bar after each batch so depleted spells disappear from the next snapshot.
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return
        val spells = mutableListOf<Pair<Int, Int>>()
        val schema = MyColors.SpellColorAtDeploymentBar
        var searchX1 = schema.x1
        while (searchX1 < schema.x2) {
            val narrowed = ColorSchema.rescope(schema, searchX1, schema.y1, schema.x2, schema.y2)
            val found = findMultiColors(schema = narrowed, byteBuffer = screenBuffer) ?: break
            spells.add(found.x to found.y)
            searchX1 = found.x + 40
        }

        if (spells.isEmpty()) return

        for ((x, y) in spells) {
            TouchActions.tap(x, y, delayTime = 300)
            TouchActions.tap(520, 240, delayTime = 300)
            TouchActions.tap(465, 320, delayTime = 300)
            TouchActions.tap(370, 355, delayTime = 300)
        }
    }
}

private suspend fun cnDeployOneBatch(finger1: DeployPath, finger2: DeployPath, customSweepMs: Int = DRAG_SWEEP_MS) {
    // One screenshot creates the troop snapshot for this area. Each detected slot
    // receives one pass on both paths; do not wait for the slot to disappear.
    val troops = cnScanBar()
    for (troop in troops) {
        TouchActions.tap(troop.x, troop.y, delayTime = 300)
        val sweepMs = (if (customSweepMs > 0) customSweepMs else troop.dragSweepMs) + Random.nextInt(-500, 500)
        TouchActions.touchDown(finger1.startX, finger1.startY, 1)
        delayWithMultiplier(600)
        TouchActions.moveSmoothly(finger1.startX, finger1.startY, finger1.endX, finger1.endY, sweepMs, 1)
        TouchActions.touchUp(1)
        delayWithMultiplier(200)
        TouchActions.touchDown(finger2.startX, finger2.startY, 1)
        delayWithMultiplier(600)
        TouchActions.moveSmoothly(finger2.startX, finger2.startY, finger2.endX, finger2.endY, sweepMs, 1)
        TouchActions.touchUp(1)
    }

    // Step 2: deploy heroes (reuse international version)
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_HEROES_ENABLED.key)) {
        deployHeroes()
    }

    // Step 3: deploy spells
    cnDeploySpells()
}

private suspend fun cnDeployClickOnPath(path: DeployPath, density: Int, clickDelayMs: Int) {
    val dx = path.endX - path.startX
    val dy = path.endY - path.startY
    val distance = kotlin.math.sqrt((dx * dx + dy * dy).toDouble())
    val steps = maxOf(1, (distance / density).toInt())
    for (i in 0..steps) {
        val t = if (steps == 0) 0f else i.toFloat() / steps
        val x = (path.startX + dx * t).toInt()
        val y = (path.startY + dy * t).toInt()
        TouchActions.tap(x, y, delayTime = clickDelayMs)
    }
}

private suspend fun cnDeployOneBatchClick(finger1: DeployPath, finger2: DeployPath, density: Int, clickDelayMs: Int) {
    val troops = cnScanBar()
    for (troop in troops) {
        TouchActions.tap(troop.x, troop.y, delayTime = 300)
        cnDeployClickOnPath(finger1, density, clickDelayMs)
        delayWithMultiplier(200)
        cnDeployClickOnPath(finger2, density, clickDelayMs)
    }

    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_HEROES_ENABLED.key)) {
        deployHeroes()
    }
    cnDeploySpells()
}

suspend fun cnMainBaseDeployTroops() {
    zoomSmallMainBase(isForAttack = true)

    val deployMode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_MODE.key)
    val customSweepMs = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_DRAG_SPEED.key).toIntOrNull() ?: DRAG_SWEEP_MS
    val clickDensity = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_CLICK_DENSITY.key).toIntOrNull() ?: 50

    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_SIEGE_MACHINES_ENABLED.key)) {
        deploySiegeMachine(
            useReinforcements = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_REINFORCEMENTS_ENABLED.key)
        )
    }

    val isClickMode = deployMode == "2"

    var isUpperHalf = true
    for (round in 1..MAX_CN_DEPLOY_ROUNDS) {
        if (isUpperHalf) {
            if (isClickMode) {
                cnDeployOneBatchClick(CN_P1_A, CN_P1_B, clickDensity, customSweepMs)
            } else {
                cnDeployOneBatch(CN_P1_A, CN_P1_B, customSweepMs)
            }
        } else {
            if (isClickMode) {
                cnDeployOneBatchClick(CN_P2_C, CN_P2_D, clickDensity, customSweepMs)
            } else {
                cnDeployOneBatch(CN_P2_C, CN_P2_D, customSweepMs)
            }
        }

        if (!cnHasAnyTroop() || round == MAX_CN_DEPLOY_ROUNDS) break

        if (isUpperHalf) {
            TouchActions.swipe(1209, 463, 1209, 156, delayTime = 100)
        } else {
            TouchActions.swipe(1209, 156, 1209, 463, delayTime = 100)
        }
        isUpperHalf = !isUpperHalf
    }
}
