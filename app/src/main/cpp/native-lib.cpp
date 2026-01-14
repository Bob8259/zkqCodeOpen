#include <jni.h>


// Implementation of the function (not exported directly)
jint getNativeTwo(JNIEnv *env, jobject thiz) {
    return 4;
}

extern "C" JNIEXPORT jdouble JNICALL
verifyHash(JNIEnv *env, jobject thiz, jstring content_before_hash, jstring server_hash);

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz);

extern "C" JNIEXPORT jstring JNICALL
generateX25519KeyPair(JNIEnv *env, jobject thiz);

extern "C" JNIEXPORT jstring JNICALL
chacha20Encrypt(JNIEnv *env, jobject thiz, jstring data, jstring key, jstring nonce);

extern "C" JNIEXPORT jstring JNICALL
chacha20Decrypt(JNIEnv *env, jobject thiz, jstring data, jstring key, jstring nonce);

extern "C" JNIEXPORT jstring JNICALL
blake2b(JNIEnv *env, jobject thiz, jstring data);

extern "C" JNIEXPORT jstring JNICALL
computeSharedSecret(JNIEnv *env, jobject thiz, jstring your_secret_key, jstring their_public_key);

extern "C" JNIEXPORT jstring JNICALL
encryptLoginPayload(JNIEnv *env, jobject thiz, jstring payload, jstring server_public_key_hex);

extern "C" JNIEXPORT jstring JNICALL
decryptLoginResponse(JNIEnv *env, jobject thiz, jstring encrypted_response);

// Array of native methods to register
static const JNINativeMethod gMethods[] = {
        {"getNativeTwo",           "()I",                                     (void *) getNativeTwo},
        {"verifyHash",             "(Ljava/lang/String;Ljava/lang/String;)D", (void *) verifyHash},
        {"generateNonce",          "()Ljava/lang/String;",                    (void *) generateNonce},
        {"generateX25519KeyPair",  "()Ljava/lang/String;",                    (void *) generateX25519KeyPair},
        {"chacha20Encrypt",        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) chacha20Encrypt},
        {"chacha20Decrypt",        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) chacha20Decrypt},
        {"blake2b",                "(Ljava/lang/String;)Ljava/lang/String;",  (void *) blake2b},
        {"computeSharedSecret",    "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) computeSharedSecret},
        {"encryptLoginPayload",    "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) encryptLoginPayload},
        {"decryptLoginResponse",   "(Ljava/lang/String;)Ljava/lang/String;",                  (void *) decryptLoginResponse},
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
