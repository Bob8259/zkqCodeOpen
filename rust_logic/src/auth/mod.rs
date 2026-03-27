pub mod last_time;

use crate::security::anti_debug::G_SECURITY_POISON_FLAG;
use crate::security::anti_debug::G_SECURITY_POISON_FLAG_2;
use std::sync::atomic::{AtomicI32, Ordering};

// Tracks the total number of findMultiColors / findMultiColorsRaw invocations on the native side
pub static FIND_MULTI_COLORS_CALL_COUNT: AtomicI32 = AtomicI32::new(0);

#[inline(always)]
fn random_nonzero_i32() -> i32 {
    let nanos = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap_or_default()
        .subsec_nanos() as i32;
    if nanos == 0 { 1 } else { nanos }
}

/// Validates the increment value and atomically adds it to the call counter.
/// Triggers multiple poison flags if `increment` is not one of the allowed values (1 or 10).
#[inline(always)]
pub fn validate_and_increment(increment: i32) {
    if increment != 1 && increment != 10 {
        if G_SECURITY_POISON_FLAG.load(Ordering::SeqCst) == 0 {
            G_SECURITY_POISON_FLAG.store(random_nonzero_i32(), Ordering::SeqCst);
        }
        if G_SECURITY_POISON_FLAG_2.load(Ordering::SeqCst) == 0 {
            G_SECURITY_POISON_FLAG_2.store(random_nonzero_i32(), Ordering::SeqCst);
        }
    }
    FIND_MULTI_COLORS_CALL_COUNT.fetch_add(increment, Ordering::Relaxed);
}
