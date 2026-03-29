use std::sync::atomic::{AtomicI64, Ordering};

use jni::sys::jboolean;
use jni::JNIEnv;
use rand::Rng;

use crate::auth::last_time::mono_millis;
use crate::security::obfuscated::ObfuscatedAtomicI32;
use crate::security::obfuscated::ObfuscatedAtomicI64;

/// Tracks whether the user has passed advertisement authentication, stored in native memory.
/// Uses an obfuscated integer instead of a plain boolean: values > 10000 mean passed,
/// values < 10000 mean not passed. Re-randomized on every read to defeat memory scanners.
/// Additionally XOR-obfuscated with re-keying for defense in depth.
pub static IS_AUTH_PASS: ObfuscatedAtomicI32 = ObfuscatedAtomicI32::new(0);

/// Monotonic timestamp (ms) of the last ad display; 0 means no ad has been shown yet.
/// XOR-obfuscated with re-keying to defeat memory scanners.
pub static LAST_AD_DISPLAY_TS: ObfuscatedAtomicI64 = ObfuscatedAtomicI64::new(0);

/// Monotonic timestamp (ms) when the current ad display started; 0 means idle.
static AD_DISPLAY_START_TS: AtomicI64 = AtomicI64::new(0);

/// Minimum elapsed time (ms) an ad display must take to be considered legitimate.
const AD_MIN_DURATION_MS: i64 = 10_000;

/// Maximum interval (ms) between ad displays when IS_AUTH_PASS is false.
const AD_MAX_INTERVAL_MS: i64 = 15 * 60 * 1000;

/// Returns the current value of the auth-pass flag.
/// Also re-randomizes the stored integer within the same range to keep the in-memory
/// value constantly changing, making it harder to locate or patch via memory scanning.
#[inline(always)]
pub fn is_auth_pass() -> bool {
    let val = IS_AUTH_PASS.load();
    let passed = val > 10000;
    // Re-randomize in the same range so the decoded value keeps changing too
    let new_val = if passed {
        rand::thread_rng().gen_range(10001..=i32::MAX)
    } else {
        rand::thread_rng().gen_range(0..10000)
    };
    IS_AUTH_PASS.store(new_val);
    passed
}

/// Revokes the auth-pass flag and resets the ad display timestamp so the
/// 10-minute frequency countdown restarts from this moment.
#[inline(always)]
pub fn revoke_auth_pass() {
    IS_AUTH_PASS.store(rand::thread_rng().gen_range(0..10000));
    LAST_AD_DISPLAY_TS.store(0);
}

// ── Ad display duration enforcement ──

/// Records the monotonic start time of an ad display.
#[inline(always)]
fn mark_ad_start() {
    AD_DISPLAY_START_TS.store(mono_millis(), Ordering::SeqCst);
}

/// Validates that the ad display lasted at least AD_MIN_DURATION_MS.
/// Stamps LAST_AD_DISPLAY_TS regardless, then resets the start marker.
/// Silently triggers poison if the duration is suspiciously short (hook detected).
#[inline(always)]
fn mark_ad_end() {
    let start = AD_DISPLAY_START_TS.load(Ordering::SeqCst);
    let now = mono_millis();

    // Always stamp the last-display time so the frequency guard is satisfied
    LAST_AD_DISPLAY_TS.store(now);
    AD_DISPLAY_START_TS.store(0, Ordering::SeqCst);

    if start == 0 {
        // markAdStart was never called — treat as tamper
        #[cfg(not(debug_assertions))]
        {
            crate::security::anti_debug::G_SECURITY_POISON_FLAG
                .store(now as i32 | 1, Ordering::SeqCst);
            crate::security::anti_debug::G_SECURITY_POISON_FLAG_3
                .fetch_add(1, Ordering::SeqCst);
        }
        return;
    }

    let elapsed = now - start;
    if elapsed < AD_MIN_DURATION_MS {
        // Ad completed too fast — likely hooked / bypassed
        #[cfg(not(debug_assertions))]
        {
            crate::security::anti_debug::G_SECURITY_POISON_FLAG
                .store(now as i32 | 1, Ordering::SeqCst);
            crate::security::anti_debug::G_SECURITY_POISON_FLAG_3
                .fetch_add(1, Ordering::SeqCst);
        }
    }
}

// ── Ad display frequency enforcement (called by security monitor) ──

/// Checks that ads are being displayed at least once per AD_MAX_INTERVAL_MS
/// when IS_AUTH_PASS is false. Seeds the timestamp on first invocation.
#[cfg(not(debug_assertions))]
#[inline(always)]
pub fn check_ad_display_frequency() {
    if is_auth_pass() {
        return;
    }

    let ts = LAST_AD_DISPLAY_TS.load();
    if ts == 0 {
        // First check — seed the monotonic timer so the countdown starts now
        let now = mono_millis();
        let _ = LAST_AD_DISPLAY_TS.compare_exchange(0, now);
        return;
    }

    let now = mono_millis();
    if now - ts > AD_MAX_INTERVAL_MS {
        // Log the trigger reason for debugging
        log::info!("rust_zkq_debug: ad display interval exceeded max ({}ms > {}ms), poison triggered", now - ts, AD_MAX_INTERVAL_MS);
        crate::security::anti_debug::G_SECURITY_POISON_FLAG
            .store(now as i32 | 1, Ordering::SeqCst);
        crate::security::anti_debug::G_SECURITY_POISON_FLAG_3
            .fetch_add(1, Ordering::SeqCst);
    }
}

// ── JNI wrappers ──

#[inline(always)]
#[allow(non_snake_case)]
pub fn getIsAuthPass(_env: JNIEnv, _class: jni::objects::JClass) -> jboolean {
    is_auth_pass() as jboolean
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn revokeAuthPass(_env: JNIEnv, _class: jni::objects::JClass) {
    revoke_auth_pass();
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn markAdStart(_env: JNIEnv, _class: jni::objects::JClass) {
    mark_ad_start();
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn markAdEnd(_env: JNIEnv, _class: jni::objects::JClass) {
    mark_ad_end();
}
