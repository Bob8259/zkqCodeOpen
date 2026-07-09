# PR #54 Merge Analysis & Plan

> **Source:** https://github.com/Bob8259/zkqCode-collaborate/pull/54
> **PR Branch:** `pr-54` (fetched from `collaborate` remote, ref `refs/pull/54/head`)
> **Local Branch:** `main` (HEAD: `3c5af419`)
> **Merge Base (divergence point):** `6f6d108c` ("feat: add functionality to claim hero hall rewards…")
> **Analysis Date:** Generated from local working tree

---

## 1. PR Summary

PR #54 contains **5 commits** (on top of the merge base `6f6d108c`):

| Commit | Subject |
|--------|---------|
| `9af1e691` | 修复推送通知发送测试并更新推送通知 (Fix push notification test send & update push notifications) |
| `49e06c78` | 重构推送通知调用入口 (Refactor push notification call entry points) |
| `526d0696` | fix: 修复空远程文件夹无法上传问题 & 新增一键复制/粘贴 & 智能按钮禁用 (Fix empty remote folder upload; add one-click copy/paste; smart button disable) |
| `015cb52e` | refactor: 将ShellHelper文件操作替换为java.io.File API (Replace ShellHelper file ops with java.io.File API) |
| `61da83be` | Revert "refactor: 将ShellHelper文件操作替换为java.io.File API" |

**Net effect:** The last two commits (`015cb52e` + `61da83be`) cancel each other out — the ShellHelper→java.io.File refactor was reverted. So the PR's effective changes are the first three commits: a **push notification refactor** + **WebDAV copy/paste & folder-management UI improvements**.

The PR touches **13 files** (+577 / −233 lines):

```
.../jar/code/auth/Auth.kt                              |  10 +-
.../jar/code/mainbase/clan/ClanLeague.kt               |  11 +-
.../jar/code/mainbase/clan/ClanWar.kt                  |  11 +-
.../jar/code/universal/EnterGameCheck.kt               |  12 +-
.../jar/code/universal/UpgradeTimeTracker.kt           |   7 +-
.../jar/code/universal/push/PushMessage.kt             |  12 +  (new file)
.../jar/code/universal/push/PushPlusHelper.kt          |   8 +-
.../jar/code/universal/push/PushRouter.kt              |  17 +  (new file)
.../jar/code/universal/push/ServerChanHelper.kt        |  43 +-
.../jar/code/universal/push/TelegramHelper.kt         |   7 +-
.../jar/code/universal/push/WxPusherHelper.kt          |   7 +-
.../jar/ui/Strings.kt                                  |  27 +-
.../jar/ui/pages/single/SpecialSettings.kt            | 638 ++++++++++++++++-----
```

---

## 2. Comparison Against Local Code

Your local `main` has diverged significantly from the merge base — **22 commits** (+1523 / −635 lines across 49 files), covering app-update checks, WebDAV folder management, research/equipment logic, pass rewards, troop-deployment debug logging, and more.

### 2.1 Files that OVERLAP (touched by both PR and local)

All 13 PR files overlap with local changes. They fall into three categories:

#### Category A — Already merged (identical changes) ✅

These files were changed in the PR **and** your local code already contains the **exact same** changes. Git will auto-merge these with no conflict and no net change.

| File | Change |
|------|--------|
| `push/PushMessage.kt` | New `PushMessage` data class — **identical** |
| `push/PushRouter.kt` | New `PushRouter` dispatcher — **identical** |
| `push/PushPlusHelper.kt` | Added `sendNotification(PushMessage)` overload — **identical** |
| `push/TelegramHelper.kt` | Added `sendNotification(PushMessage)` overload — **identical** |
| `push/WxPusherHelper.kt` | Added `sendNotification(PushMessage)` overload — **identical** |
| `push/ServerChanHelper.kt` | Removed router logic; added `send(PushMessage)` — **identical** |
| `code/auth/Auth.kt` | Switched call site to `PushRouter.send(PushMessage(...))` — **identical** — **EXCLUDED from merge per decision (see §4.0)** |
| `code/universal/EnterGameCheck.kt` | Switched call site to `PushRouter.send(...)` — **identical** |
| `code/universal/UpgradeTimeTracker.kt` | Switched 2 call sites to `PushRouter.send(...)` — **identical** |

