package com.coc.zkqcode.core.system.checkpermissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.coc.zkqcode.core.util.exit.AppExitHelper
import java.security.MessageDigest

object CheckIntergrity {
    // Obfuscated hash storage - XOR encoded to prevent static string extraction
    private val _d = intArrayOf(
        24, 120, 72, 90, 12, 40, 27, 127, 73, 90, 8, 89,
        108, 14, 72, 42, 8, 94, 108, 9, 76, 95, 12, 40,
        25, 8, 60, 44, 9, 90, 111, 4, 56, 88, 121, 40,
        99, 4, 70, 92, 8, 46, 28, 122, 76, 45, 122, 93,
        24, 126, 76, 44, 121, 93, 24, 121, 78, 93, 116, 92,
        107, 120, 63, 92
    )
    private val _k = intArrayOf(0x5A, 0x3C, 0x7E, 0x19, 0x4D, 0x6B)

    // Recover the expected SHA256 hex string at runtime via XOR deobfuscation
    private fun expectedHash(): String {
        return _d.mapIndexed { i, v -> (v xor _k[i % _k.size]).toChar() }.joinToString("")
    }

    fun checkAppIntegrity(context: Context) {
        // Only check at release mode
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            return
        }

        if (!verifySignature(context)) {
            AppExitHelper.exitApplication(context)
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun verifySignature(context: Context): Boolean {
        try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            
            val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signingInfo = packageInfo.signingInfo ?: return false
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners[0].toByteArray()
                } else {
                    signingInfo.signingCertificateHistory[0].toByteArray()
                }
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                val signatures = packageInfo.signatures ?: return false
                @Suppress("DEPRECATION")
                signatures[0].toByteArray()
            }

            val sha256 = getSHA256(signature)
            return sha256.equals(expectedHash(), ignoreCase = true)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun getSHA256(signature: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(signature)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
