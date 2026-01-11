#include <jni.h>
#include "encrypt.h"
#include "xtea_hash.h"

// Implementation of the function (not exported directly)
jint getNativeTwo(JNIEnv *env, jobject thiz) {
    return 4;
}

extern "C" JNIEXPORT jdouble JNICALL
verifyHash(JNIEnv *env, jobject thiz, jstring content_before_hash, jstring server_hash);

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz);

extern "C" JNIEXPORT jstring JNICALL
xteaEncrypt(JNIEnv *env, jobject thiz, jstring data, jstring key) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr);
    const char *keyChars = env->GetStringUTFChars(key, nullptr);

    std::string encrypted = XTEA_encrypt(dataChars, keyChars);

    env->ReleaseStringUTFChars(data, dataChars);
    env->ReleaseStringUTFChars(key, keyChars);

    return env->NewStringUTF(encrypted.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
xteaDecrypt(JNIEnv *env, jobject thiz, jstring data, jstring key) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr);
    const char *keyChars = env->GetStringUTFChars(key, nullptr);

    std::string decrypted = XTEA_decrypt(dataChars, keyChars);

    env->ReleaseStringUTFChars(data, dataChars);
    env->ReleaseStringUTFChars(key, keyChars);

    return env->NewStringUTF(decrypted.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
nativeXteaHash(JNIEnv *env, jobject thiz, jstring data, jstring key) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr);
    const char *keyChars = env->GetStringUTFChars(key, nullptr);

    std::string result = XTEA_generate_hash(dataChars, keyChars);

    env->ReleaseStringUTFChars(data, dataChars);
    env->ReleaseStringUTFChars(key, keyChars);

    return env->NewStringUTF(result.c_str());
}

// Array of native methods to register
static const JNINativeMethod gMethods[] = {
        {"getNativeTwo",  "()I",                                     (void *) getNativeTwo},
        {"verifyHash",    "(Ljava/lang/String;Ljava/lang/String;)D", (void *) verifyHash},
        {"generateNonce", "()Ljava/lang/String;",                    (void *) generateNonce},
        {"xteaEncrypt",   "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) xteaEncrypt},
        {"xteaDecrypt",   "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) xteaDecrypt},
        {"nativeXteaHash","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) nativeXteaHash}
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
