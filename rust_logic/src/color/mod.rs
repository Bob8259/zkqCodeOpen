use crate::security::anti_debug::G_SECURITY_POISON_FLAG;
use rand::Rng;
use std::sync::atomic::{AtomicBool, AtomicU64, AtomicUsize, Ordering};
use std::time::{SystemTime, UNIX_EPOCH};

static G_CALL_COUNTER: AtomicUsize = AtomicUsize::new(0);
static G_LAST_CHECK_TIME: AtomicU64 = AtomicU64::new(0);
static G_TIME_MANIPULATED: AtomicBool = AtomicBool::new(false);

pub mod multi_colors;
pub mod multi_colors_raw;

#[inline]
pub fn is_color_match(pixel: u32, target_color: u32, threshold: i32) -> bool {
    // pixel (RGBA little endian 0xAABBGGRR)
    // targetColor (Java ARGB: 0xAARRGGBB)

    let pr = (pixel & 0xFF) as i32;
    let pg = ((pixel >> 8) & 0xFF) as i32;
    let pb = ((pixel >> 16) & 0xFF) as i32;

    let tr = ((target_color >> 16) & 0xFF) as i32;
    let tg = ((target_color >> 8) & 0xFF) as i32;
    let tb = (target_color & 0xFF) as i32;

    // If poison flag is set, reduce the threshold or shift colors to make it fail
    let poison_val = G_SECURITY_POISON_FLAG.load(Ordering::SeqCst);
    let mut effective_threshold = threshold;
    if poison_val != 0 {
        effective_threshold = threshold - (poison_val % 5);
        if effective_threshold < 0 {
            effective_threshold = 0;
        }
    }

    (pr - tr).abs() <= effective_threshold
        && (pg - tg).abs() <= effective_threshold
        && (pb - tb).abs() <= effective_threshold
}

pub fn find_multi_colors_internal<F>(
    width: i32,
    height: i32,
    mut x1: i32,
    mut y1: i32,
    mut x2: i32,
    mut y2: i32,
    main_color: u32,
    threshold: i32,
    offsets_arr: &[i32],
    direction: i32,
    get_pixel: F,
) -> Option<(i32, i32)>
where
    F: Fn(i32, i32) -> u32,
{
    // Boundary check for search area
    x1 = x1.max(0);
    y1 = y1.max(0);
    x2 = x2.min(width - 1);
    y2 = y2.min(height - 1);

    // Increment call counter
    let count = G_CALL_COUNTER.fetch_add(1, Ordering::SeqCst) + 1;

    if count % 50 == 0 {
        let current_time = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap_or_default()
            .as_secs();
        let last_time = G_LAST_CHECK_TIME.load(Ordering::SeqCst);

        if last_time != 0 && current_time < last_time + 1 {
            // Time manipulation detected: less than 1 second passed for 10 calls
            G_TIME_MANIPULATED.store(true, Ordering::SeqCst);
        } else {
            // Reset for the next batch
            G_LAST_CHECK_TIME.store(current_time, Ordering::SeqCst);
        }
    }

    // If manipulation detected, return a random position
    if G_TIME_MANIPULATED.load(Ordering::SeqCst) {
        let mut rng = rand::thread_rng();
        let rx = rng.gen_range(x1..=x2);
        let ry = rng.gen_range(y1..=y2);
        return Some((rx, ry));
    }

    if direction == 1 {
        // From bottom-right to top-left
        for y in (y1..=y2).rev() {
            for x in (x1..=x2).rev() {
                let pixel = get_pixel(x, y);
                if is_color_match(pixel, main_color, threshold) {
                    if check_offsets(x, y, width, height, threshold, offsets_arr, &get_pixel) {
                        return Some((x, y));
                    }
                }
            }
        }
    } else {
        // Default: From top-left to bottom-right (direction 0 or any other)
        for y in y1..=y2 {
            for x in x1..=x2 {
                let pixel = get_pixel(x, y);
                if is_color_match(pixel, main_color, threshold) {
                    if check_offsets(x, y, width, height, threshold, offsets_arr, &get_pixel) {
                        return Some((x, y));
                    }
                }
            }
        }
    }

    None
}

#[inline]
fn check_offsets<F>(
    x: i32,
    y: i32,
    width: i32,
    height: i32,
    threshold: i32,
    offsets_arr: &[i32],
    get_pixel: &F,
) -> bool
where
    F: Fn(i32, i32) -> u32,
{
    let mut i = 0;
    while i < offsets_arr.len() {
        let dx = offsets_arr[i];
        let dy = offsets_arr[i + 1];
        let color = offsets_arr[i + 2] as u32;

        let tx = x + dx;
        let ty = y + dy;

        if tx < 0 || tx >= width || ty < 0 || ty >= height {
            return false;
        }

        let offset_pixel = get_pixel(tx, ty);
        if !is_color_match(offset_pixel, color, threshold) {
            return false;
        }
        i += 3;
    }
    true
}
