#[cfg(unix)]
use rustix::fs::{openat, Mode, OFlags, CWD};
use std::env;
use std::net::TcpStream;
use std::panic;
use std::sync::atomic::AtomicI32;
use std::sync::atomic::AtomicI64;
use std::sync::atomic::Ordering;
use std::thread;
use std::time::Duration;

use crate::auth::last_time::mono_millis;

pub static G_SECURITY_POISON_FLAG: AtomicI32 = AtomicI32::new(0);
// Secondary poison flag — set by a different subset of detections
pub static G_SECURITY_POISON_FLAG_2: AtomicI32 = AtomicI32::new(0);
// Tertiary poison flag — accumulates violation counts, triggers after threshold
pub static G_SECURITY_POISON_FLAG_3: AtomicI32 = AtomicI32::new(0);

// Monotonic heartbeat updated every iteration of the security monitor thread.
// External code can compare this against the current time to detect thread death.
pub static MONITOR_HEARTBEAT: AtomicI64 = AtomicI64::new(0);

// Compile-time XOR encoder — plaintext is evaluated at compile time only,
// the original bytes never appear in the output binary
#[allow(dead_code)]
const fn xor_bytes<const N: usize>(input: [u8; N], key: u8) -> [u8; N] {
    let mut out = [0u8; N];
    let mut i = 0;
    while i < N {
        out[i] = input[i] ^ key;
        i += 1;
    }
    out
}

// Runtime XOR decoder — force-inlined so no single "decode" callsite exists
#[inline(always)]
fn xor_decode(encrypted: &[u8], key: u8) -> String {
    encrypted.iter().map(|&b| (b ^ key) as char).collect()
}

// Per-category XOR keys to frustrate batch decryption
#[cfg(unix)]
const K_PROC: u8 = 0x42;
#[cfg(unix)]
const K_MAPS: u8 = 0x5A;
const K_ENV: u8 = 0x73;
const K_NET: u8 = 0x37;
#[cfg(unix)]
const K_HOOK: u8 = 0x2E;

// Encrypted constants — proc filesystem paths
#[cfg(unix)]
const ENC_PROC_STATUS: [u8; 17] = xor_bytes(*b"/proc/self/status", K_PROC);
#[cfg(unix)]
const ENC_TRACER_PID: [u8; 10] = xor_bytes(*b"TracerPid:", K_PROC);
#[cfg(unix)]
const ENC_PROC_MAPS: [u8; 15] = xor_bytes(*b"/proc/self/maps", K_MAPS);

// Encrypted constants — memory map analysis keywords
#[cfg(unix)]
const ENC_FRIDA_MAP: [u8; 5] = xor_bytes(*b"frida", K_MAPS);
#[cfg(unix)]
const ENC_GADGET: [u8; 6] = xor_bytes(*b"gadget", K_MAPS);
#[cfg(unix)]
const ENC_GUM_JS: [u8; 6] = xor_bytes(*b"gum-js", K_MAPS);
#[cfg(unix)]
const ENC_DATA_LOCAL_TMP: [u8; 15] = xor_bytes(*b"/data/local/tmp", K_MAPS);
#[cfg(unix)]
const ENC_RWXP: [u8; 4] = xor_bytes(*b"rwxp", K_MAPS);
#[cfg(unix)]
const ENC_ANON: [u8; 6] = xor_bytes(*b"[anon]", K_MAPS);

// Encrypted constants — environment variable name
const ENC_LD_PRELOAD: [u8; 10] = xor_bytes(*b"LD_PRELOAD", K_ENV);

// Encrypted constants — network address
const ENC_FRIDA_ADDR: [u8; 15] = xor_bytes(*b"127.0.0.1:27042", K_NET);

// Encrypted constants — hook framework names
#[cfg(unix)]
const ENC_LSPOSED: [u8; 7] = xor_bytes(*b"lsposed", K_HOOK);
#[cfg(unix)]
const ENC_XPOSED: [u8; 6] = xor_bytes(*b"xposed", K_HOOK);
#[cfg(unix)]
const ENC_FRIDA_HOOK: [u8; 5] = xor_bytes(*b"frida", K_HOOK);

// Silent poison triggers — no log output in release to avoid leaking detection info.
// Split across multiple flags so no single NOP can disable all protection.
#[inline(always)]
fn random_nonzero_i32() -> i32 {
    let nanos = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap_or_default()
        .subsec_nanos() as i32;
    if nanos == 0 {
        1
    } else {
        nanos
    }
}

#[inline(always)]
fn trigger_poison() {
    if G_SECURITY_POISON_FLAG.load(Ordering::SeqCst) == 0 {
        G_SECURITY_POISON_FLAG.store(random_nonzero_i32(), Ordering::SeqCst);
    }
}

