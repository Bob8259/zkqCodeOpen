use std::sync::atomic::{AtomicI64, Ordering};
use std::time::{SystemTime, UNIX_EPOCH};

use jni::sys::jlong;
use jni::JNIEnv;

/// Stores the last authentication timestamp in native memory, invisible to JVM inspection.
static LAST_TIME: AtomicI64 = AtomicI64::new(0);

/// Returns the current system time in milliseconds since UNIX epoch.
#[inline(always)]
fn current_millis() -> i64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_millis() as i64
}

/// Returns the stored last_time. If uninitialized (0), atomically sets it to the current
/// system time and returns that value. Uses CAS to avoid races on first init.
#[inline(always)]
pub fn get_or_init_last_time() -> i64 {
    let stored = LAST_TIME.load(Ordering::SeqCst);
    if stored != 0 {
        return stored;
    }
    let now = current_millis();
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
    get_or_init_last_time()
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn updateLastTime(_env: JNIEnv, _class: jni::objects::JClass, new_time: jlong) {
    set_last_time(new_time);
}
