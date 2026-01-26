package com.coc.zkqcode.utils.checkpermissions

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object ServerHelper {
    suspend fun waitForServerResponse(): String? {
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://localhost:6839/zkq").build()
        val deferred = CompletableDeferred<String?>()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // 发送一条测试消息
                webSocket.send("""{"actionType": "system_action", "subAction": "read"}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                deferred.complete(text)
                webSocket.close(1000, "Done")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                deferred.complete(null)
            }
        })

        return withTimeoutOrNull(10000) { // 最多等待10秒
            deferred.await()
        }
    }
}
