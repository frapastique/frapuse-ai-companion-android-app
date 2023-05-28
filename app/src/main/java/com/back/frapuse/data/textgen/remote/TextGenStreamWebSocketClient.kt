package com.back.frapuse.data.textgen.remote

import com.back.frapuse.data.textgen.models.llm.TextGenGenerateRequest
import com.back.frapuse.data.textgen.models.llm.TextGenStreamResponse
import com.back.frapuse.util.Companions.Companion.moshi
import com.back.frapuse.util.Companions.Companion.okHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private const val TAG = "TextGenStreamSocketClient"
private const val BASE_URL = "ws://192.168.178.20:7864/api/v1/stream"

class TextGenStreamWebSocketClient {
    // Listener object which extends the WebSocketListener
    private val listener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            // Receive a message from the server and invoke a callback function
            onResponseReceived?.invoke(moshi
                .adapter(TextGenStreamResponse::class.java)
                .fromJson(text)
            )
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // Handle any errors and invoke a callback function
            onError?.invoke(t.message)
        }
    }

    // Websocket instance which passes the request and the listener
    private var webSocket: WebSocket? = null

    // Callback method to handle the messages from the server
    var onResponseReceived: ((TextGenStreamResponse?) -> Unit)? = null

    // Callback method to handle the errors from the websocket
    var onError: ((String?) -> Unit)? = null

    // Method to send a message to the server
    fun sendMessage(textGenGenerateRequest: TextGenGenerateRequest) {
        webSocket?.send(moshi.adapter(TextGenGenerateRequest::class.java)
            .toJson(textGenGenerateRequest)
        )
    }

    // Method to close the websocket connection
    fun close() {
        webSocket?.close(1000, "Stream ended")
    }

    // Method to open the websocket connection
    fun open() {
        // Request builder object with the websocket URL
        val request = Request.Builder().url(BASE_URL).build()
        // Initialize the websocket instance
        webSocket = okHttpClient.newWebSocket(request, listener)
    }
}