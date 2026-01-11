package com.coc.zkqcode.zkqnative

@Suppress("KotlinJniMissingFunction") //明明运行正常，还一直报错，把你屏蔽了
object NativeTools {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getNativeTwo(): Int
    external fun verifyHash(contentBeforeHash: String, serverHash: String): Double
    external fun generateNonce(): String
    external fun xteaEncrypt(data: String, key: String): String
    external fun xteaDecrypt(data: String, key: String): String
    external fun nativeXteaHash(data: String, key: String): String
}