> **Conclusion for Category A:** The entire push-notification refactor (commits `9af1e691` + `49e06c78`) is **already present in your local `main`**. Merging the PR contributes nothing new here. Additionally, [`code/auth/Auth.kt`](app/src/main/java/com/coc/zkqcode/jar/code/auth/Auth.kt:1) is **explicitly excluded** from the merge per the decision in §4.0 — even though it is already identical locally, no Auth-related changes are to be adopted.

#### Category B — Auto-merges cleanly but with LOCAL DIVERGENCE ⚠️

| File | PR change | Local change | Merge result |
|------|-----------|--------------|--------------|
| `code/mainbase/clan/ClanLeague.kt` | Switches push call to `PushRouter.send(PushMessage(...))` | **Same** push switch **PLUS** refactored cooldown logic (moved the 12h cooldown guard from a hard skip to a conditional that only gates the push notification) | Auto-merges. Local cooldown refactor is preserved; PR's push change is already present locally → **net zero from PR** |
| `code/mainbase/clan/ClanWar.kt` | Switches push call to `PushRouter.send(PushMessage(...))` | **Same** push switch **PLUS** same cooldown-logic refactor as ClanLeague | Auto-merges. Same as above → **net zero from PR** |
| `ui/Strings.kt` | Removes `sectionOther`, `petAndEquipment`, `others`; renames `sectionPet`→"战宠设置"; changes equipment-scope labels; adds `upgradeScopeAll`; adds `serverchanTestSending/Success/Failed` | Adds WebDAV folder-management strings (`deleteFolder`, `renameFolder`, `saveFolderName`, `folderNameLabel`, `webdavSyncMode*`, `webdavEditMode*`, etc.) **PLUS** the same `serverchanTestSending/Success/Failed` strings | Auto-merges (different line regions), **BUT** the PR's `petAndEquipment`/`sectionOther`/`others` removals and equipment-scope label changes are **explicitly EXCLUDED from merge** per §4.0 — and are also unsafe (see §3.1) |

#### Category C — CONFLICT (manual resolution required) ❌

| File | Conflict regions | Root cause |
|------|------------------|------------|
| `ui/pages/single/SpecialSettings.kt` | ~18 conflict regions (lines 164–796) | Both branches heavily rewrote the WebDAV section. **Local** implemented advanced folder management (list/expand, delete, rename, create, upload/download with overwrite vs. incremental sync modes, smart button-disable based on remote file presence). **PR** implemented a simpler "一键复制/一键粘贴" (copy/paste WebDAV credentials) feature plus a different folder-list/upload UI. The two implementations are structurally incompatible in the same code regions. |

---

## 3. Key Findings & Risks

### 3.1 ⚠️ PR breaks compilation (pre-existing PR bug)

