#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <vector>
#include <cmath>

inline bool isColorMatch(uint32_t pixel, uint32_t targetColor, int threshold) {
    // uint32_t pixel (RGBA in memory, little endian as 0xAABBGGRR)
    // targetColor (Java ARGB: 0xAARRGGBB)
    
    // Extract channels from pixel (0xAABBGGRR)
    int pr = pixel & 0xFF;
    int pg = (pixel >> 8) & 0xFF;
    int pb = (pixel >> 16) & 0xFF;

    // Extract channels from targetColor (0xAARRGGBB)
    int tr = (targetColor >> 16) & 0xFF;
    int tg = (targetColor >> 8) & 0xFF;
    int tb = targetColor & 0xFF;

    return std::abs(pr - tr) <= threshold &&
           std::abs(pg - tg) <= threshold &&
           std::abs(pb - tb) <= threshold;
}

extern "C"
jintArray findMultiColors(
        JNIEnv *env, jobject thiz,
        jobject bitmap,
        jint x1, jint y1, jint x2, jint y2,
        jint mainColor,
        jint threshold,
        jintArray flatOffsets) {

    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return nullptr;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return nullptr;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        return nullptr;
    }

    jint *offsetsArr = env->GetIntArrayElements(flatOffsets, nullptr);
    jsize offsetsLen = env->GetArrayLength(flatOffsets);

    int foundX = -1;
    int foundY = -1;

    uint32_t *data = (uint32_t *) pixels;
    int width = info.width;
    int height = info.height;

    // Boundary check for search area
    x1 = std::max(0, x1);
    y1 = std::max(0, y1);
    x2 = std::min((int)width - 1, x2);
    y2 = std::min((int)height - 1, y2);

    for (int y = y1; y <= y2; ++y) {
        uint32_t *row = data + y * width;
        for (int x = x1; x <= x2; ++x) {
            uint32_t pixel = row[x];
            if (isColorMatch(pixel, (uint32_t)mainColor, threshold)) {
                bool allOffsetsMatch = true;
                for (int i = 0; i < offsetsLen; i += 3) {
                    int dx = offsetsArr[i];
                    int dy = offsetsArr[i + 1];
                    uint32_t color = (uint32_t)offsetsArr[i + 2];

                    int tx = x + dx;
                    int ty = y + dy;

                    if (tx < 0 || tx >= width || ty < 0 || ty >= height) {
                        allOffsetsMatch = false;
                        break;
                    }

                    uint32_t offsetPixel = data[ty * width + tx];
                    if (!isColorMatch(offsetPixel, color, threshold)) {
                        allOffsetsMatch = false;
                        break;
                    }
                }

                if (allOffsetsMatch) {
                    foundX = x;
                    foundY = y;
                    goto end;
                }
            }
        }
    }

end:
    env->ReleaseIntArrayElements(flatOffsets, offsetsArr, JNI_ABORT);
    AndroidBitmap_unlockPixels(env, bitmap);

    if (foundX != -1) {
        jintArray result = env->NewIntArray(2);
        jint res[2] = {foundX, foundY};
        env->SetIntArrayRegion(result, 0, 2, res);
        return result;
    }

    return nullptr;
}
