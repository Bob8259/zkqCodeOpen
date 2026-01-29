use crate::color::find_multi_colors_internal;
use jni::objects::{JByteBuffer, JIntArray, JObject};
use jni::sys::{jint, jintArray};
use jni::JNIEnv;

#[no_mangle]
pub unsafe extern "system" fn find_multi_colors_raw(
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
) -> jintArray {
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
        let p = src_buf.add(offset) as *const u32;
        *p
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
        get_pixel,
    );

    if let Some((fx, fy)) = result {
        let res_arr = env.new_int_array(2).unwrap();
        let buf = [fx, fy];
        env.set_int_array_region(&res_arr, 0, &buf).unwrap();
        res_arr.as_raw()
    } else {
        std::ptr::null_mut()
    }
}