The PR **removes** these string keys from [`Strings.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/Strings.kt:1):

- `sectionOther`
- `petAndEquipment`
- `others`

However, these keys are **still referenced** in code the PR does **not** modify:

- [`MainBaseConfigs.kt:491`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt:491) → `Strings.petAndEquipment`
- [`MainBaseConfigs.kt:537`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt:537) → `Strings.others`
- [`HomeScreen.kt:140`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/homescreen/HomeScreen.kt:140) → `Strings.sectionOther`

> If the PR's `Strings.kt` changes are taken as-is, the project will **fail to compile**. These references must either be updated to the PR's new keys (`petAndOther`, `sectionPet`) or the removed keys must be retained.

### 3.2 ✅ Push refactor is redundant

The push-notification refactor (the bulk of the PR's line count across 9 files) is **already in your local `main`**. Merging it produces no benefit — only the WebDAV UI work in `SpecialSettings.kt` and the `Strings.kt` label changes are genuinely new.

### 3.3 ⚠️ WebDAV feature overlap is a design decision

Your local WebDAV folder management is **more feature-complete** than the PR's:

| Feature | Local `main` | PR #54 |
|---------|:------------:|:------:|
| Folder list + expand/collapse | ✅ | ✅ (different impl) |
| Create folder | ✅ | ✅ |
| Delete folder | ✅ | ❌ |
| Rename folder | ✅ | ❌ |
| Overwrite vs. Incremental sync modes | ✅ (dropdown) | ✅ (checkboxes, simpler) |
| Smart button-disable (checks remote files) | ✅ | ✅ |
| One-click copy/paste WebDAV credentials | ❌ | ✅ |
| Empty-remote-folder upload fix | ✅ (via `WebDAVHelper`) | ✅ (commit `526d0696`) |

The **only genuinely new feature** the PR offers that your local code lacks is the **一键复制/一键粘贴 (copy/paste WebDAV credentials)** convenience feature.

---

## 4. Merge Plan

### 4.0 Exclusions (do NOT merge these)

The following PR changes are **explicitly excluded** from the merge by decision. They must not be adopted, ported, or cherry-picked:

| Excluded area | PR files / changes affected | Reason |
|---------------|----------------------------|--------|
| **`petAndEquipment` & related label changes** | [`Strings.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/Strings.kt:1) — removal of `sectionOther`, `petAndEquipment`, `others`; rename of `sectionPet`→"战宠设置"; equipment-scope label changes (`upgradeScopeWear`, `upgradeScopeCustom`, new `upgradeScopeAll`) | Excluded by decision. These are also unsafe on their own (§3.1) because the removed keys are still referenced in [`MainBaseConfigs.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt:491) and [`HomeScreen.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/homescreen/HomeScreen.kt:140). |
| **`Auth`-related changes** | [`code/auth/Auth.kt`](app/src/main/java/com/coc/zkqcode/jar/code/auth/Auth.kt:1) — the push call-site switch to `PushRouter.send(PushMessage(...))` | Excluded by decision. (Note: this change is already present identically in local `main`, so excluding it has no practical effect — it simply confirms no Auth work is to be taken from the PR.) |

> **Net effect of exclusions:** The `Strings.kt` label/equipment-scope changes are dropped entirely. The `Auth.kt` change is a no-op (already local). The remaining mergeable content is limited to the WebDAV copy/paste feature in `SpecialSettings.kt` plus the `serverchanTestSending/Success/Failed` strings (already local).

### Recommended strategy: **Cherry-pick only the net-new feature, do NOT merge the PR branch**

Because 9 of 13 PR files are already in your local code, the `Auth` and `petAndEquipment`/label changes are excluded (§4.0), and the remaining work is concentrated in `SpecialSettings.kt` (conflicting) + `Strings.kt` (partly excluded), a full `git merge pr-54` would create a large manual conflict in `SpecialSettings.kt` for very little gain.

#### Step 1 — Do NOT merge `pr-54` directly

A full merge would:
- Auto-apply 9 files with zero net change (harmless but noisy).
- Auto-merge `ClanLeague.kt` / `ClanWar.kt` / `Strings.kt` cleanly (local wins on cooldown logic).
- Produce an **18-region conflict in `SpecialSettings.kt`** requiring manual resolution.
- Introduce the **compile-breaking `Strings.kt` removals** (§3.1) — which are also **excluded by decision** (§4.0).
- Pull in the **excluded `Auth.kt`** change (§4.0), though it is a no-op since it is already local.

#### Step 2 — Manually port the one net-new feature: "一键复制/一键粘贴"

