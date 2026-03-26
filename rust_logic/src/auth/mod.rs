use crate::security::anti_debug::G_SECURITY_POISON_FLAG;
use std::sync::atomic::{AtomicI32, Ordering};

// Tracks the total number of findMultiColors / findMultiColorsRaw invocations on the native side
pub static FIND_MULTI_COLORS_CALL_COUNT: AtomicI32 = AtomicI32::new(0);

/// Validates the increment value and atomically adds it to the call counter.
/// Triggers the security poison if `increment` is not one of the allowed values (1 or 10).
pub fn validate_and_increment(increment: i32) {
    if increment != 1 && increment != 10 {
        // Invalid increment — trigger poison flag
        if G_SECURITY_POISON_FLAG.load(Ordering::SeqCst) == 0 {
            G_SECURITY_POISON_FLAG.store(42, Ordering::SeqCst);
            log::error!("Auth: invalid increment value {}", increment);
        }
    }
    FIND_MULTI_COLORS_CALL_COUNT.fetch_add(increment, Ordering::Relaxed);
}
