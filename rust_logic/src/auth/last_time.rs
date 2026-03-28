use std::sync::atomic::{AtomicI64, Ordering};
use std::sync::OnceLock;
use std::time::{Instant, SystemTime, UNIX_EPOCH};

use jni::sys::jlong;
use jni::JNIEnv;

/// Stores the last authentication timestamp in native memory, invisible to JVM inspection.
static LAST_TIME: AtomicI64 = AtomicI64::new(0);

// ── Monotonic clock for auth-call-frequency guard ──

/// Process-scoped anchor for the monotonic clock; created on first access.
static MONO_ANCHOR: OnceLock<Instant> = OnceLock::new();

/// Returns monotonic milliseconds since the process-scoped anchor.
/// Immune to system-time manipulation unlike `SystemTime`.
#[inline(always)]
pub fn mono_millis() -> i64 {
    MONO_ANCHOR.get_or_init(Instant::now).elapsed().as_millis() as i64
}

/// Tracks the monotonic timestamp of the most recent `getLastTime` JNI invocation.
static LAST_GET_CALL_TS: AtomicI64 = AtomicI64::new(0);

/// Returns the monotonic timestamp of the last `getLastTime` call, or 0 if never called.
#[inline(always)]
pub fn last_get_call_ts() -> i64 {
    LAST_GET_CALL_TS.load(Ordering::SeqCst)
}

/// Atomically seeds `LAST_GET_CALL_TS` to the current monotonic time only when still 0.
/// Used by the security monitor to start the 6-hour countdown without racing a real call.
#[inline(always)]
pub fn try_init_get_call_ts() {
    let now = mono_millis();
    let _ = LAST_GET_CALL_TS.compare_exchange(0, now, Ordering::SeqCst, Ordering::SeqCst);
}

/// Returns the current system time in milliseconds since UNIX epoch.
#[inline(always)]
fn current_millis() -> i64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_millis() as i64
}

/// Returns the stored last_time. If uninitialized (0), atomically sets it to the current
/// system time minus 5 minutes and returns that value. Uses CAS to avoid races on first init.
#[inline(always)]
pub fn get_or_init_last_time() -> i64 {
    let stored = LAST_TIME.load(Ordering::SeqCst);
    if stored != 0 {
        return stored;
    }
    // Offset by -5 minutes so the first call appears as if last auth was 5 minutes ago
    let now = current_millis() - 5 * 60 * 1000;
    match LAST_TIME.compare_exchange(0, now, Ordering::SeqCst, Ordering::SeqCst) {
        Ok(_) => now,
        Err(existing) => existing,
    }
}

/// Atomically overwrites the stored last_time with a new value.
#[inline(always)]
pub fn set_last_time(new_time: i64) {
    LAST_TIME.store(new_time, Ordering::SeqCst);
}

// ── JNI wrappers ──

#[inline(always)]
#[allow(non_snake_case)]
pub fn getLastTime(_env: JNIEnv, _class: jni::objects::JClass) -> jlong {
    // Record monotonic invocation time for auth-call-frequency guard
    LAST_GET_CALL_TS.store(mono_millis(), Ordering::SeqCst);
    get_or_init_last_time()
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn updateLastTime(_env: JNIEnv, _class: jni::objects::JClass, new_time: jlong) {
    set_last_time(new_time);
}
