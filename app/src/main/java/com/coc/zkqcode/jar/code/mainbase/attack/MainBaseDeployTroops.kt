package com.coc.zkqcode.jar.code.mainbase.attack


import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

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
private const val DRAG_SWEEP_SLOW_MS = 2000

suspend fun mainBaseDeployTroops() {
    zoomSmallMainBase(isForAttack = true)

    // Deploy each troop type if detected in the deploy bar
    deployIfPresent(DRAG_SWEEP_MS, MyColors.DragonAtDeployBar, MyColors.DragonAtDeployBar2)
    deployIfPresent(DRAG_SWEEP_MS, MyColors.GiantAtDeployBar, MyColors.GiantAtDeployBar2)
    deployIfPresent(DRAG_SWEEP_SLOW_MS, MyColors.BarbarianAtDeployBar, MyColors.BarbarianAtDeployBar2)
    deployIfPresent(DRAG_SWEEP_SLOW_MS, MyColors.ArcherAtDeployBar, MyColors.ArcherAtDeployBar2)
}

/**
 * Detects whether [schemas] are visible in the deploy bar and, if so,
 * deploys all units of that type via continuous back-and-forth dragging.
 */
private suspend fun deployIfPresent(dragSweepMs: Int, vararg schemas: ColorSchema) {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndStop("failed to take screenshot at close advertisement")
    for (schema in schemas) {
        val troop = findMultiColors(schema = schema, byteBuffer = screenBuffer)
        if (troop != null) {
            dragUntilDeployed(troop.x, troop.y, dragSweepMs, schemas)
            return
        }
    }
}

/**
 * Selects the troop at ([x], [y]) in the deploy bar, then holds one finger down
 * and alternates between [DRAG_START_X],[DRAG_START_Y] and [DRAG_END_X],[DRAG_END_Y]
 * until [schemas] are no longer detected (all units deployed) or [DEPLOY_TIMEOUT_MS]
 * has elapsed for this troop.
 */
private suspend fun dragUntilDeployed(x: Int, y: Int, dragSweepMs: Int, schemas: Array<out ColorSchema>) {
    // Select the troop in the deploy bar
    TouchActions.tap(x, y)
    delayWithMultiplier(300)

    val startTime = System.currentTimeMillis()
    // Hold the finger down; it will stay down for the entire drag loop
    TouchActions.touchDown(DRAG_START_X, DRAG_START_Y, 1)
    try {
        delayWithMultiplier(600)
        var currentDragSweepMs = dragSweepMs
        while (true) {
            // Drag forward: deploy position
            TouchActions.moveSmoothly(DRAG_START_X, DRAG_START_Y, DRAG_END_X, DRAG_END_Y, currentDragSweepMs, 1)
            delayWithMultiplier(300)

            // Check whether the troop is still present in the deploy bar
            val elapsed = System.currentTimeMillis() - startTime
            val stillPresent = schemas.any { findMultiColors(schema = it) != null }
            if (!stillPresent || elapsed >= DEPLOY_TIMEOUT_MS) break

            // Drag back: ready for another forward sweep
            TouchActions.moveSmoothly(DRAG_END_X, DRAG_END_Y, DRAG_START_X, DRAG_START_Y, currentDragSweepMs, 1)
            delayWithMultiplier(300)

            // Check timeout again after the return sweep before the next forward drag
            if (System.currentTimeMillis() - startTime >= DEPLOY_TIMEOUT_MS) break

            // Progressively increase the drag duration if this is a slower troop
            if (dragSweepMs > 600) {
                currentDragSweepMs += 200
            }
        }
    } finally {
        // Always release the finger, even if cancelled
        withContext(NonCancellable) {
            TouchActions.touchUp(1)
        }
    }
}
