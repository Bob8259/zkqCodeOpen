#[cfg(all(unix, not(debug_assertions)))]
use rustix::fs::{openat, Mode, OFlags, CWD};
#[cfg(not(debug_assertions))]
use std::env;
#[cfg(not(debug_assertions))]
use std::net::TcpStream;
#[cfg(not(debug_assertions))]
use std::panic;
use std::sync::atomic::AtomicI32;
#[cfg(not(debug_assertions))]
use std::sync::atomic::Ordering;
#[cfg(not(debug_assertions))]
use std::thread;
#[cfg(not(debug_assertions))]
use std::time::Duration;

pub static G_SECURITY_POISON_FLAG: AtomicI32 = AtomicI32::new(0);

#[cfg(not(debug_assertions))]
fn trigger_poison(reason: &str) {
    if G_SECURITY_POISON_FLAG.load(Ordering::SeqCst) == 0 {
        G_SECURITY_POISON_FLAG.store(42, Ordering::SeqCst);
        log::error!("Security monitor: Violation detected! Reason: {}", reason);
    }
}

#[cfg(all(unix, not(debug_assertions)))]
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

#[cfg(not(debug_assertions))]
fn check_debugger_present() {
    #[cfg(unix)]
    {
        const XOR_KEY: u8 = 0x42;

        fn xor_decrypt(encrypted: &[u8]) -> String {
            encrypted.iter().map(|&b| (b ^ XOR_KEY) as char).collect()
        }

        const ENCRYPTED_PATH: [u8; 17] = [
            0x2f ^ XOR_KEY,
            0x70 ^ XOR_KEY,
            0x72 ^ XOR_KEY,
            0x6f ^ XOR_KEY,
            0x63 ^ XOR_KEY,
            0x2f ^ XOR_KEY,
            0x73 ^ XOR_KEY,
            0x65 ^ XOR_KEY,
            0x6c ^ XOR_KEY,
            0x66 ^ XOR_KEY,
            0x2f ^ XOR_KEY,
            0x73 ^ XOR_KEY,
            0x74 ^ XOR_KEY,
            0x61 ^ XOR_KEY,
            0x74 ^ XOR_KEY,
            0x75 ^ XOR_KEY,
            0x73 ^ XOR_KEY,
        ];
        const ENCRYPTED_TRACER: [u8; 10] = [
            0x54 ^ XOR_KEY,
            0x72 ^ XOR_KEY,
            0x61 ^ XOR_KEY,
            0x63 ^ XOR_KEY,
            0x65 ^ XOR_KEY,
            0x72 ^ XOR_KEY,
            0x50 ^ XOR_KEY,
            0x69 ^ XOR_KEY,
            0x64 ^ XOR_KEY,
            0x3a ^ XOR_KEY,
        ];

        let path = xor_decrypt(&ENCRYPTED_PATH);
        let tracer = xor_decrypt(&ENCRYPTED_TRACER);

        if let Some(content) = read_to_string_rustix(&path) {
            for line in content.lines() {
                if line.starts_with(&tracer) {
                    let pid_str = line.replace(&tracer, "").trim().to_string();
                    if let Ok(pid) = pid_str.parse::<i32>() {
                        if pid != 0 {
                            trigger_poison("tracer pid detected");
                            return;
                        }
                    }
                }
            }
        }
    }
}

#[cfg(not(debug_assertions))]
fn check_env_injection() {
    if env::var("LD_PRELOAD").is_ok() {
        trigger_poison("LD_PRELOAD detected");
    }
}

#[cfg(not(debug_assertions))]
fn check_time_drift() {
    let start = std::time::Instant::now();
    let mut _x = 0;
    for i in 0..100 {
        _x += i;
    }

    if start.elapsed().as_micros() > 500 {
        trigger_poison("time drift detected");
    }
}

#[cfg(not(debug_assertions))]
fn check_memory_maps() {
    #[cfg(unix)]
    {
        if let Some(maps) = read_to_string_rustix("/proc/self/maps") {
            for line in maps.lines() {
                if line.contains("frida") || line.contains("gadget") || line.contains("gum-js") {
                    trigger_poison("unauthorized memory segment detected");
                    return;
                }
                if line.contains("/data/local/tmp") {
                    trigger_poison("unauthorized memory segment detected");
                    return;
                }
                if line.contains("rwxp") && line.contains("[anon]") {
                    trigger_poison("unauthorized memory segment detected");
                    return;
                }
            }
        }
    }
}

#[cfg(not(debug_assertions))]
fn check_frida_port() {
    if let Ok(addr) = "127.0.0.1:27042".parse() {
        if TcpStream::connect_timeout(&addr, Duration::from_millis(200)).is_ok() {
            trigger_poison("frida port detected");
        }
    }
}

#[cfg(not(debug_assertions))]
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
                    if fname.contains("lsposed")
                        || fname.contains("xposed")
                        || fname.contains("frida")
                    {
                        HOOK_DETECTED.store(true, Ordering::SeqCst);
                        G_SECURITY_POISON_FLAG.store(42, Ordering::SeqCst);
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
            trigger_poison("native hooks detected");
        }
    }
}

pub fn start_security_monitor() {
    #[cfg(not(debug_assertions))]
    thread::spawn(|| {
        let result = panic::catch_unwind(|| {
            let mut heavy_check_counter = 0;

            loop {
                check_debugger_present();
                check_env_injection();
                check_time_drift();

                if heavy_check_counter >= 12 {
                    check_memory_maps();
                    check_frida_port();
                    check_native_hooks();
                    heavy_check_counter = 0;
                }

                heavy_check_counter += 1;
                thread::sleep(Duration::from_secs(5));
            }
        });

        if let Err(_e) = result {}
    });

    #[cfg(debug_assertions)]
    {
        log::info!("Security monitor disabled in debug build");
    }
}
