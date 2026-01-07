package com.coc.zkqcode.utils.websocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import java.util.concurrent.TimeUnit

class ServerConnection(private val url: String) {
    private val TAG = "ServerConnection"
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var onMessageReceived: ((String) -> Unit)? = null

    fun connect(onOpen: () -> Unit, onMessage: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        Log.d(TAG, "Connecting to $url")
        this.onMessageReceived = onMessage
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Opened")
                onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message Received: $text")
                onMessageReceived?.invoke(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Failure: ${t.message}", t)
                onFailure(t)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closing: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closed: $code / $reason")
            }
        })
    }

    fun sendAction(action: Any) {
        val json = gson.toJson(action)
        Log.d(TAG, "Sending Action: $json")
        val sent = webSocket?.send(json) ?: false
        if (!sent) {
            Log.e(TAG, "Failed to send message (WebSocket might be null or closed)")
        }
    }

    fun close() {
        Log.d(TAG, "Closing WebSocket")
        webSocket?.close(1000, "Normal closure")
    }
}