#[inline(always)]
fn trigger_poison_secondary() {
    if G_SECURITY_POISON_FLAG_2.load(Ordering::SeqCst) == 0 {
        G_SECURITY_POISON_FLAG_2.store(random_nonzero_i32(), Ordering::SeqCst);
    }
}

#[inline(always)]
fn trigger_poison_accumulate() {
    G_SECURITY_POISON_FLAG_3.fetch_add(1, Ordering::SeqCst);
}

// Marked cold — only used in security check paths, not normal operation
#[cfg(unix)]
#[cold]
fn read_to_string_rustix(path: &str) -> Option<String> {
    let fd = openat(CWD, path, OFlags::RDONLY, Mode::empty()).ok()?;
    let mut buf = Vec::new();
    let mut tmp = [0u8; 4096];
    loop {
        match rustix::io::read(&fd, &mut tmp) {
            Ok(0) => break,
            Ok(n) => buf.extend_from_slice(&tmp[..n]),
            Err(_) => return None,
        }
    }
    String::from_utf8(buf).ok()
}

#[inline(always)]
fn check_debugger_present() {
    #[cfg(unix)]
    {
        let path = xor_decode(&ENC_PROC_STATUS, K_PROC);
        let tracer = xor_decode(&ENC_TRACER_PID, K_PROC);

        if let Some(content) = read_to_string_rustix(&path) {
            for line in content.lines() {
                if line.starts_with(&tracer) {
                    let pid_str = line.replace(&tracer, "").trim().to_string();
                    if let Ok(pid) = pid_str.parse::<i32>() {
                        if pid != 0 {
                            trigger_poison();
                            trigger_poison_accumulate();
                            return;
                        }
                    }
                }
            }
        }
    }
}

#[inline(always)]
fn check_env_injection() {
    let var_name = xor_decode(&ENC_LD_PRELOAD, K_ENV);
    if env::var(&var_name).is_ok() {
        trigger_poison_secondary();
        trigger_poison_accumulate();
    }
}

#[inline(always)]
fn check_time_drift() {
    let start = std::time::Instant::now();
    let mut _x = 0;
    for i in 0..100 {
        _x += i;
    }

    if start.elapsed().as_micros() > 500 {
        trigger_poison_accumulate();
    }
}

#[inline(always)]
fn check_memory_maps() {
    #[cfg(unix)]
    {
        let maps_path = xor_decode(&ENC_PROC_MAPS, K_MAPS);
        let s_frida = xor_decode(&ENC_FRIDA_MAP, K_MAPS);
        let s_gadget = xor_decode(&ENC_GADGET, K_MAPS);
        let s_gum_js = xor_decode(&ENC_GUM_JS, K_MAPS);
        let s_data_tmp = xor_decode(&ENC_DATA_LOCAL_TMP, K_MAPS);
        let s_rwxp = xor_decode(&ENC_RWXP, K_MAPS);
        let s_anon = xor_decode(&ENC_ANON, K_MAPS);

        if let Some(maps) = read_to_string_rustix(&maps_path) {
            for line in maps.lines() {
                if line.contains(&*s_frida)
                    || line.contains(&*s_gadget)
                    || line.contains(&*s_gum_js)
                {
                    trigger_poison_secondary();
                    trigger_poison_accumulate();
                    return;
                }
                if line.contains(&*s_data_tmp) {
                    trigger_poison_secondary();
                    return;
                }
                if line.contains(&*s_rwxp) && line.contains(&*s_anon) {
                    trigger_poison_secondary();
                    trigger_poison_accumulate();
                    return;
                }
            }
        }
    }
}

#[inline(always)]
fn check_frida_port() {
    let addr_str = xor_decode(&ENC_FRIDA_ADDR, K_NET);
    if let Ok(addr) = addr_str.parse() {
        if TcpStream::connect_timeout(&addr, Duration::from_millis(200)).is_ok() {
            trigger_poison();
            trigger_poison_secondary();
        }
    }
}