The only feature in the PR not present locally is the WebDAV credential copy/paste UI. Port it manually into your local [`SpecialSettings.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/single/SpecialSettings.kt:1):

1. Add the two `CustomButton` rows ("一键复制" / "一键粘贴") and the `Popup` paste dialog from the PR's `SpecialSettings.kt` diff, inserting them **after** the WebDAV credential input rows (`SettingInputRow` for URL/username/password) and **before** your local folder-management UI.
2. Required imports already present locally: `ClipData`, `ClipboardManager`, `Popup`, `PopupProperties`, `Surface`, `BasicTextField`, `RoundedCornerShape` — verify none are missing.
3. The copy/paste feature uses hardcoded Chinese strings ("一键复制", "已复制服务器地址、用户名、密码", etc.). Per project rules, these should be routed through the localization layer. Add new keys to [`Strings.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/Strings.kt:1) (e.g. `webdavCopyCredentials`, `webdavPasteCredentials`, `webdavPasteDialogTitle`, `webdavPasteConfirm`, `webdavPasteCancel`, `webdavCopiedMsg`, `webdavPastedMsg`, `webdavPasteInvalidMsg`) with both Chinese and English values, then reference them from the UI.

#### Step 3 — Verify the empty-remote-folder upload fix is present

Commit `526d0696` fixes empty remote folder uploads. Your local code already has [`WebDAVHelper.kt`](app/src/main/java/com/coc/zkqcode/jar/code/universal/network/WebDAVHelper.kt:1) (added locally). Confirm the `ensureRemotePath` logic handles empty folders equivalently — if not, port the specific fix.

#### Step 4 — Do NOT adopt the PR's `Strings.kt` label / `petAndEquipment` changes (excluded per §4.0)

The PR's removal of `sectionOther` / `petAndEquipment` / `others` and the equipment-scope label renames (`upgradeScopeWear`→"仅穿戴装备", `upgradeScopeCustom`→"仅指定装备", new `upgradeScopeAll`) are **explicitly excluded from the merge** (§4.0). They are also **not** accompanied by updates to the referencing files, so adopting them would break compilation (§3.1). These changes are to be dropped entirely — do not port them now or later unless done as a **separate, complete** change that also updates [`MainBaseConfigs.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/mainbase/MainBaseConfigs.kt:491) and [`HomeScreen.kt`](app/src/main/java/com/coc/zkqcode/jar/ui/pages/homescreen/HomeScreen.kt:140).

#### Step 5 — Build & verify

After porting the copy/paste feature:

1. Run a Gradle debug build:
   ```
   gradlew assembleDebug
   ```
2. Confirm no compile errors (especially around `Strings.kt` references).
3. Manually test the WebDAV copy/paste and existing folder-management features together in the Special Settings screen.

---

## 5. Summary Table

| PR file | Status on merge | Action |
|---------|-----------------|--------|
| `push/PushMessage.kt` | Already in local (identical) | None needed |
| `push/PushRouter.kt` | Already in local (identical) | None needed |
| `push/PushPlusHelper.kt` | Already in local (identical) | None needed |
| `push/TelegramHelper.kt` | Already in local (identical) | None needed |
| `push/WxPusherHelper.kt` | Already in local (identical) | None needed |
| `push/ServerChanHelper.kt` | Already in local (identical) | None needed |
| `code/auth/Auth.kt` | Already in local (identical) | **EXCLUDED** (§4.0) — no Auth changes to be adopted |
| `code/universal/EnterGameCheck.kt` | Already in local (identical) | None needed |
| `code/universal/UpgradeTimeTracker.kt` | Already in local (identical) | None needed |
| `code/mainbase/clan/ClanLeague.kt` | Auto-merges; local cooldown refactor wins | None needed |
| `code/mainbase/clan/ClanWar.kt` | Auto-merges; local cooldown refactor wins | None needed |
| `ui/Strings.kt` | Auto-merges but PR removals break compilation | **EXCLUDED** (§4.0) — reject `petAndEquipment`/label changes; add new copy/paste string keys only |
| `ui/pages/single/SpecialSettings.kt` | **CONFLICT** (18 regions) | **Manually port** only the copy/paste feature into local UI |

**Bottom line:** The PR is largely already merged into your local `main`. The `Auth`-related and `petAndEquipment`/label changes are **explicitly excluded** (§4.0). The only net-new value is the WebDAV credential copy/paste convenience feature. Do **not** merge the PR branch directly — port that single feature manually, add localized strings for it, and skip the PR's compile-breaking `Strings.kt` label changes.
