package com.coc.zkqcode.core.system.checkpermissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.coc.zkqcode.core.util.exit.AppExitHelper
import java.security.MessageDigest

object CheckIntergrity {
    private const val EXPECTED_SHA256 = "BD:6C:AC:AC:7C:E2:62:63:E5:65:2F:AC:C4:B5:D1:58:FA:4C:98:8E:EE:FF:24:76:BB:25:46:BE:0D:97:1D:AE"

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
            return sha256.equals(EXPECTED_SHA256.replace(":", ""), ignoreCase = true)
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
