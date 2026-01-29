#include <jni.h>
#include <android/log.h>
#include "dex_loader.h"


//The C++ code are unused. But I just want to keep them as a souvenir.

extern "C" void start_security_monitor();

// Implementation of the function (not exported directly)
jint getNativeTwo(JNIEnv *env, jobject thiz) {
    return 4;
}

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz);

extern "C" JNIEXPORT jstring JNICALL
generateX25519KeyPair(JNIEnv *env, jobject thiz);

extern "C" JNIEXPORT jstring JNICALL
chacha20Encrypt(JNIEnv *env, jobject thiz, jstring data, jstring nonce);

extern "C" JNIEXPORT jstring JNICALL
chacha20Decrypt(JNIEnv *env, jobject thiz, jstring data, jstring nonce);

extern "C" JNIEXPORT jstring JNICALL
blake2b(JNIEnv *env, jobject thiz, jstring data);

extern "C" JNIEXPORT jstring JNICALL
computeSharedSecret(JNIEnv *env, jobject thiz, jstring your_secret_key, jstring their_public_key);

extern "C" JNIEXPORT jstring JNICALL
encryptLoginPayload(JNIEnv *env, jobject thiz, jstring payload, jstring server_public_key_hex);

extern "C" JNIEXPORT jstring JNICALL
decryptLoginResponse(JNIEnv *env, jobject thiz, jstring encrypted_response);

extern "C" JNIEXPORT jbyteArray JNICALL
decryptJar(JNIEnv *env, jobject thiz, jbyteArray data);

extern "C" JNIEXPORT jintArray JNICALL
findMultiColors(JNIEnv *env, jobject thiz, jobject bitmap, jint x1, jint y1, jint x2, jint y2,
                jint mainColor, jint threshold, jintArray flatOffsets);

extern "C" JNIEXPORT jintArray JNICALL
findMultiColorsRaw(JNIEnv *env, jobject thiz, jobject buffer, jint width, jint height, jint stride,
                   jint x1, jint y1, jint x2, jint y2, jint mainColor, jint threshold,
                   jintArray flatOffsets);

// Array of native methods to register
static const JNINativeMethod gMethods[] = {
        {"getNativeTwo",             "()I",                                                      (void *) getNativeTwo},
        {"generateNonce",            "()Ljava/lang/String;",                                     (void *) generateNonce},
        {"generateX25519KeyPair",    "()Ljava/lang/String;",                                     (void *) generateX25519KeyPair},
        {"chacha20Encrypt",          "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) chacha20Encrypt},
        {"chacha20Decrypt",          "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) chacha20Decrypt},
        {"blake2b",                  "(Ljava/lang/String;)Ljava/lang/String;",                   (void *) blake2b},
        {"computeSharedSecret",      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) computeSharedSecret},
        {"encryptLoginPayload",      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) encryptLoginPayload},
        {"decryptLoginResponse",     "(Ljava/lang/String;)Ljava/lang/String;",                   (void *) decryptLoginResponse},
        {"decryptJar",               "([B)[B",                                                   (void *) decryptJar},
        {"createInMemoryDex",        "([B)I",                                                    (void *) createInMemoryDex},
        {"nativeFindMultiColors",    "(Landroid/graphics/Bitmap;IIIIII[I)[I",                    (void *) findMultiColors},
        {"nativeFindMultiColorsRaw", "(Ljava/nio/ByteBuffer;IIIIIIIII[I)[I",                     (void *) findMultiColorsRaw},
};

// JNI_OnLoad is called when the library is loaded
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Find the class where the methods are declared
    jclass clazz = env->FindClass("com/coc/zkqcode/nativehelper/NativeTools");
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    // Register the native methods
    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return JNI_ERR;
    }

    // Start security monitor thread
    start_security_monitor();

    return JNI_VERSION_1_6;
}