#[inline(always)]
fn check_native_hooks() {
    #[cfg(target_os = "android")]
    {
        use libc::{c_char, c_void, dladdr, Dl_info};
        use std::ffi::CStr;
        use std::sync::atomic::AtomicBool;

        static HOOK_DETECTED: AtomicBool = AtomicBool::new(false);

        #[repr(C)]
        #[allow(non_camel_case_types)]
        enum _Unwind_Reason_Code {
            _URC_NO_REASON = 0,
            _URC_FOREIGN_EXCEPTION_CAUGHT = 1,
            _URC_FATAL_PHASE2_ERROR = 2,
            _URC_FATAL_PHASE1_ERROR = 3,
            _URC_NORMAL_STOP = 4,
            _URC_END_OF_STACK = 5,
            _URC_HANDLER_FOUND = 6,
            _URC_INSTALL_CONTEXT = 7,
            _URC_CONTINUE_UNWIND = 8,
        }

        #[allow(non_camel_case_types)]
        enum _Unwind_Context {}

        #[allow(non_camel_case_types)]
        type _Unwind_Trace_Fn = unsafe extern "C" fn(
            context: *mut _Unwind_Context,
            arg: *mut c_void,
        ) -> _Unwind_Reason_Code;

        #[allow(non_snake_case)]
        extern "C" {
            fn _Unwind_Backtrace(
                trace: _Unwind_Trace_Fn,
                trace_argument: *mut c_void,
            ) -> _Unwind_Reason_Code;
            fn _Unwind_GetIP(context: *mut _Unwind_Context) -> libc::uintptr_t;
        }

        unsafe extern "C" fn unwind_callback(
            context: *mut _Unwind_Context,
            _arg: *mut c_void,
        ) -> _Unwind_Reason_Code {
            let pc = _Unwind_GetIP(context);
            if pc != 0 {
                let mut info: Dl_info = std::mem::zeroed();
                if dladdr(pc as *const c_void, &mut info) != 0 && !info.dli_fname.is_null() {
                    let fname = CStr::from_ptr(info.dli_fname as *const c_char).to_string_lossy();
                    // Decrypt hook framework names inside the callback
                    let s1 = xor_decode(&ENC_LSPOSED, K_HOOK);
                    let s2 = xor_decode(&ENC_XPOSED, K_HOOK);
                    let s3 = xor_decode(&ENC_FRIDA_HOOK, K_HOOK);
                    if fname.contains(&*s1) || fname.contains(&*s2) || fname.contains(&*s3) {
                        HOOK_DETECTED.store(true, Ordering::SeqCst);
                        // Set all poison flags from inside the callback directly
                        G_SECURITY_POISON_FLAG.store(random_nonzero_i32(), Ordering::SeqCst);
                        G_SECURITY_POISON_FLAG_2.store(random_nonzero_i32(), Ordering::SeqCst);
                        G_SECURITY_POISON_FLAG_3.fetch_add(10, Ordering::SeqCst);
                        return _Unwind_Reason_Code::_URC_END_OF_STACK;
                    }
                }
            }
            _Unwind_Reason_Code::_URC_NO_REASON
        }

        unsafe {
            _Unwind_Backtrace(unwind_callback, std::ptr::null_mut());
        }

        if HOOK_DETECTED.load(Ordering::SeqCst) {
            trigger_poison();
            trigger_poison_secondary();
        }
    }
}

// Additional Frida detection: scan extra default ports
#[inline(always)]
fn check_frida_extra_ports() {
    const ENC_ADDR_43: [u8; 15] = xor_bytes(*b"127.0.0.1:27043", K_NET);
    const ENC_ADDR_44: [u8; 15] = xor_bytes(*b"127.0.0.1:27044", K_NET);

    for enc_addr in [&ENC_ADDR_43[..], &ENC_ADDR_44[..]] {
        let addr_str = xor_decode(enc_addr, K_NET);
        if let Ok(addr) = addr_str.parse() {
            if TcpStream::connect_timeout(&addr, Duration::from_millis(100)).is_ok() {
                trigger_poison_secondary();
                trigger_poison_accumulate();
            }
        }
    }
}

// Scan /proc/self/fd for suspicious open file descriptors pointing to injected libs
#[cfg(unix)]
#[inline(always)]
fn check_proc_fd() {
    const K_FD: u8 = 0x4F;
    const ENC_PROC_FD: [u8; 13] = xor_bytes(*b"/proc/self/fd", K_FD);
    const ENC_FRIDA_FD: [u8; 5] = xor_bytes(*b"frida", K_FD);
    const ENC_GADGET_FD: [u8; 6] = xor_bytes(*b"gadget", K_FD);

    let fd_dir = xor_decode(&ENC_PROC_FD, K_FD);
    let s_frida = xor_decode(&ENC_FRIDA_FD, K_FD);
    let s_gadget = xor_decode(&ENC_GADGET_FD, K_FD);

    if let Ok(entries) = std::fs::read_dir(&fd_dir) {
        for entry in entries.flatten() {
            if let Ok(link) = std::fs::read_link(entry.path()) {
                let link_str = link.to_string_lossy();
                if link_str.contains(&*s_frida) || link_str.contains(&*s_gadget) {
                    trigger_poison();
                    trigger_poison_secondary();
                    return;
                }
            }
        }
    }
}

