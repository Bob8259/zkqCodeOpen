#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <vector>
#include <cmath>
#include "multi_colors_utils.h"

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

    auto *data = static_cast<uint8_t *>(pixels);
    auto width = static_cast<int>(info.width);
    auto height = static_cast<int>(info.height);
    auto stride = static_cast<int>(info.stride);

    auto getPixel = [&](int x, int y) {
        return *reinterpret_cast<uint32_t *>(data + y * stride + x * 4);
    };

    bool found = findMultiColorsInternal(
            width, height,
            x1, y1, x2, y2,
            static_cast<uint32_t>(mainColor),
            threshold,
            offsetsArr,
            offsetsLen,
            getPixel,
            foundX, foundY
    );

    env->ReleaseIntArrayElements(flatOffsets, offsetsArr, JNI_ABORT);
    AndroidBitmap_unlockPixels(env, bitmap);

    if (found) {
        jintArray result = env->NewIntArray(2);
        jint res[2] = {foundX, foundY};
        env->SetIntArrayRegion(result, 0, 2, res);
        return result;
    }

    return nullptr;
}
