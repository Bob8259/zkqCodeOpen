package com.coc.zkqcode.zkqnative

object NativeTools {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getNativeTwo(): Int
    external fun verifyHash(contentBeforeHash: String, serverHash: String): Double
    external fun generateNonce(): String
}
