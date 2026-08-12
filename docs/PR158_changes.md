# PR #158 — feat: 四面下兵在国服与国际服均可用

## Overview

| Field | Value |
| --- | --- |
| Repository | `Bob8259/zkqCode-collaborate` |
| PR Number | 158 |
| Title | feat: 四面下兵在国服与国际服均可用 |
| Created At | 2026-08-12T14:01:15Z |
| Status | Open (oldest open PR at time of reading) |

**Summary:** Removes the China-server (CN) restriction on the 4-side (四面) deploy
mode so that it works on both the CN server and the international (global) server.

## Scope

All changes are under `app/src/main/java/com/coc/zkqcode/jar`. Neither changed file
is `suspend fun displayAds()`, so both are included below.

> Note: The PR changes below are **not yet applied** to the local working copy.
> The local code still contains the CN-only condition and the red warning text.

---

## Changed Files

1. `app/src/main/java/com/coc/zkqcode/jar/code/mainbase/attack/SearchOpponents.kt`
2. `app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt`

---

## Change 1 — `SearchOpponents.kt`

**File:** `app/src/main/java/com/coc/zkqcode/jar/code/mainbase/attack/SearchOpponents.kt`
**Function:** `tryAutoBattleOrDeploy()` (fallback deploy branch)

### What changed

The CN-version-only guard was removed from the fallback deploy condition. Previously
the 4-side deploy path (`cnMainBaseDeployTroops`) only ran when the current game
version was `GameVersion.CN`; now it runs regardless of game version.

### Diff

```diff
     // Fallback: Mode "0" = original single-side deploy; otherwise CN 4-side deploy
+    // Fallback: Mode "0" = original single-side deploy; otherwise 4-side deploy
     if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.MANUAL_TRAINING.key) &&
-        getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_MODE.key) != "0" &&
-        InGameVars.currentGameVersion == GameVersion.CN
+        getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_MODE.key) != "0"
     ) {
         cnMainBaseDeployTroops()
     } else {
```

### Local code comparison

The local working copy still has the **old** (pre-PR) logic:

```kotlin
// Fallback: Mode "0" = original single-side deploy; otherwise CN 4-side deploy
if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.MANUAL_TRAINING.key) &&
    getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_MODE.key) != "0" &&
    InGameVars.currentGameVersion == GameVersion.CN
) {
    cnMainBaseDeployTroops()
} else {
```

### Changes to apply (if merging)

1. Update the comment from `otherwise CN 4-side deploy` → `otherwise 4-side deploy`.
2. Remove the `&& InGameVars.currentGameVersion == GameVersion.CN` condition from the
   `if` statement.

---

## Change 2 — `MainBaseConfigs.kt`

**File:** `app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt`
**Location:** `mainBaseConfig(...)` LazyListScope, "Deploy mode" section

### What changed

The `AnimatedVisibility` block showing the red warning text
`"四面下兵在国际服不生效"` ("4-side deploy does not work on the international server")
was removed entirely.

### Diff

```diff
                     SettingDropdown(
                         key = "${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index",
                         options = listOf(Strings.deployModeOriginal, Strings.deployModeSlider, Strings.deployModeFast)
                     )
-                    AnimatedVisibility(
-                        visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value != "0"
-                    ) {
-                        Text(
-                            text = "四面下兵在国际服不生效",
-                            color = Color.Red,
-                            style = MaterialTheme.typography.labelSmall,
-                            modifier = Modifier.padding(top = 2.dp)
-                        )
-                    }
                     AnimatedVisibility(
                         visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value != "0"
                     ) {
```

### Local code comparison

The local working copy still contains the block being removed (lines 343–352):

```kotlin
AnimatedVisibility(
    visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value != "0"
) {
    Text(
        text = "四面下兵在国际服不生效",
        color = Color.Red,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(top = 2.dp)
    )
}
```

### Changes to apply (if merging)

1. Delete the `AnimatedVisibility` block that displays the red warning text
   `"四面下兵在国际服不生效"`.

---

## Summary of Changes

| # | File | Change |
| --- | --- | --- |
| 1 | `SearchOpponents.kt` | Remove CN-only game version check so 4-side deploy works on both CN and international servers; update comment. |
| 2 | `MainBaseConfigs.kt` | Remove the red warning text "四面下兵在国际服不生效" from the deploy mode UI. |

No files were modified — this document lists the PR changes only.