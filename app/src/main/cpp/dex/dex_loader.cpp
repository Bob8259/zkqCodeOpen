#include "dex_loader.h"
#include <unistd.h>
#include <sys/syscall.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <cerrno>
#include <cstring>

// ashmem definitions (for older Android versions)
#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#pragma ide diagnostic ignored "OCUnusedMacroInspection"
#define ASHMEM_DEVICE "/dev/ashmem"
#define ASHMEM_NAME_LEN 256
#define ASHMEM_SET_NAME _IOW(0x77, 1, char[ASHMEM_NAME_LEN])//Do not delete unused variables. They are used to create spaces or maybe?
#define ASHMEM_SET_SIZE _IOW(0x77, 3, size_t)

// memfd_create syscall number (varies by architecture)
#ifndef __NR_memfd_create
#if defined(__aarch64__)
#define __NR_memfd_create 279
#elif defined(__arm__)
#define __NR_memfd_create 385
#elif defined(__i386__)
#define __NR_memfd_create 356
#elif defined(__x86_64__)
#define __NR_memfd_create 319
#endif
#endif

jint createInMemoryDex(JNIEnv *env, jobject thiz, jbyteArray data) {
    if (data == nullptr) {
        return -1;
    }

    jsize dataLen = env->GetArrayLength(data);
    if (dataLen <= 0) {
        return -1;
    }

    jbyte *dataBytes = env->GetByteArrayElements(data, nullptr);
    if (dataBytes == nullptr) {
        return -1;
    }

    int fd = -1;

    // Try memfd_create first (available on kernel 3.17+ / API 26+)
    #ifdef __NR_memfd_create
    fd = static_cast<int>(syscall(__NR_memfd_create, "dex", 0));
    #endif

    // Fallback to ashmem if memfd_create is not available or failed
    if (fd < 0) {
        fd = open(ASHMEM_DEVICE, O_RDWR);
        if (fd < 0) {
            env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
            return -1;
        }

        // Set the name for the ashmem region
        if (ioctl(fd, ASHMEM_SET_NAME, "dex") < 0) {
            close(fd);
            env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
            return -1;
        }

        // Set the size for the ashmem region
        if (ioctl(fd, ASHMEM_SET_SIZE, static_cast<size_t>(dataLen)) < 0) {
            close(fd);
            env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
            return -1;
        }
    }

    // Write data to the file descriptor
    ssize_t totalWritten = 0;
    while (totalWritten < dataLen) {
        ssize_t written = write(fd, dataBytes + totalWritten, dataLen - totalWritten);
        if (written < 0) {
            if (errno == EINTR) {
                continue;  // Interrupted, retry
            }
            close(fd);
            env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
            return -1;
        }
        totalWritten += written;
    }

    // Reset file position to beginning for reading
    if (lseek(fd, 0, SEEK_SET) < 0) {
        close(fd);
        env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
        return -1;
    }

    env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT);
    return fd;
}


#pragma clang diagnostic pop