// Verify .so integrity by checking its size on disk via /proc/self/maps
#[cfg(unix)]
#[inline(always)]
fn check_so_integrity() {
    const K_SO: u8 = 0x61;
    const ENC_LIB_NAME: [u8; 16] = xor_bytes(*b"librust_logic.so", K_SO);

    let lib_name = xor_decode(&ENC_LIB_NAME, K_SO);
    let maps_path = xor_decode(&ENC_PROC_MAPS, K_MAPS);

    if let Some(maps_content) = read_to_string_rustix(&maps_path) {
        for line in maps_content.lines() {
            if line.contains(&*lib_name) {
                // Extract the mapped file path from the maps entry
                if let Some(path) = line.rsplit_once(' ').map(|(_, p)| p.trim()) {
                    if !path.is_empty() && path.starts_with('/') {
                        // Verify the on-disk file is readable and not zero-length
                        if let Ok(metadata) = std::fs::metadata(path) {
                            if metadata.len() == 0 {
                                trigger_poison_accumulate();
                            }
                        } else {
                            trigger_poison_accumulate();
                        }
                        return;
                    }
                }
            }
        }
    }
}

// Auth-call-frequency guard: if IS_AUTH_PASS is true, getLastTime must be called
// at least once per 6 hours (21_600_000 ms). Uses monotonic clock to resist time manipulation.
#[inline(always)]
fn check_auth_call_frequency() {
    if !crate::auth::ad_track::is_auth_pass() {
        return;
    }
    let ts = crate::auth::last_time::last_get_call_mono_ts();
    if ts == 0 {
        // First check after auth passed — seed the monotonic timer
        crate::auth::last_time::try_init_get_call_mono_ts();
        return;
    }
    let now = crate::auth::last_time::mono_millis();
    let elapsed = now - ts;
    let threshold = 8 * 3600 * 1000;
    if elapsed > threshold {
        trigger_poison();
        trigger_poison_accumulate();
    }
}

// Simple LCG pseudo-random for sleep jitter (avoids pulling in rand for the monitor thread)
#[inline(always)]
fn lcg_rand(state: &mut u64) -> u64 {
    *state = state
        .wrapping_mul(6364136223846793005)
        .wrapping_add(1442695040888963407);
    *state
}

#[cold]
pub fn start_security_monitor() {
    thread::spawn(|| {
        // Outer restart loop — if the inner catch_unwind ever exits, restart immediately
        loop {
            let _ = panic::catch_unwind(|| {
                let mut heavy_check_counter: u32 = 0;
                // Seed the LCG from the current time for non-deterministic intervals
                let mut rng_state = std::time::SystemTime::now()
                    .duration_since(std::time::UNIX_EPOCH)
                    .unwrap_or_default()
                    .as_nanos() as u64;

                // Set initial heartbeat to prove the monitor has started
                MONITOR_HEARTBEAT.store(mono_millis(), Ordering::SeqCst);

                loop {
                    // Wrap each check individually so a panic in one doesn't skip the rest
                    let _ = panic::catch_unwind(|| check_debugger_present());
                    let _ = panic::catch_unwind(|| check_env_injection());
                    let _ = panic::catch_unwind(|| check_time_drift());

                    // Heavy checks at variable frequency (every 8-16 light iterations)
                    let heavy_threshold = 8 + (lcg_rand(&mut rng_state) % 9) as u32;
                    if heavy_check_counter >= heavy_threshold {
                        let _ = panic::catch_unwind(|| check_memory_maps());
                        let _ = panic::catch_unwind(|| check_frida_port());
                        let _ = panic::catch_unwind(|| check_frida_extra_ports());
                        let _ = panic::catch_unwind(|| check_native_hooks());
                        let _ = panic::catch_unwind(|| check_auth_call_frequency());
                        let _ = panic::catch_unwind(|| {
                            crate::auth::ad_track::check_ad_display_frequency();
                        });
                        #[cfg(unix)]
                        {
                            let _ = panic::catch_unwind(|| check_proc_fd());
                            let _ = panic::catch_unwind(|| check_so_integrity());
                        }
                        heavy_check_counter = 0;
                    }

                    heavy_check_counter += 1;

                    // Update heartbeat — proves this thread is still alive
                    MONITOR_HEARTBEAT.store(mono_millis(), Ordering::SeqCst);

                    // Randomized sleep interval: 3-8 seconds
                    let sleep_ms = 3000 + (lcg_rand(&mut rng_state) % 5001) as u64;
                    thread::sleep(Duration::from_millis(sleep_ms));
                }
            });
            // Outer catch_unwind caught a panic — restart after a short delay
            thread::sleep(Duration::from_millis(1000));
        }
    });
}
