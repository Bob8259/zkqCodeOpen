#include <jni.h>
#include <android/log.h>
#include <vector>
#include <cmath>
#include <cstdlib>
#include "multi_colors_utils.h"

extern "C"
jintArray findMultiColorsRaw(
        JNIEnv *env, jobject thiz,
        jobject byteBuffer,
        jint width, jint height,
        jint stride, // stride is in bytes
        jint x1, jint y1, jint x2, jint y2,
        jint mainColor,
        jint threshold,
        jintArray flatOffsets) {

    auto *srcBuf = (uint8_t *) env->GetDirectBufferAddress(byteBuffer);
    if (srcBuf == nullptr) {
        return nullptr;
    }

    jint *offsetsArr = env->GetIntArrayElements(flatOffsets, nullptr);
    jsize offsetsLen = env->GetArrayLength(flatOffsets);

    int foundX = -1;
    int foundY = -1;

    auto getPixel = [&](int x, int y) {
        return *reinterpret_cast<uint32_t *>(srcBuf + y * stride + x * 4);
    };

    bool found = findMultiColorsInternal(
            static_cast<int>(width), static_cast<int>(height),
            x1, y1, x2, y2,
            static_cast<uint32_t>(mainColor),
            threshold,
            offsetsArr,
            offsetsLen,
            getPixel,
            foundX, foundY
    );

    env->ReleaseIntArrayElements(flatOffsets, offsetsArr, JNI_ABORT);

    if (found) {
        jintArray result = env->NewIntArray(2);
        jint res[2] = {foundX, foundY};
        env->SetIntArrayRegion(result, 0, 2, res);
        return result;
    }

    return nullptr;
}
