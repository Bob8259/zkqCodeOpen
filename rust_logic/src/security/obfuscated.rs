use std::sync::Mutex;

use rand::Rng;

// Internal state for the obfuscated value: XOR key + encoded payload
struct InnerI64 {
    key: i64,
    encoded: i64,
    initialized: bool,
}

// Internal state for the obfuscated i32 value
struct InnerI32 {
    key: i32,
    encoded: i32,
    initialized: bool,
}

/// Mutex-guarded i64 that XOR-encodes its value with a random key,
/// re-keying on every access so the memory representation constantly changes.
pub struct ObfuscatedAtomicI64 {
    inner: Mutex<InnerI64>,
}

// SAFETY: Mutex already provides Send+Sync; we just need the compiler to see it
// for static promotion since InnerI64 isn't automatically Sync.
unsafe impl Sync for ObfuscatedAtomicI64 {}

impl ObfuscatedAtomicI64 {
    pub const fn new(initial: i64) -> Self {
        Self {
            inner: Mutex::new(InnerI64 {
                key: 0,
                encoded: initial,
                initialized: false,
            }),
        }
    }

    /// Lazily encode the raw initial value on first access
    #[inline(always)]
    fn ensure_init(inner: &mut InnerI64) {
        if !inner.initialized {
            let key: i64 = rand::thread_rng().gen();
            inner.encoded = inner.encoded ^ key;
            inner.key = key;
            inner.initialized = true;
        }
    }

    /// Decode the stored value and re-key so the in-memory bytes change
    #[inline(always)]
    pub fn load(&self) -> i64 {
        let mut guard = self.inner.lock().unwrap_or_else(|e| e.into_inner());
        Self::ensure_init(&mut guard);
        let value = guard.encoded ^ guard.key;
        let new_key: i64 = rand::thread_rng().gen();
        guard.encoded = value ^ new_key;
        guard.key = new_key;
        value
    }

    /// Encode a new value under a fresh random key
    #[inline(always)]
    pub fn store(&self, val: i64) {
        let mut guard = self.inner.lock().unwrap_or_else(|e| e.into_inner());
        let new_key: i64 = rand::thread_rng().gen();
        guard.encoded = val ^ new_key;
        guard.key = new_key;
        guard.initialized = true;
    }

    /// Compare-and-swap on the *decoded* value; re-keys regardless of outcome
    #[inline(always)]
    pub fn compare_exchange(&self, current: i64, new: i64) -> Result<i64, i64> {
        let mut guard = self.inner.lock().unwrap_or_else(|e| e.into_inner());
        Self::ensure_init(&mut guard);
        let value = guard.encoded ^ guard.key;
        let new_key: i64 = rand::thread_rng().gen();
        if value == current {
            guard.encoded = new ^ new_key;
            guard.key = new_key;
            Ok(current)
        } else {
            guard.encoded = value ^ new_key;
            guard.key = new_key;
            Err(value)
        }
    }
}

/// Mutex-guarded i32 that XOR-encodes its value with a random key,
/// re-keying on every access so the memory representation constantly changes.
pub struct ObfuscatedAtomicI32 {
    inner: Mutex<InnerI32>,
}

unsafe impl Sync for ObfuscatedAtomicI32 {}

impl ObfuscatedAtomicI32 {
    pub const fn new(initial: i32) -> Self {
        Self {
            inner: Mutex::new(InnerI32 {
                key: 0,
                encoded: initial,
                initialized: false,
            }),
        }
    }

    #[inline(always)]
    fn ensure_init(inner: &mut InnerI32) {
        if !inner.initialized {
            let key: i32 = rand::thread_rng().gen();
            inner.encoded = inner.encoded ^ key;
            inner.key = key;
            inner.initialized = true;
        }
    }

    #[inline(always)]
    pub fn load(&self) -> i32 {
        let mut guard = self.inner.lock().unwrap_or_else(|e| e.into_inner());
        Self::ensure_init(&mut guard);
        let value = guard.encoded ^ guard.key;
        let new_key: i32 = rand::thread_rng().gen();
        guard.encoded = value ^ new_key;
        guard.key = new_key;
        value
    }

    #[inline(always)]
    pub fn store(&self, val: i32) {
        let mut guard = self.inner.lock().unwrap_or_else(|e| e.into_inner());
        let new_key: i32 = rand::thread_rng().gen();
        guard.encoded = val ^ new_key;
        guard.key = new_key;
        guard.initialized = true;
    }
}
