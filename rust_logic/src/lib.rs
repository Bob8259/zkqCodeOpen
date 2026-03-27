use android_logger::Config;
use jni::sys::{jint, JNI_VERSION_1_6};
use jni::{JavaVM, NativeMethod};
use log::LevelFilter;
use std::ffi::c_void;

pub mod auth;
mod bridge;
pub mod color;
mod dexloader;
pub mod security;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _reserved: *mut c_void) -> jint {
    // Initialize logger, set filter level and tag
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Info) // Filter out verbose internal logs
            .with_tag("zkq_rust"), // More unique tag
    );

    log::info!("Rust logging system initialized successfully!");

    // Start security monitoring
    security::anti_debug::start_security_monitor();

    let mut env = vm.get_env().expect("Cannot get JNIEnv");

    // Decrypt JNI class path at runtime — plaintext never appears in binary
    const K_JNI: u8 = 0x3B;
    const fn xor_bytes_jni<const N: usize>(input: [u8; N], key: u8) -> [u8; N] {
        let mut out = [0u8; N];
        let mut i = 0;
        while i < N {
            out[i] = input[i] ^ key;
            i += 1;
        }
        out
    }
    const ENC_CLASS: [u8; 38] = xor_bytes_jni(*b"com/coc/zkqcode/nativehelper/RustTools", K_JNI);
    let class_name: String = ENC_CLASS.iter().map(|&b| (b ^ K_JNI) as char).collect();
    let class = env.find_class(&class_name).expect("Class not found");

    // Define method mappings
    let methods = [
        NativeMethod {
            name: "sayHello".into(),
            sig: "(Ljava/lang/String;)Ljava/lang/String;".into(),
            fn_ptr: bridge::rust_say_hello as *mut c_void,
        },
        NativeMethod {
            name: "decryptJar".into(),
            sig: "([B)[B".into(),
            fn_ptr: security::cypherhelper::decryptJar as *mut c_void,
        },
        NativeMethod {
            name: "generateNonce".into(),
            sig: "()Ljava/lang/String;".into(),
            fn_ptr: security::login::generateNonce as *mut c_void,
        },
        NativeMethod {
            name: "encryptLoginPayload".into(),
            sig: "(Ljava/lang/String;)Ljava/lang/String;".into(),
            fn_ptr: security::login::encryptLoginPayload as *mut c_void,
        },
        NativeMethod {
            name: "decryptLoginResponse".into(),
            sig: "(Ljava/lang/String;)Ljava/lang/String;".into(),
            fn_ptr: security::login::decryptLoginResponse as *mut c_void,
        },
        NativeMethod {
            name: "createInMemoryDex".into(),
            sig: "([B)I".into(),
            fn_ptr: dexloader::dex_loader::create_in_memory_dex as *mut c_void,
        },
        NativeMethod {
            name: "findMultiColors".into(),
            sig: "(Landroid/graphics/Bitmap;IIIIII[III)[I".into(),
            fn_ptr: color::multi_colors::find_multi_colors as *mut c_void,
        },
        NativeMethod {
            name: "findMultiColorsRaw".into(),
            sig: "(Ljava/nio/ByteBuffer;IIIIIIIII[III)[I".into(),
            fn_ptr: color::multi_colors_raw::find_multi_colors_raw as *mut c_void,
        },
    ];

    // Execute registration
    env.register_native_methods(class, &methods)
        .expect("Registration failed");

    JNI_VERSION_1_6
}
