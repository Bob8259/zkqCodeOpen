package com.coc.zkqcode.nativehelper
//The C++ code are unused. But I just want to keep them as a souvenir.

@Suppress("KotlinJniMissingFunction") //明明运行正常，还一直报错，把你屏蔽了
object NativeTools {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getNativeTwo(): Int

    external fun generateNonce(): String
    external fun generateX25519KeyPair(): String
    external fun chacha20Encrypt(data: String, nonce: String): String
    external fun chacha20Decrypt(data: String, nonce: String): String
    external fun blake2b(data: String): String
    external fun computeSharedSecret(yourSecretKey: String, theirPublicKey: String): String

    external fun encryptLoginPayload(payload: String, serverPublicKey: String): String
    external fun decryptLoginResponse(encryptedResponse: String): String
    external fun decryptJar(data: ByteArray): ByteArray

    /**
     * Creates an in-memory file descriptor and writes the provided data to it.
     * Uses memfd_create on newer kernels, falls back to ashmem on older ones.
     * @param data The byte array to write to memory
     * @return File descriptor (>= 0) on success, -1 on failure
     */
    external fun createInMemoryDex(data: ByteArray): Int

    external fun nativeFindMultiColors(
        bitmap: android.graphics.Bitmap,
        x1: Int, y1: Int, x2: Int, y2: Int,
        mainColor: Int,
        threshold: Int,
        flatOffsets: IntArray
    ): IntArray?

    external fun nativeFindMultiColorsRaw(
        buffer: java.nio.ByteBuffer,
        width: Int, height: Int, stride: Int,
        x1: Int, y1: Int, x2: Int, y2: Int,
        mainColor: Int,
        threshold: Int,
        flatOffsets: IntArray
    ): IntArray?
}
