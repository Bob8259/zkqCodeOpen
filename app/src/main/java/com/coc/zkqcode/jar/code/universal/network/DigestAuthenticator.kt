package com.coc.zkqcode.jar.code.universal.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * OkHttp Authenticator 实现 HTTP Digest 摘要认证。
 * 参考 OpenList gowebdav 的 digestAuth.go 设计。
 *
 * 用法：在 OkHttpClient.Builder 中设置
 *   .authenticator(DigestAuthenticator(username, password))
 *
 * 注意：当前实现仅支持 qop=auth 和 qop=auth-int（降级到 auth），不支持 MD5-sess。
 */
class DigestAuthenticator(
    private val username: String,
    private val password: String
) : Authenticator {

    private val hexChars = "0123456789abcdef"

    override fun authenticate(route: Route?, response: Response): Request? {
        val wwwAuth = response.header("WWW-Authenticate") ?: return null
        if (!wwwAuth.startsWith("Digest", ignoreCase = true)) return null

        val params = parseParams(wwwAuth.removePrefix("Digest").trim())
        val realm = params["realm"] ?: return null
        val nonce = params["nonce"] ?: return null
        val opaque = params["opaque"]
        val qop = params["qop"] // may be "auth" or "auth,auth-int" or null

        val method = response.request.method
        val uri = response.request.url.encodedPath
        val cnonce = generateCnonce()
        val nc = "00000001"

        val ha1 = md5Hex("$username:$realm:$password")
        val ha2 = md5Hex("$method:$uri")

        // 选择 qop：优先 auth
        val selectedQop = when {
            qop == null -> null
            qop.split(",").any { it.trim() == "auth" } -> "auth"
            else -> null
        }

        val responseValue = if (selectedQop != null) {
            md5Hex("$ha1:$nonce:$nc:$cnonce:$selectedQop:$ha2")
        } else {
            md5Hex("$ha1:$nonce:$ha2")
        }

        val sb = StringBuilder("Digest ")
        sb.append("username=\"$username\", ")
        sb.append("realm=\"$realm\", ")
        sb.append("nonce=\"$nonce\", ")
        sb.append("uri=\"$uri\", ")
        sb.append("response=\"$responseValue\"")
        if (opaque != null) sb.append(", opaque=\"$opaque\"")
        if (selectedQop != null) sb.append(", qop=$selectedQop, nc=$nc, cnonce=\"$cnonce\"")
        sb.append(", algorithm=MD5")

        return response.request.newBuilder()
            .header("Authorization", sb.toString())
            .build()
    }

    private fun parseParams(header: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val regex = Regex("""(\w+)\s*=\s*(?:"([^"]*)"|([^,]*))""")
        regex.findAll(header).forEach { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[2].ifBlank { match.groupValues[3] }
            map[key] = value
        }
        return map
    }

    private fun md5Hex(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { String.format("%02x", it) }
    }

    private fun generateCnonce(): String {
        val random = ByteArray(16)
        SecureRandom().nextBytes(random)
        return random.joinToString("") { String.format("%02x", it) }
    }
}
