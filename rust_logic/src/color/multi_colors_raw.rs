use crate::auth;
use crate::color::find_multi_colors_internal;
use jni::objects::{JByteBuffer, JIntArray, JObject};
use jni::sys::{jint, jintArray};
use jni::JNIEnv;

// Symbol name stripped — registered via RegisterNatives in JNI_OnLoad
pub extern "system" fn find_multi_colors_raw(
    env: JNIEnv,
    _class: JObject,
    byte_buffer: JByteBuffer,
    width: jint,
    height: jint,
    stride: jint,
    x1: jint,
    y1: jint,
    x2: jint,
    y2: jint,
    main_color: jint,
    threshold: jint,
    flat_offsets: JIntArray,
    direction: jint,
    increment: jint,
) -> jintArray {
    // Validate increment and update the native-side call counter
    auth::validate_and_increment(increment);

    let src_buf = env.get_direct_buffer_address(&byte_buffer);
    if src_buf.is_err() {
        return std::ptr::null_mut();
    }
    let src_buf = src_buf.unwrap();

    let offsets_len = env.get_array_length(&flat_offsets).unwrap_or(0) as usize;
    let mut offsets_vec = vec![0i32; offsets_len];
    if env
        .get_int_array_region(&flat_offsets, 0, &mut offsets_vec)
        .is_err()
    {
        return std::ptr::null_mut();
    }

    let stride_usize = stride as usize;

    let get_pixel = |x: i32, y: i32| {
        let offset = (y as usize * stride_usize) + (x as usize * 4);
        unsafe {
            let p = src_buf.add(offset) as *const u32;
            *p
        }
    };

    let result = find_multi_colors_internal(
        width,
        height,
        x1,
        y1,
        x2,
        y2,
        main_color as u32,
        threshold,
        &offsets_vec,
        direction,
        get_pixel,
    );

    if let Some((fx, fy)) = result {
        if let Ok(res_arr) = env.new_int_array(2) {
            let buf = [fx, fy];
            if env.set_int_array_region(&res_arr, 0, &buf).is_ok() {
                return res_arr.as_raw();
            }
        }
    }
    std::ptr::null_mut()
}
