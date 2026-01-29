use jni::sys::{jint, JNI_VERSION_1_6};
use jni::{JavaVM, NativeMethod};
use std::ffi::c_void;

mod bridge;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _reserved: *mut c_void) -> jint {
    let mut env = vm.get_env().expect("Cannot get JNIEnv");

    // 找到你的 Kotlin 类
    let class_name = "com/coc/zkqcode/nativehelper/RustTools";
    let class = env.find_class(class_name).expect("找不到类");

    // 定义方法映射
    let methods = [
        NativeMethod {
            name: "sayHello".into(),                                 // Kotlin 中的方法名
            sig: "(Ljava/lang/String;)Ljava/lang/String;".into(),    // JNI 签名
            fn_ptr: bridge::rust_say_hello as *mut c_void,             // Rust 中的函数指针
        },
    ];

    // 执行注册
    env.register_native_methods(class, &methods).expect("注册失败");

    JNI_VERSION_1_6
}