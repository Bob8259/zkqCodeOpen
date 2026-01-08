#include <jni.h>

// Implementation of the function (not exported directly)
jint getNativeTwo(JNIEnv *env, jobject thiz) {
    return 4;
}

extern "C" JNIEXPORT jdouble JNICALL
verifyHash(JNIEnv *env, jobject thiz, jstring content_before_hash, jstring server_hash);

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz);

// Array of native methods to register
static const JNINativeMethod gMethods[] = {
        {"getNativeTwo",  "()I",                                     (void *) getNativeTwo},
        {"verifyHash",    "(Ljava/lang/String;Ljava/lang/String;)D", (void *) verifyHash},
        {"generateNonce", "()Ljava/lang/String;",                    (void *) generateNonce}
};

// JNI_OnLoad is called when the library is loaded
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Find the class where the methods are declared
    jclass clazz = env->FindClass("com/coc/zkqcode/zkqnative/NativeTools");
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    // Register the native methods
    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
