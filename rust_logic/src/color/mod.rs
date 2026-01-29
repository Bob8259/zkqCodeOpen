use crate::security::anti_debug::G_SECURITY_POISON_FLAG;
use std::sync::atomic::Ordering;

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

    for y in y1..=y2 {
        for x in x1..=x2 {
            let pixel = get_pixel(x, y);
            if is_color_match(pixel, main_color, threshold) {
                let mut all_offsets_match = true;
                let mut i = 0;
                while i < offsets_arr.len() {
                    let dx = offsets_arr[i];
                    let dy = offsets_arr[i + 1];
                    let color = offsets_arr[i + 2] as u32;

                    let tx = x + dx;
                    let ty = y + dy;

                    if tx < 0 || tx >= width || ty < 0 || ty >= height {
                        all_offsets_match = false;
                        break;
                    }

                    let offset_pixel = get_pixel(tx, ty);
                    if !is_color_match(offset_pixel, color, threshold) {
                        all_offsets_match = false;
                        break;
                    }
                    i += 3;
                }

                if all_offsets_match {
                    return Some((x, y));
                }
            }
        }
    }

    None
}
