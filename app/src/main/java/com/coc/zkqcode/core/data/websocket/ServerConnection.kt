package com.coc.zkqcode.core.data.websocket

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ServerConnection(private val url: String) {
    private companion object {
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
    }
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var onMessageReceived: ((String) -> Unit)? = null

    fun connect(onOpen: () -> Unit, onMessage: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        Timber.d("Connecting to $url")
        this.onMessageReceived = onMessage
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Timber.d("WebSocket Opened")
                onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived?.invoke(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Timber.e(t, "WebSocket Failure: ${t.message}")
                onFailure(t)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("WebSocket Closing: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("WebSocket Closed: $code / $reason")
            }
        })
    }

    fun sendAction(action: Any) {
        val json = gson.toJson(action)
        val sent = webSocket?.send(json) ?: false
        if (!sent) {
            Timber.e("Failed to send message (WebSocket might be null or closed)")
        }
    }

    fun close() {
        Timber.d("Closing WebSocket")
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        onMessageReceived = null
    }
}
