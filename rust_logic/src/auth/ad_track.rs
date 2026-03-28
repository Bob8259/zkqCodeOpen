use std::sync::atomic::{AtomicBool, AtomicI64, Ordering};

use jni::sys::jboolean;
use jni::JNIEnv;

/// Tracks whether the user has passed advertisement authentication, stored in native memory.
pub static IS_AUTH_PASS: AtomicBool = AtomicBool::new(false);

/// Monotonic timestamp (ms) of the last ad display; 0 means no ad has been shown yet.
pub static LAST_AD_DISPLAY_TS: AtomicI64 = AtomicI64::new(0);

/// Returns the current value of the auth-pass flag.
#[inline(always)]
pub fn is_auth_pass() -> bool {
    IS_AUTH_PASS.load(Ordering::SeqCst)
}

/// Revokes the auth-pass flag by unconditionally setting it to false.
#[inline(always)]
pub fn revoke_auth_pass() {
    IS_AUTH_PASS.store(false, Ordering::SeqCst);
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

/// Resets the last ad display timestamp to 0.
#[inline(always)]
pub fn reset_last_ad_display_ts() {
    LAST_AD_DISPLAY_TS.store(0, Ordering::SeqCst);
}

#[inline(always)]
#[allow(non_snake_case)]
pub fn resetLastAdDisplayTs(_env: JNIEnv, _class: jni::objects::JClass) {
    reset_last_ad_display_ts();
}
