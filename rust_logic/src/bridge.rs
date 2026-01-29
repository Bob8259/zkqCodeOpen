use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

// 不再需要 Java_com_... 这种长名字
pub fn rust_say_hello(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    // 将输入转换为 Rust 字符串
    let input: String = env.get_string(&input).expect("Couldn't get java string!").into();
    
    // 逻辑处理
    let output = format!("动态注册111说：你好，{}！", input);

    // 转换回 JNI 字符串返回
    let output = env.new_string(output).expect("Couldn't create java string!");
    output.into_raw()
}
