use android_logger::Config;
use jni::sys::{jint, JNI_VERSION_1_6};
use jni::{JavaVM, NativeMethod};
use log::LevelFilter;
use std::ffi::c_void;

mod bridge;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _reserved: *mut c_void) -> jint {
    // 初始化日志，设置过滤级别和 Tag
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Debug) // 允许打印的最低级别
            .with_tag("RustRuntime"), // 设置一个固定的 Tag 方便搜索
    );

    log::info!("Rust 日志系统初始化成功！");

    let mut env = vm.get_env().expect("Cannot get JNIEnv");

    // 找到你的 Kotlin 类
    let class_name = "com/coc/zkqcode/nativehelper/RustTools";
    let class = env.find_class(class_name).expect("找不到类");

    // 定义方法映射
    let methods = [NativeMethod {
        name: "sayHello".into(),                              // Kotlin 中的方法名
        sig: "(Ljava/lang/String;)Ljava/lang/String;".into(), // JNI 签名
        fn_ptr: bridge::rust_say_hello as *mut c_void,        // Rust 中的函数指针
    }];

    // 执行注册
    env.register_native_methods(class, &methods)
        .expect("注册失败");

    JNI_VERSION_1_6
}
