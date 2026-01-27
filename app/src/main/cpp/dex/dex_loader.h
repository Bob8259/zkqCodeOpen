#ifndef DEX_LOADER_H
#define DEX_LOADER_H

#include <jni.h>

/**
 * Creates an in-memory file descriptor and writes the provided data to it.
 * Uses memfd_create on newer kernels, falls back to ashmem on older ones.
 * @param env JNI environment
 * @param thiz Java object reference
 * @param data Byte array containing the DEX/JAR data
 * @return File descriptor (>= 0) on success, -1 on failure
 */
jint createInMemoryDex(JNIEnv *env, jobject thiz, jbyteArray data);

#endif // DEX_LOADER_H
