# Plan: Fix ScreenCaptureManager after rotation (API 34+)

## Problem summary

After screen rotation, logs show:

```text
ensureVirtualDisplay: Size changed but API 34+ prevents recreation, will use shell fallback
```

At the same time the status-bar **recording icon remains**. That is expected and important:

- The **MediaProjection session is still alive** (hence the icon).
- The code only refuses to call `createVirtualDisplay()` again on the **same** projection instance (Android 14+ restriction).
- Current code **returns early** without resizing/recreating the display, then later capture uses **new metrics** with the **old ImageReader/VirtualDisplay**, producing wrong `CaptureResult` metadata (e.g. reported `1280x720` with `rowStride=2880` and empty center pixel).

Observed bad frame example:

```text
ok 5ms 1280x720 pixelStride=4 rowStride=2880 cap=3686400 center=RGBA(0,0,0,0)
```

- `rowStride=2880` = `720 * 4` → buffer geometry is portrait-like.
- Reported `width/height` come from `updateMetrics()` (`screenWidth`/`screenHeight`), not from the actual image plane.
- So rotation breaks **geometry consistency**, not necessarily “permission lost”.

## Root cause chain

Primary file: [`ScreenCaptureManager.kt`](app/src/main/java/com/coc/zkqcode/core/system/screencapture/ScreenCaptureManager.kt)

1. Every capture calls `updateMetrics()` → width/height swap on rotation.
2. `ensureVirtualDisplay()` detects `imageReader` size ≠ new metrics.
3. On API 34+ (`canRecreateVirtualDisplay() == false`) it **logs and returns**, leaving old `virtualDisplay` + `imageReader` alive.
4. Capture still proceeds with old surface, but labels the buffer with **new** `screenWidth`/`screenHeight`.
5. Consumers (`findMultiColors`, etc.) index with wrong width/height → false negatives / wrong coords.
6. If capture later fails, `useShellFallback = true` permanently skips MediaProjection even though the recording session may still be active.

Secondary issues:

- `CaptureResult.width/height` always use metrics, never ImageReader/plane truth.
- Size-change path does not try `VirtualDisplay.resize()`.
- Permanent shell fallback is too aggressive for a recoverable size-change case.
- App product target is `720x1280`; rotation to landscape is outside the intended runtime geometry.

## Goals

1. After rotation (or any size change), capture must return a buffer whose **width/height/rowStride** are self-consistent.
2. Prefer keeping MediaProjection when the session is still valid (recording icon present).
3. Avoid permanent shell lockout solely because of orientation/size change.
4. Keep shell fallback only for truly broken MediaProjection pipelines.
5. Preserve existing public API: `capture(asBitmap)`, `CaptureResult`.

## Non-goals

- Redesigning all color-detection call sites.
- Removing shell fallback entirely.
- Supporting arbitrary multi-display setups beyond default display.

## Proposed solution

### Strategy A (preferred): resize in place on size change

When metrics change and a VirtualDisplay already exists:

1. Create a new `ImageReader` at the new size.
2. Call `virtualDisplay.resize(newW, newH, density)`.
3. Call `virtualDisplay.setSurface(newImageReader.surface)`.
4. Close the old `ImageReader` after surface swap.
5. Invalidate frame caches (`cachedBitmap`, `cachedCaptureResult`, `lastFrameTimestamp`).
6. Continue MediaProjection capture (no shell, no re-permission).

Why this is preferred:

- Does **not** call `createVirtualDisplay()` again.
- Matches Android 14+ constraint better than full recreation.
- Keeps the existing projection token/session (recording icon stays, correctly).

Fallback if resize/setSurface fails:

- Clean display resources.
- Temporary shell capture for this request.
- Do **not** set permanent `useShellFallback` immediately; attempt MediaProjection recovery on next captures, or request a fresh projection only if projection itself dies.

### Strategy B: derive geometry from the image, not metrics alone

In `processImage()` / shell conversion:

- Prefer actual buffer geometry:
  - `width`/`height` from `image.width`/`image.height` (or ImageReader size)
  - `pixelStride`/`rowStride` from plane
- Only use metrics as a bootstrap for creating VirtualDisplay/ImageReader.
- Optionally sanity-check: if `rowStride < width * pixelStride`, treat as invalid and recover.

This fixes wrong labeling even if metrics race with display size.

### Strategy C: recover projection only when session is dead

Split failure modes:

| Failure | Action |
|--------|--------|
| Size changed, projection alive | Resize + new ImageReader (Strategy A) |
| Projection null / callback `onStop` | Re-request permission or shell temporary |
| Image acquire timeout, pipeline alive | Return cache if valid; else short retry |
| Repeated MediaProjection hard failures | Then set `useShellFallback = true` |

Do **not** treat “size changed on API 34+” as “MediaProjection is dead”.

### Strategy D (product hardening, optional but recommended)

Because scripts assume `720x1280`:

1. Keep warning in `cleanUp()` when resolution ≠ `720x1280`.
2. Optionally force/lock capture target to portrait `720x1280` when device reports landscape swapped metrics (if that matches runtime policy).
3. Or document that rotation during run is unsupported and auto-restore orientation via shell (`wm user-rotation` / settings) before capture.

Choose one policy explicitly during implementation:

- **Policy P1:** Support rotation by resizing capture pipeline.
- **Policy P2:** Detect non-720x1280 and auto-correct orientation/size, keep capture fixed.

Recommended default: **P1 for correctness** + keep resolution warning; add P2 only if product wants hard lock.

## Implementation plan (ordered)

### 1) Instrumentation (keep/extend `runTestCode`)

In [`MainScript.kt`](app/src/main/java/com/coc/zkqcode/jar/code/MainScript.kt) test loop, log:

