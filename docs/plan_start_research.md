# Development Plan: Start Research Based on Priority List

## Objective

After `findAllResearchItems()` collects and sorts the upgradeable items by
priority, iterate through the sorted list and attempt to find and tap each
item on the research screen. For each item: reset the scroll position, then
swipe through up to 8 pages looking for it. If found, tap it and stop. If not
found after 8 swipes, move on to the next item in the priority list.

**Additionally**: Remove the `x, y` fields from `ResearchItem` since they are
not reused — the `startResearch()` phase re-detects positions via
`findMultiColors()`.

---

## Why removing `x, y` from `ResearchItem` is viable

The `x, y` coordinates are used in exactly **one place** during the scan phase:
to compute the insufficient-resources check area
`(x-35, y+59)` → `(x+105, y+84)`. This happens immediately after
`findMultiColors()` returns the position, and the coordinates are never
referenced again.

The `startResearch()` phase **cannot** reuse the stored coordinates because
scrolling changes the item's on-screen position. It must re-detect the
position via `findMultiColors()`.

Therefore, storing `x, y` in `ResearchItem` is dead data. The data class
becomes:

```kotlin
private data class ResearchItem(
    val name: String,   // Chinese name (e.g. "野蛮人")
    val priority: Int,  // Lower = higher priority
)
```

The `x, y` remain as local variables inside the scan loop where they are
needed for the insufficient-resources check.

---

## Context Summary

### Color schemas per item

Each research item may have **multiple color schemas** with the same `name`
field. For example:
- `RESEARCH_BARBARIAN` and `RESEARCH_BARBARIAN_2` both have name "野蛮人"
- `RESEARCH_LIGHTNING`, `RESEARCH_LIGHTNING_2`, `RESEARCH_LIGHTNING_3` all have name "雷电法术"

To search for a specific item by name, we need to filter
[`MainBaseResearchColors.allResearchColors`](app/src/main/java/com/coc/zkqcode/jar/code/colorschema/colorpackage/mainbase/MainBaseResearchColors.kt:97)
by `schema.name == targetName` and try each matching schema.

### Reference: BuilderBaseResearch upgrade step

[`BuilderBaseResearch.kt:127-132`](app/src/main/java/com/coc/zkqcode/jar/code/builderbase/research/BuilderBaseResearch.kt:127):
The builder base uses a fixed grid (no scrolling), so coordinates are stable
and can be reused. The main base requires re-detection after scrolling.

---

## Implementation Plan

### Step 1: Simplify `ResearchItem` data class

Change from:
```kotlin
private data class ResearchItem(
    val name: String,
    val priority: Int,
    val x: Int,
    val y: Int,
)
```
To:
```kotlin
private data class ResearchItem(
    val name: String,
    val priority: Int,
)
```

### Step 2: Update the scan loop to use local `x, y`

In `findAllResearchItems()`, the `itemPos` from `findMultiColors()` is already
a local variable. Just change the `upgradeableItems.add(...)` call to not pass
coordinates:

```kotlin
// Before:
upgradeableItems.add(ResearchItem(itemName, priority, itemPos.x, itemPos.y))
// After:
upgradeableItems.add(ResearchItem(itemName, priority))
```

### Step 3: Add `startResearch()` function

Add a new `suspend fun startResearch(upgradeableItems: List<ResearchItem>)`
after `findAllResearchItems()`.

#### Build a name → schemas lookup

```kotlin
val schemasByName = MainBaseResearchColors.allResearchColors
    .filter { it.name != null }
    .groupBy { it.name!! }
```

#### Search algorithm

```kotlin
for (item in upgradeableItems) {
    val schemas = schemasByName[item.name] ?: continue
    var found = false

    // Reset the research list to the beginning.
    TouchActions.swipe(240, 500, 4000, 500, delayAfter = 800)

    for (page in 0 until 8) {
        if (found) break

        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false)
            as? ScreenCaptureManager.CaptureResult ?: continue

        for (schema in schemas) {
            val pos = findMultiColors(
                schema = schema,
                byteBuffer = screenBuffer,
                increment = 1
            )
            if (pos != null) {
                // Found the item — tap it and exit.
                TouchActions.tap(pos.x, pos.y, delayTime = 800)
                found = true
                break
            }
        }

        if (!found) {
            // Swipe to the next page.
            TouchActions.swipe(950, 500, 200, 500, delayAfter = 300)
        }
    }

    if (found) {
        accountMessage("开始研究: ${item.name}")
        break
    }
    // If not found, the outer loop continues to the next item.
    // The reset swipe at the top of the next iteration handles re-scrolling.
}
```

### Step 4: Call `startResearch()` from `findAllResearchItems()`

At the end of `findAllResearchItems()`, after sorting and displaying the
message, if the list is non-empty:

```kotlin
if (upgradeableItems.isNotEmpty()) {
    startResearch(upgradeableItems)
}
```

---

## Files to Modify

| File | Change |
|------|--------|
| [`MainBaseResearch.kt`](app/src/main/java/com/coc/zkqcode/jar/code/mainbase/research/MainBaseResearch.kt:47) | Simplify `ResearchItem` (remove `x, y`); update `upgradeableItems.add()` call; add `startResearch()` function; call it at end of `findAllResearchItems()` |

No new imports needed — all required classes are already imported.

---

## Edge Cases

1. **Item not found after 8 swipes**: Move on to the next item in the
   priority list. The reset swipe at the start of the next iteration
   re-scrolls to the beginning.

2. **Screenshot capture fails**: Skip that page (`continue`) and try the next
   swipe.

3. **Item has multiple schemas**: Try each schema in order; the first match
   wins.

4. **Empty upgradeable list**: `startResearch()` is not called; the existing
   "no items" message is shown.

5. **All items not found**: After exhausting the entire list, the function
   returns without tapping anything. An appropriate message could be shown.

---

## Build & Verify

After implementation:
1. Run a Gradle debug build to verify compilation.
2. Fix any compilation errors.
