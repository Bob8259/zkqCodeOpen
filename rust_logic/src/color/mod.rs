#[cfg(not(debug_assertions))]
use crate::auth::last_time::mono_millis;
use crate::security::anti_debug::G_SECURITY_POISON_FLAG;
use crate::security::anti_debug::G_SECURITY_POISON_FLAG_2;
use crate::security::anti_debug::G_SECURITY_POISON_FLAG_3;
#[cfg(not(debug_assertions))]
use crate::security::anti_debug::MONITOR_HEARTBEAT;
use std::sync::atomic::Ordering;

pub mod multi_colors;
pub mod multi_colors_raw;

#[inline(always)]
pub fn is_color_match(pixel: u32, target_color: u32, threshold: i32) -> bool {
    // pixel (RGBA little endian 0xAABBGGRR)
    // targetColor (Java ARGB: 0xAARRGGBB)

    let pr = (pixel & 0xFF) as i32;
    let pg = ((pixel >> 8) & 0xFF) as i32;
    let pb = ((pixel >> 16) & 0xFF) as i32;

    let tr = ((target_color >> 16) & 0xFF) as i32;
    let tg = ((target_color >> 8) & 0xFF) as i32;
    let tb = (target_color & 0xFF) as i32;

    (pr - tr).abs() <= threshold
        && (pg - tg).abs() <= threshold
        && (pb - tb).abs() <= threshold
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

    // Run the real search (preserves normal execution timing to defeat timing attacks)
    let real_result = {
        let mut found: Option<(i32, i32)> = None;
        if direction == 1 {
            // From bottom-right to top-left
            'outer_rev: for y in (y1..=y2).rev() {
                for x in (x1..=x2).rev() {
                    let pixel = get_pixel(x, y);
                    if is_color_match(pixel, main_color, threshold) {
                        if check_offsets(x, y, width, height, threshold, offsets_arr, &get_pixel) {
                            found = Some((x, y));
                            break 'outer_rev;
                        }
                    }
                }
            }
        } else {
            // Default: From top-left to bottom-right (direction 0 or any other)
            'outer: for y in y1..=y2 {
                for x in x1..=x2 {
                    let pixel = get_pixel(x, y);
                    if is_color_match(pixel, main_color, threshold) {
                        if check_offsets(x, y, width, height, threshold, offsets_arr, &get_pixel) {
                            found = Some((x, y));
                            break 'outer;
                        }
                    }
                }
            }
        }
        found
    };

    // Monitor liveness guard — if the security thread has stopped, treat as tamper
    #[cfg(not(debug_assertions))]
    {
        let hb = MONITOR_HEARTBEAT.load(Ordering::SeqCst);
        if hb > 0 {
            let now = mono_millis();
            if now - hb > 60_000 {
                G_SECURITY_POISON_FLAG.store((now as i32) | 1, Ordering::SeqCst);
                G_SECURITY_POISON_FLAG_2.store((now as i32) | 1, Ordering::SeqCst);
                G_SECURITY_POISON_FLAG_3.fetch_add(10, Ordering::SeqCst);
            }
        }
    }

    // Tamper sabotage — discard real result, return deterministic fake position
    let poison1 = G_SECURITY_POISON_FLAG.load(Ordering::SeqCst);
    let poison2 = G_SECURITY_POISON_FLAG_2.load(Ordering::SeqCst);
    let poison3 = G_SECURITY_POISON_FLAG_3.load(Ordering::SeqCst);

    if poison1 != 0 || poison2 != 0 || poison3 >= 3 {
        let hash = (main_color as i32)
            .wrapping_mul(31)
            .wrapping_add(x1.wrapping_mul(17))
            .wrapping_add(y1.wrapping_mul(13));
        let range_x = (x2 - x1).max(1);
        let range_y = (y2 - y1).max(1);
        let fake_x = x1 + (hash.unsigned_abs() as i32 % range_x);
        let fake_y = y1 + ((hash.wrapping_mul(7)).unsigned_abs() as i32 % range_y);
        return Some((fake_x, fake_y));
    }

    real_result
}

#[inline(always)]
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