- metrics W/H
- ImageReader W/H (expose a debug getter if needed)
- CaptureResult W/H, strides, capacity
- non-zero / opaque pixel counts
- whether path was MediaProjection vs shell
- before/after rotation comparison

Success criteria for later verification:

- After rotate, either:
  - MediaProjection path still works with consistent geometry, or
  - temporary shell works without permanent lockout and recovers.

### 2) Fix size-change handling in `ensureVirtualDisplay`

File: [`ScreenCaptureManager.kt`](app/src/main/java/com/coc/zkqcode/core/system/screencapture/ScreenCaptureManager.kt)

Replace API34 early-return with:

```text
if (sizeChanged) {
  try resize path (all API levels if VirtualDisplay exists)
  on failure:
    if (canRecreateVirtualDisplay()) recreate via createVirtualDisplay
    else mark sizeMismatchPending and return without pretending success
}
```

Details:

- Add helper `rebindVirtualDisplayToNewSize(projection, w, h, dpi)`.
- Clear caches on any rebind.
- Ensure listener is cleared before closing old ImageReader.
- Threading: keep using `backgroundHandler`.

### 3) Make `CaptureResult` geometry trustworthy

In MediaProjection `processImage`:

- Use `image.width` / `image.height` for result dimensions.
- Compute row padding from plane against **image width**, not metrics width.
- Same for bitmap crop path.

In shell path ([`ShellScreenCapture.kt`](app/src/main/java/com/coc/zkqcode/core/system/screencapture/ShellScreenCapture.kt)):

- Already uses bitmap width/height; keep that as source of truth.

### 4) Soften permanent shell fallback

Current:

```text
projectionResult == null -> useShellFallback = true forever
```

Change to:

- Count consecutive hard failures.
- Size-mismatch / first null frame should **not** immediately permanent-fallback.
- Permanent fallback only after N consecutive pipeline failures or projection `onStop` without recoverable token.
- Add `clearShellFallback()` when projection is healthy again / new permission granted (already partially done in `onPermissionGranted`).

### 5) Explicit recovery API (optional but useful)

Add something like:

- `fun invalidateDisplayResources(reason: String)`
- `suspend fun capture(...):` already recovers internally

If resize fails on API 34+ and token cannot recreate VD:

- Option 5a: auto re-request MediaProjection permission (needs Activity/launcher path via existing `ProjectionPermissionHelper`).
- Option 5b: shell until user re-grants.

Prefer 5b for unattended bot runs; optionally 5a when UI is available.

### 6) Regression tests / manual matrix

Manual device checks (root + API 34+):

1. Cold start, grant projection, capture `asBitmap=false` at 720x1280.
2. Rotate to landscape, capture again.
3. Rotate back to portrait, capture again.
4. Confirm recording icon behavior and whether MediaProjection remains preferred.
5. Kill projection from system UI, confirm shell fallback and logs.
6. Re-grant permission, confirm MediaProjection resumes (`useShellFallback` cleared).
7. Run one real consumer path (`findMultiColors`) before/after rotation.

Debug build after each code change (`gradlew :app:compileDebugKotlin` / debug assemble as usual).

## Concrete code touch points

| Area | File | Change |
|------|------|--------|
| Size change / resize | `ScreenCaptureManager.kt` | Replace API34 no-op return with resize/rebind |
| Result geometry | `ScreenCaptureManager.kt` | Use image dimensions in `processImage` |
| Fallback policy | `ScreenCaptureManager.kt` | Soften `useShellFallback` permanence |
| Debug visibility | `ScreenCaptureManager.kt` + `MainScript.kt` | Expose path + sizes in test logs |
| Shell path | `ShellScreenCapture.kt` | Only if needed for consistency/logging |
| Permission reacquire | `ProjectionPermissionHelper.kt` / `MainActivity.kt` | Only if choosing auto re-request |

## Risk analysis

| Risk | Mitigation |
|------|------------|
| `VirtualDisplay.resize` not reliable on some OEMs | Catch failures; fall back to recreate (pre-34) or temporary shell (34+) |
| Surface race while closing old ImageReader | Clear listener first; swap surface; then close old reader |
| Cached stale frame after rotate | Always invalidate caches on size rebind |
| Scripts assume 720x1280 coords | Even with correct capture, landscape breaks game scripts; keep resolution warning / optional orientation lock |
| Auto permission dialog interrupts unattended runs | Prefer shell temporary recovery over forced dialog |

## Recommended decision points for approval

1. **Rotation policy:** support resize (P1) vs force portrait 720x1280 (P2)?
2. **If resize fails on API 34+:** temporary shell only, or auto re-request MediaProjection?
3. **Permanent shell fallback:** disable entirely except projection death, or keep with failure threshold (recommend threshold = 3)?

Suggested defaults:

- P1 resize support
- temporary shell on resize failure
- permanent shell only after repeated hard failures / projection stop
- keep current `runTestCode` diagnostics until fix verified

## Execution phases

### Phase 1 — Correctness fix (must)
1. Resize/rebind on size change.
2. CaptureResult uses image geometry.
3. Invalidate caches on rebind.
4. Soften fallback lockout for size-change.

### Phase 2 — Recovery polish
1. Failure counters / better logs.
2. Optional auto re-permission.
3. Optional orientation lock policy.

### Phase 3 — Cleanup
1. Remove or gate verbose `runTestCode` once verified.
2. Final debug build + rotation matrix sign-off.

## Done criteria

- Rotating screen no longer produces inconsistent `width/rowStride` pairs.
- Center/opaque pixel samples look like a real frame (not all zeros) on a normal game/home screen.
- Recording icon can remain while MediaProjection continues to work after rotation.
- Shell fallback is used only when MediaProjection is actually unusable.
- Existing call sites keep compiling with no API break.
- Debug build succeeds.
