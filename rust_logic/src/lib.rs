use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[no_mangle] // 必须：禁止 Rust 编译器混淆函数名
pub extern "system" fn Java_com_coc_zkqcode_RustBridge_sayHello(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    // 将输入转换为 Rust 字符串
    let input: String = env.get_string(&input).expect("Couldn't get java string!").into();
    
    // 逻辑处理
    let output = format!("Rust 说：你好，{}！", input);

    // 转换回 JNI 字符串返回
    let output = env.new_string(output).expect("Couldn't create java string!");
    output.into_raw()
}