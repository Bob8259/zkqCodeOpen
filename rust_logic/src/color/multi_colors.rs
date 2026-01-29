use crate::color::find_multi_colors_internal;
use jni::objects::{JIntArray, JObject};
use jni::sys::{jint, jintArray, jobject};
use jni::JNIEnv;

// Android Bitmap FFI
#[repr(C)]
#[derive(Debug, Default)]
struct AndroidBitmapInfo {
    width: u32,
    height: u32,
    stride: u32,
    format: i32,
    flags: u32,
}

const ANDROID_BITMAP_FORMAT_RGBA_8888: i32 = 1;

#[link(name = "jnigraphics")]
extern "C" {
    fn AndroidBitmap_getInfo(
        env: *mut jni::sys::JNIEnv,
        bitmap: jobject,
        info: *mut AndroidBitmapInfo,
    ) -> i32;
    fn AndroidBitmap_lockPixels(
        env: *mut jni::sys::JNIEnv,
        bitmap: jobject,
        pixels: *mut *mut std::ffi::c_void,
    ) -> i32;
    fn AndroidBitmap_unlockPixels(env: *mut jni::sys::JNIEnv, bitmap: jobject) -> i32;
}

#[no_mangle]
pub unsafe extern "system" fn find_multi_colors(
    env: JNIEnv,
    _class: JObject,
    bitmap: JObject,
    x1: jint,
    y1: jint,
    x2: jint,
    y2: jint,
    main_color: jint,
    threshold: jint,
    flat_offsets: JIntArray,
) -> jintArray {
    let mut info = AndroidBitmapInfo::default();
    if AndroidBitmap_getInfo(env.get_native_interface(), bitmap.as_raw(), &mut info) < 0 {
        return std::ptr::null_mut();
    }

    if info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 {
        return std::ptr::null_mut();
    }

    let mut pixels = std::ptr::null_mut();
    if AndroidBitmap_lockPixels(env.get_native_interface(), bitmap.as_raw(), &mut pixels) < 0 {
        return std::ptr::null_mut();
    }

    let offsets_len = env.get_array_length(&flat_offsets).unwrap_or(0) as usize;
    let mut offsets_vec = vec![0i32; offsets_len];
    if env
        .get_int_array_region(&flat_offsets, 0, &mut offsets_vec)
        .is_err()
    {
        AndroidBitmap_unlockPixels(env.get_native_interface(), bitmap.as_raw());
        return std::ptr::null_mut();
    }

    let stride = info.stride as usize;
    let pixels_ptr = pixels as *const u8;

    let get_pixel = |x: i32, y: i32| {
        let offset = (y as usize * stride) + (x as usize * 4);
        let p = pixels_ptr.add(offset) as *const u32;
        *p
    };

    let result = find_multi_colors_internal(
        info.width as i32,
        info.height as i32,
        x1,
        y1,
        x2,
        y2,
        main_color as u32,
        threshold,
        &offsets_vec,
        get_pixel,
    );

    AndroidBitmap_unlockPixels(env.get_native_interface(), bitmap.as_raw());

    if let Some((fx, fy)) = result {
        let res_arr = env.new_int_array(2).unwrap();
        let buf = [fx, fy];
        env.set_int_array_region(&res_arr, 0, &buf).unwrap();
        res_arr.as_raw()
    } else {
        std::ptr::null_mut()
    }
}
