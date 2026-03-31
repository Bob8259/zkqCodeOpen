package com.coc.zkqcode.core.util.crypto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Solves a Proof-of-Work challenge by brute-forcing a salt whose SHA-256 hash
 * (when concatenated with the server nonce) satisfies the difficulty criteria:
 * first two bytes are zero and the high nibble of the third byte is < 3.
 */
@Suppress("KotlinUnreachableCode")
suspend fun solvePoW(nonce: String): String = withContext(Dispatchers.Default) {
    var salt = 0
    val md = MessageDigest.getInstance("SHA-256")
    while (true) {
        val saltStr = salt.toString()
        val data = (nonce + saltStr).toByteArray()
        val hashBytes = md.digest(data)

        // Check for 0000 (first 2 bytes are 0) and 5th char < 3 (high nibble of 3rd byte < 3)
        if (hashBytes[0] == 0.toByte() && hashBytes[1] == 0.toByte()) {
            val highNibble = (hashBytes[2].toInt() and 0xFF) ushr 4
            if (highNibble < 3) {
                return@withContext saltStr
            }
        }
        salt++
    }
    return@withContext ""
}
