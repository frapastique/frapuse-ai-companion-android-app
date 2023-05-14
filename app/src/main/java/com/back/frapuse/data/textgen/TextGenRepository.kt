package com.back.frapuse.data.textgen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.textgen.models.TextGenChatLibrary
import com.back.frapuse.data.textgen.models.TextGenGenerateRequest
import com.back.frapuse.data.textgen.models.TextGenGenerateResponse
import com.back.frapuse.data.textgen.models.TextGenGenerateResponseText
import com.back.frapuse.data.textgen.models.TextGenModelResponse
import com.back.frapuse.data.textgen.models.TextGenPrompt
import com.back.frapuse.data.textgen.models.TextGenTokenCountBody
import com.back.frapuse.data.textgen.models.TextGenTokenCountResponse
import com.back.frapuse.data.textgen.local.TextGenChatLibraryDatabase
import com.back.frapuse.data.textgen.models.TextGenStreamResponse
import com.back.frapuse.data.textgen.remote.TextGenBlockAPI
import com.back.frapuse.data.textgen.remote.TextGenStreamWebSocketClient

private const val TAG = "TextGenRepository"

class TextGenRepository(
    private val apiBlock: TextGenBlockAPI,
    private val database: TextGenChatLibraryDatabase
    ) {

    // Instance of the WebSocketClient class
    private val textGenStreamWebSocketClient = TextGenStreamWebSocketClient()

    // LiveData object to expose the messages from the server
    private val _streamResponseMessage = MutableLiveData<TextGenStreamResponse>()
    val streamResponseMessage: LiveData<TextGenStreamResponse>
        get() = _streamResponseMessage

    // LiveData object to expose the errors from the websocket
    private val _streamErrorMessage = MutableLiveData<String>()
    val streamErrorMessage: LiveData<String>
        get() = _streamErrorMessage

    init {
        // Callback method for the WebSocketClient class
        textGenStreamWebSocketClient.onResponseReceived = { textGenStreamResponse ->
            _streamResponseMessage.postValue(textGenStreamResponse)
        }
        textGenStreamWebSocketClient.onError = { text ->
            _streamErrorMessage.postValue(text)
        }
    }

    /* _______ TextGen Block ___________________________________________________________ */

    suspend fun getModel(): TextGenModelResponse {
        return try {
            apiBlock.retrofitService.getModel()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading model from TextGen block API: \n\t $e")
            TextGenModelResponse("Error")
        }
    }

    suspend fun getTokenCount(prompt: TextGenPrompt): TextGenTokenCountResponse {
        return try {
            apiBlock.retrofitService.getTokenCount(prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving token count from TextGen block API: \n\t $e")
            TextGenTokenCountResponse(listOf(TextGenTokenCountBody("Error")))
        }
    }

    suspend fun generateBlockText(parameters: TextGenGenerateRequest): TextGenGenerateResponse {
        return try {
            apiBlock.retrofitService.generateText(parameters)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving text response TextGen block API: \n\t $e")
            TextGenGenerateResponse(listOf(TextGenGenerateResponseText("Error")))
        }
    }

    /* _______ TextGen Stream __________________________________________________________ */

    // Method to send a message to the server using the WebSocketClient class
    fun sendMessageToWebSocket(textGenGenerateRequest: TextGenGenerateRequest) {
        try {
            textGenStreamWebSocketClient.sendMessage(textGenGenerateRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message to server over websocket:\n\t$e")
        }
    }

    // Method to close the websocket connection using the WebSocketClient class
    fun closeWebsocketClient() {
        try {
            textGenStreamWebSocketClient.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing websocket:\n\t$e")
        }
    }

    // Method to reset stream response message
    fun resetStreamResponseMessage() {
        _streamResponseMessage.value = TextGenStreamResponse(
            event = "waiting",
            message_num = 0
        )
    }

    /* ____________________________________ Methods Local ______________________________ */

    /**
     * Method to insert an element into the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat information which gets inserted
     * */
    suspend fun insertChat(TextGenChatLibrary: TextGenChatLibrary) {
        try {
            database.textGenChatDao.insertChat(TextGenChatLibrary)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting chat in 'textGenChatLibrary_table': \n\t $e")
        }
    }

    /**
     * Method to load all elements from the 'textGenChatLibrary_table' database
     * @return List -> TextGenChatLibrary
     * */
    suspend fun getAllChats(): List<TextGenChatLibrary> {
        return try {
            database.textGenChatDao.getAllChats()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all chats from 'textGenChatLibrary_table': \n\t $e")
            listOf()
        }
    }

    /**
     * Method to update an element in the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat entry which gets updated
     * */
    suspend fun updateChat(TextGenChatLibrary: TextGenChatLibrary) {
        try {
            database.textGenChatDao.updateChat(TextGenChatLibrary)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating chat in 'textGenChatLibrary_table': \n\t $e")
        }
    }

    /**
     * Method to get the count of elements from the 'textGenChatLibrary_table' database
     * @return Int
     * */
    suspend fun getChatCount(): Int {
        return try {
            database.textGenChatDao.getChatCount()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting the size of 'textGenChatLibrary_table': \n\t $e")
        }
    }

    /**
     * Method to delete an element in the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat entry which gets deleted
     * */
    suspend fun deleteChat(TextGenChatLibrary: TextGenChatLibrary) {
        try {
            database.textGenChatDao.deleteChat(TextGenChatLibrary)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting chat from 'textGenChatLibrary_table': \n\t $e")
        }
    }

    /**
     * Method to delete all elements from the 'textGenChatLibrary_table' database
     * */
    suspend fun deleteAllChats() {
        try {
            database.textGenChatDao.deleteAllChats()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all chats from 'textGenChatLibrary_table': \n\t $e")
        }
    }

    /**
     * Method to get specified element from the 'textGenChatLibrary_table' database
     * @param chatID ID of the wanted chat
     * @return TextGenChatLibrary
     * */
    suspend fun getChat(ID: Long): TextGenChatLibrary {
        return try {
            database.textGenChatDao.getChat(ID)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching chat from 'textGenChatLibrary_table': \n\t $e")
            TextGenChatLibrary(
                conversationID = -1,
                dateTime = "",
                modelName = "",
                tokens = "",
                type = "",
                message = "",
                sentImage = "",
                sentDocument = "",
                documentText = "",
                finalContext = ""
            )
        }
    }
}