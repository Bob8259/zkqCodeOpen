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

struct BitmapLock<'a> {
    env: &'a JNIEnv<'a>,
    bitmap: JObject<'a>,
    pixels: *mut std::ffi::c_void,
}

impl<'a> BitmapLock<'a> {
    fn lock(env: &'a JNIEnv<'a>, bitmap: JObject<'a>) -> Option<Self> {
        let mut pixels = std::ptr::null_mut();
        unsafe {
            if AndroidBitmap_lockPixels(env.get_native_interface(), bitmap.as_raw(), &mut pixels)
                < 0
            {
                return None;
            }
        }
        Some(Self {
            env,
            bitmap,
            pixels,
        })
    }
}

impl Drop for BitmapLock<'_> {
    fn drop(&mut self) {
        unsafe {
            AndroidBitmap_unlockPixels(self.env.get_native_interface(), self.bitmap.as_raw());
        }
    }
}

#[no_mangle]
pub extern "system" fn find_multi_colors(
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
    direction: jint,
) -> jintArray {
    let mut info = AndroidBitmapInfo::default();
    unsafe {
        if AndroidBitmap_getInfo(env.get_native_interface(), bitmap.as_raw(), &mut info) < 0 {
            return std::ptr::null_mut();
        }
    }

    if info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 {
        return std::ptr::null_mut();
    }

    let lock = match BitmapLock::lock(&env, bitmap) {
        Some(l) => l,
        None => return std::ptr::null_mut(),
    };

    let offsets_len = env.get_array_length(&flat_offsets).unwrap_or(0) as usize;
    let mut offsets_vec = vec![0i32; offsets_len];
    if env
        .get_int_array_region(&flat_offsets, 0, &mut offsets_vec)
        .is_err()
    {
        return std::ptr::null_mut();
    }

    let stride = info.stride as usize;
    let pixels_ptr = lock.pixels as *const u8;

    let get_pixel = |x: i32, y: i32| {
        let offset = (y as usize * stride) + (x as usize * 4);
        unsafe {
            let p = pixels_ptr.add(offset) as *const u32;
            *p
        }
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
