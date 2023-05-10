package com.back.frapuse.data.textgen.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class TextGenStreamSocketClient(uri: URI) : WebSocketClient(uri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        // Send a request to get the prompt
        val request = mapOf(
            "prompt" to "In order to make homemade bread, follow these steps:\n1)",
            "max_new_tokens" to 250,
            "do_sample" to true,
            "temperature" to 1.3,
            "top_p" to 0.1,
            "typical_p" to 1,
            "repetition_penalty" to 1.18,
            "top_k" to 40,
            "min_length" to 0,
            "no_repeat_ngram_size" to 0,
            "num_beams" to 1,
            "penalty_alpha" to 0,
            "length_penalty" to 1,
            "early_stopping" to false,
            "seed" to -1,
            "add_bos_token" to true,
            "truncation_length" to 2048,
            "ban_eos_token" to false,
            "skip_special_tokens" to true,
            "stopping_strings" to listOf<String>()
        )
        val json = Gson().toJson(request)
        send(json)
    }

    override fun onMessage(message: String?) {
        // Receive a text message from the server
        println(message)
        // Parse the message as a JSON object
        val jsonObject = Gson().fromJson(message, JsonObject::class.java)
        // Check if the event is stream_end
        if (jsonObject["event"].asString == "stream_end") {
            // Close the connection with a normal closure status and a reason
            close(1000, "Stream ended")
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        // Close the connection gracefully
        close()
    }

    override fun onError(ex: Exception?) {
        // Handle error
        ex?.printStackTrace()
    }
}

/*fun main() {
    // Create a WebSocket connection using Java-WebSocket's connect method
    val uri = URI("ws://192.168.178.20:7864/api/v1/stream")
    val webSocketClient = TextGenStreamSocketClient(uri)
    webSocketClient.connect()
}*/
