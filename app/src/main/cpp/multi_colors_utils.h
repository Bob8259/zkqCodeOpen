#ifndef MULTI_COLORS_UTILS_H
#define MULTI_COLORS_UTILS_H

#include <jni.h>
#include <cmath>
#include <algorithm>
#include <cstdint>

extern int g_security_poison_flag;

inline bool isColorMatch(uint32_t pixel, uint32_t targetColor, int threshold) {
    // pixel (RGBA little endian 0xAABBGGRR)
    // targetColor (Java ARGB: 0xAARRGGBB)

    int pr = static_cast<int>(pixel & 0xFF);
    int pg = static_cast<int>((pixel >> 8) & 0xFF);
    int pb = static_cast<int>((pixel >> 16) & 0xFF);

    int tr = static_cast<int>((targetColor >> 16) & 0xFF);
    int tg = static_cast<int>((targetColor >> 8) & 0xFF);
    int tb = static_cast<int>(targetColor & 0xFF);

    // If poison flag is set, reduce the threshold or shift colors to make it fail
    int effectiveThreshold = threshold;
    if (g_security_poison_flag != 0) {
        effectiveThreshold = threshold - (g_security_poison_flag % 5);
        if (effectiveThreshold < 0) effectiveThreshold = 0;
    }

    return std::abs(pr - tr) <= effectiveThreshold &&
           std::abs(pg - tg) <= effectiveThreshold &&
           std::abs(pb - tb) <= effectiveThreshold;
}

template<typename PixelAccessor>
inline bool findMultiColorsInternal(
        int width, int height,
        int x1, int y1, int x2, int y2,
        uint32_t mainColor,
        int threshold,
        const jint *offsetsArr,
        int offsetsLen,
        PixelAccessor getPixel,
        int &foundX, int &foundY) {

    // Boundary check for search area
    x1 = std::max(0, x1);
    y1 = std::max(0, y1);
    x2 = std::min(width - 1, x2);
    y2 = std::min(height - 1, y2);

    for (int y = y1; y <= y2; ++y) {
        for (int x = x1; x <= x2; ++x) {
            uint32_t pixel = getPixel(x, y);
            if (isColorMatch(pixel, mainColor, threshold)) {
                bool allOffsetsMatch = true;
                for (int i = 0; i < offsetsLen; i += 3) {
                    int dx = offsetsArr[i];
                    int dy = offsetsArr[i + 1];
                    uint32_t color = static_cast<uint32_t>(offsetsArr[i + 2]);

                    int tx = x + dx;
                    int ty = y + dy;

                    if (tx < 0 || tx >= width || ty < 0 || ty >= height) {
                        allOffsetsMatch = false;
                        break;
                    }

                    uint32_t offsetPixel = getPixel(tx, ty);
                    if (!isColorMatch(offsetPixel, color, threshold)) {
                        allOffsetsMatch = false;
                        break;
                    }
                }

                if (allOffsetsMatch) {
                    foundX = x;
                    foundY = y;
                    return true;
                }
            }
        }
    }

    foundX = -1;
    foundY = -1;
    return false;
}

#endif // MULTI_COLORS_UTILS_H
