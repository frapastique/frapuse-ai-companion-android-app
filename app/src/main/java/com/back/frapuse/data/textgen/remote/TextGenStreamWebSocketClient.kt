package com.back.frapuse.data.textgen.remote

import android.util.Log
import com.back.frapuse.data.textgen.models.TextGenGenerateRequest
import com.back.frapuse.data.textgen.models.TextGenStreamResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private const val TAG = "TextGenStreamSocketClient"
private const val BASE_URL = "ws://192.168.178.20:7864/api/v1/stream"

class TextGenStreamWebSocketClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // OkHttpClient instance
    private val okHttpClient = OkHttpClient()

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
    private val webSocket: WebSocket

    // Callback method to handle the messages from the server
    var onResponseReceived: ((TextGenStreamResponse?) -> Unit)? = null

    // Callback method to handle the errors from the websocket
    var onError: ((String?) -> Unit)? = null

    init {
        // Request builder object with the websocket URL
        val request = Request.Builder().url(BASE_URL).build()
        // Initialize the websocket instance
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    // Method to send a message to the server
    fun sendMessage(textGenGenerateRequest: TextGenGenerateRequest) {
        webSocket.send(moshi.adapter(TextGenGenerateRequest::class.java)
            .toJson(textGenGenerateRequest)
        )
    }

    // Method to close the websocket connection
    fun close() {
        webSocket.close(1000, "Stream ended")
    }
}