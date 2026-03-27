use std::sync::atomic::{AtomicBool, Ordering};

use jni::sys::jboolean;
use jni::JNIEnv;

/// Tracks whether the user has passed advertisement authentication, stored in native memory.
pub static IS_AUTH_PASS: AtomicBool = AtomicBool::new(false);

/// Returns the current value of the auth-pass flag.
#[inline(always)]
pub fn is_auth_pass() -> bool {
    IS_AUTH_PASS.load(Ordering::SeqCst)
}

// ── JNI wrapper ──

#[inline(always)]
#[allow(non_snake_case)]
pub fn getIsAuthPass(_env: JNIEnv, _class: jni::objects::JClass) -> jboolean {
    is_auth_pass() as jboolean
}
