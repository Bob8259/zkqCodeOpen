#include <jni.h>
#include <android/log.h>
#include <vector>
#include <cmath>
#include <cstdlib>

inline bool isColorMatch(uint32_t pixel, uint32_t targetColor, int threshold) {
    // pixel (RGBA/BGRA little endian 0xAABBGGRR) depending on format, 
    // but usually ByteBuffer from ImageReader (RGBA_8888) is R G B A order in memory bytes.
    // So reading as uint32_t little endian:
    // Memory: R G B A
    // uint32: 0xAABBGGRR

    // targetColor is Java Color (ARGB: 0xAARRGGBB) => A R G B

    // Let's decode properly.
    // If pixel is read as uint32_t*, and memory is R G B A
    // pixel = A << 24 | B << 16 | G << 8 | R
    // 0xAABBGGRR

    int pr = static_cast<int>(pixel & 0xFF);
    int pg = static_cast<int>((pixel >> 8) & 0xFF);
    int pb = static_cast<int>((pixel >> 16) & 0xFF);
    // pa = (pixel >> 24) & 0xFF;

    // targetColor from Java: 0xAARRGGBB
    int tr = static_cast<int>((targetColor >> 16) & 0xFF);
    int tg = static_cast<int>((targetColor >> 8) & 0xFF);
    int tb = static_cast<int>(targetColor & 0xFF);

    return std::abs(pr - tr) <= threshold &&
           std::abs(pg - tg) <= threshold &&
           std::abs(pb - tb) <= threshold;
}

extern "C"
jintArray findMultiColorsRaw(
        JNIEnv *env, jobject thiz,
        jobject byteBuffer,
        jint width, jint height,
        jint stride, // stride is in bytes usually, or pixels? ImageReader rowStride is bytes.
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

    // Boundary check
    x1 = std::max(0, x1);
    y1 = std::max(0, y1);
    x2 = std::min((int) width - 1, x2);
    y2 = std::min((int) height - 1, y2);

    // Safety check stride
    if (stride < width * 4) {
        // Should not happen for RGBA_8888 unless rowStride is smaller?
        // But usually rowStride >= width * 4
    }

    for (int y = y1; y <= y2; ++y) {
        // Calculate row start address
        uint8_t *rowPtr = srcBuf + y * stride;

        for (int x = x1; x <= x2; ++x) {
            // Each pixel is 4 bytes: R G B A (in memory)
            // We treat it as uint32_t for speed if alignment allows, but 
            // getting direct value is safer with pointer math if unsafe.
            // Let's cast to uint32_t for convenient reading if we assume alignment or x86/arm allow unaligned.
            // Android ARM typically allows unaligned access.

            uint32_t pixel = *((uint32_t *) (rowPtr + x * 4));

            if (isColorMatch(pixel, (uint32_t) mainColor, threshold)) {
                bool allOffsetsMatch = true;
                for (int i = 0; i < offsetsLen; i += 3) {
                    int dx = offsetsArr[i];
                    int dy = offsetsArr[i + 1];
                    auto color = static_cast<uint32_t>(offsetsArr[i + 2]);

                    int tx = x + dx;
                    int ty = y + dy;

                    if (tx < 0 || tx >= width || ty < 0 || ty >= height) {
                        allOffsetsMatch = false;
                        break;
                    }

                    uint8_t *offsetRowPtr = srcBuf + ty * stride;
                    uint32_t offsetPixel = *((uint32_t *) (offsetRowPtr + tx * 4));

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

    if (foundX != -1) {
        jintArray result = env->NewIntArray(2);
        jint res[2] = {foundX, foundY};
        env->SetIntArrayRegion(result, 0, 2, res);
        return result;
    }

    return nullptr;
}
