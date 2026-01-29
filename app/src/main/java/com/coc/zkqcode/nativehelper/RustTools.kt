package com.coc.zkqcode.nativehelper

object RustTools {
    init {
        // 名字必须和 Cargo.toml 中的 name 一致
        System.loadLibrary("rust_logic")
    }

    // 声明 native 方法，名字必须和 Rust 里的对应
    external fun sayHello(input: String): String


    external fun chacha20Encrypt(data: String, nonce: String): String

    external fun chacha20Decrypt(data: String, nonce: String): String

    external fun blake2b(data: String): String

    external fun decryptJar(data: ByteArray): ByteArray

    external fun generateNonce(): String

    external fun encryptLoginPayload(payload: String, serverPublicKeyHex: String): String

    external fun decryptLoginResponse(encryptedResponse: String): String
}