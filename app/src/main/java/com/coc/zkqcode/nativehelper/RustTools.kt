package com.coc.zkqcode.nativehelper

object RustTools {
    init {
        // The name must match the name in Cargo.toml
        System.loadLibrary("rust_logic")
    }

    // Declare native methods, names must correspond to those in Rust
    external fun sayHello(input: String): String

    external fun decryptJar(data: ByteArray): ByteArray

    external fun generateNonce(): String

    external fun encryptLoginPayload(payload: String): String

    external fun decryptLoginResponse(encryptedResponse: String): String

    external fun getLastTime(): Long

    external fun updateLastTime(newTime: Long)

    external fun getIsAuthPass(): Boolean

    external fun revokeAuthPass()

    external fun markAdStart()

    external fun markAdEnd()

    external fun createInMemoryDex(data: ByteArray): Int

    external fun findMultiColors(
        bitmap: android.graphics.Bitmap,
        x1: Int, y1: Int, x2: Int, y2: Int,
        mainColor: Int,
        threshold: Int,
        flatOffsets: IntArray,
        direction: Int,
        increment: Int
    ): IntArray?

    external fun findMultiColorsRaw(
        byteBuffer: java.nio.ByteBuffer,
        width: Int, height: Int,
        stride: Int,
        x1: Int, y1: Int, x2: Int, y2: Int,
        mainColor: Int,
        threshold: Int,
        flatOffsets: IntArray,
        direction: Int,
        increment: Int
    ): IntArray?
}