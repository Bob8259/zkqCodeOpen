# PR #132 — Change Summary

- **Repository:** `Bob8259/zkqCode-collaborate`
- **PR number:** 132 (oldest currently open PR)
- **Title:** fix: 修复升级时间上号模式目标账号循环重复登录的问题
- **Created at:** 2026-08-01T13:26:08Z
- **Scope of this document:** Changes under `app/src/main/java/com/coc/zkqcode/jar` only, excluding `suspend fun displayAds()` (not present in this PR).

## Comparison with local code

Each change below was compared against the current local working tree. **None of the PR's changes are present locally yet** — applying this PR would introduce all of them.

## Changed files

| # | File (under `app/src/main/java/com/coc/zkqcode/jar`) | Type of change |
|---|------------------------------------------------------|----------------|
| 1 | `code/universal/smalltools/UpgradeTimeTracker.kt`    | Modified (1 addition + 1 relocation + 1 block replacement) |
| 2 | `code/universal/smalltools/scheduling/UpgradeTimeSchedulingLogic.kt` | Modified (1 import + 1 call site) |

---

## 1. `UpgradeTimeTracker.kt`

### 1.1 New function `removeScheduleForAccount` (added)

Added right after `loadSchedule()` (local file: between current line 48 and the `// ── Called on game exit` comment).

```kotlin
/** Remove all schedule entries for a given account. */
suspend fun removeScheduleForAccount(account: Int) {
    val existing = loadSchedule().toMutableList()
    if (existing.removeAll { it.account == account }) {
        saveSchedule(existing)
    }
}
```

### 1.2 `checkUpgradeTimers()` — relocation of `val acc`

The line `val acc = InGameVars.currentAccountNumber` is **moved earlier** — from after `parseHelpers(...)` (local line 66) to immediately after the `jsonObject` parse (right before `extractTimerItems`).

Before (local order):

```kotlin
val jsonObject = try { Gson().fromJson(clipboardText.trim(), JsonObject::class.java) } catch (_: Exception) { return } ?: return
val timerItems = extractTimerItems(jsonObject)
if (timerItems.isEmpty()) return

val nowSec = System.currentTimeMillis() / 1000L
val jsonTs = jsonObject.get("timestamp")?.asLong ?: nowSec
val boosts = parseBoosts(jsonObject)
val helpers = parseHelpers(jsonObject)
val acc = InGameVars.currentAccountNumber
val advanceMin = ...
```

After (PR):

```kotlin
val jsonObject = try { Gson().fromJson(clipboardText.trim(), JsonObject::class.java) } catch (_: Exception) { return } ?: return
val acc = InGameVars.currentAccountNumber
val timerItems = extractTimerItems(jsonObject)
if (timerItems.isEmpty()) return

val nowSec = System.currentTimeMillis() / 1000L
val jsonTs = jsonObject.get("timestamp")?.asLong ?: nowSec
val boosts = parseBoosts(jsonObject)
val helpers = parseHelpers(jsonObject)
val advanceMin = ...
```

### 1.3 `checkScheduledUpgradeNotifications()` — cleanup logic replaced

The "Remove completed items" block at the end of the function (local lines 173-177) is **replaced** with a simpler save-on-change.

Removed (local):

```kotlin
// Remove completed items
val cleaned = schedule.filter { it.completionTs > nowSec }
if (cleaned.size != schedule.size || changed) {
    saveSchedule(cleaned)
}
```

Added (PR):

```kotlin
if (changed) saveSchedule(schedule)
```

> Note: completed entries are no longer filtered out here. Their removal is now handled explicitly via the new `removeScheduleForAccount(...)` call at the scheduling call site (see §2.2).

---

## 2. `UpgradeTimeSchedulingLogic.kt`

### 2.1 New import (added)

Added after the existing `loadSchedule` import (local line 11).

```kotlin
import com.coc.zkqcode.jar.code.universal.smalltools.removeScheduleForAccount
```

### 2.2 `runUpgradeTimeSchedulingScript()` — call inserted before `runAccountStepBlock()`

Inside the `if (target != null)` branch, a call to `removeScheduleForAccount(target.account)` is inserted between `delay(500L)` and `runAccountStepBlock()` (local lines 84-85).

Before (local):

```kotlin
ShowMessage(buildScheduleSummary(schedule, enabledAccounts, target.account, "升级完成，准备上号"), isChecking = false)
delay(500L)
runAccountStepBlock()
lastFillAccount = 0
```

After (PR):

```kotlin
ShowMessage(buildScheduleSummary(schedule, enabledAccounts, target.account, "升级完成，准备上号"), isChecking = false)
delay(500L)
removeScheduleForAccount(target.account)
runAccountStepBlock()
lastFillAccount = 0
```

---

## Summary of the fix

The PR resolves a loop where, in "升级时间上号模式" (upgrade-time login-scheduling mode), the target account would repeatedly re-login after its upgrade completed. Previously, completed schedule entries were pruned generically inside `checkScheduledUpgradeNotifications`. Now they are explicitly removed for the target account at the moment the account is about to be logged in (`removeScheduleForAccount(target.account)`), and the background checker only persists notified-state changes (`if (changed) saveSchedule(schedule)`) without filtering completed items.
