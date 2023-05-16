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
import com.back.frapuse.data.textgen.local.TextGenDocumentOperationDatabase
import com.back.frapuse.data.textgen.models.TextGenDocumentOperation
import com.back.frapuse.data.textgen.models.TextGenStreamResponse
import com.back.frapuse.data.textgen.remote.TextGenBlockAPI
import com.back.frapuse.data.textgen.remote.TextGenStreamWebSocketClient

private const val TAG = "TextGenRepository"

class TextGenRepository(
    private val apiBlock: TextGenBlockAPI,
    private val databaseChat: TextGenChatLibraryDatabase,
    private val databaseOperation: TextGenDocumentOperationDatabase
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
            Log.e(
                TAG,
                "Error loading model from TextGen block API:" +
                        "\n\t$e"
            )
            TextGenModelResponse("Error")
        }
    }

    suspend fun getTokenCount(prompt: TextGenPrompt): TextGenTokenCountResponse {
        return try {
            apiBlock.retrofitService.getTokenCount(prompt)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error retrieving token count from TextGen block API:" +
                        "\n\t$e"
            )
            TextGenTokenCountResponse(listOf(TextGenTokenCountBody("Error")))
        }
    }

    suspend fun generateBlockText(parameters: TextGenGenerateRequest): TextGenGenerateResponse {
        return try {
            apiBlock.retrofitService.generateText(parameters)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error retrieving text response TextGen block API:" +
                        "\n\t$e"
            )
            TextGenGenerateResponse(listOf(TextGenGenerateResponseText("Error")))
        }
    }

    /* _______ TextGen Stream __________________________________________________________ */

    // Method to send a message to the server using the WebSocketClient class
    fun sendMessageToWebSocket(textGenGenerateRequest: TextGenGenerateRequest) {
        try {
            textGenStreamWebSocketClient.sendMessage(textGenGenerateRequest)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error sending message to server over websocket:" +
                        "\n\t$e"
            )
        }
    }

    // Method to close the websocket connection using the WebSocketClient class
    fun closeWebsocketClient() {
        try {
            textGenStreamWebSocketClient.close()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error closing websocket:" +
                        "\n\t$e"
            )
        }
    }

    // Method to reset stream response message
    fun resetStreamResponseMessage() {
        _streamResponseMessage.value = TextGenStreamResponse(
            event = "waiting",
            message_num = 0
        )
    }

    /* _______ Methods Local Chat ______________________________________________________ */

    /**
     * Method to insert an element into the 'textGenChatLibrary_table' database
     * @param textGenChatLibrary Chat information which gets inserted
     * */
    suspend fun insertChat(textGenChatLibrary: TextGenChatLibrary) {
        try {
            databaseChat.textGenChatDao.insertChat(textGenChatLibrary)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error inserting chat in 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to load all elements from the 'textGenChatLibrary_table' database
     * @return List -> TextGenChatLibrary
     * */
    suspend fun getAllChats(): List<TextGenChatLibrary> {
        return try {
            databaseChat.textGenChatDao.getAllChats()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting all chats from 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
            listOf()
        }
    }

    /**
     * Method to update an element in the 'textGenChatLibrary_table' database
     * @param textGenChatLibrary Chat entry which gets updated
     * */
    suspend fun updateChat(textGenChatLibrary: TextGenChatLibrary) {
        try {
            databaseChat.textGenChatDao.updateChat(textGenChatLibrary)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error updating chat in 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to get the count of elements from the 'textGenChatLibrary_table' database
     * @return Int
     * */
    suspend fun getChatCount(): Int {
        return try {
            databaseChat.textGenChatDao.getChatCount()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting the size of 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to delete an element in the 'textGenChatLibrary_table' database
     * @param textGenChatLibrary Chat entry which gets deleted
     * */
    suspend fun deleteChat(textGenChatLibrary: TextGenChatLibrary) {
        try {
            databaseChat.textGenChatDao.deleteChat(textGenChatLibrary)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting chat from 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to delete all elements from the 'textGenChatLibrary_table' database
     * */
    suspend fun deleteAllChats() {
        try {
            databaseChat.textGenChatDao.deleteAllChats()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting all chats from 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to get specified element from the 'textGenChatLibrary_table' database
     * @param ID ID of the wanted chat
     * @return TextGenChatLibrary
     * */
    suspend fun getChat(ID: Long): TextGenChatLibrary {
        return try {
            databaseChat.textGenChatDao.getChat(ID)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error fetching chat from 'textGenChatLibrary_table':" +
                        "\n\t$e"
            )
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

    /* _______ Methods Local Document Operation ________________________________________ */

    /**
     * Method to insert an element into the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation information which gets inserted
     * */
    suspend fun insertOperation(textGenDocumentOperation: TextGenDocumentOperation) {
        try {
            databaseOperation.textGenDocumentOperationDao.insertOperation(textGenDocumentOperation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error inserting operation in 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to load all elements from the 'textGenDocumentOperation_table' database
     * @return List -> TextGenDocumentOperation
     * */
    suspend fun getAllOperations(): List<TextGenDocumentOperation> {
        return try {
            databaseOperation.textGenDocumentOperationDao.getAllOperations()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting all operations from 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
            listOf()
        }
    }

    /**
     * Method to update an element in the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation entry which gets updated
     * */
    suspend fun updateOperation(textGenDocumentOperation: TextGenDocumentOperation) {
        try {
            databaseOperation.textGenDocumentOperationDao.updateOperation(textGenDocumentOperation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting all chats from 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to get the count of elements from the 'textGenDocumentOperation_table' database
     * @return Int
     * */
    suspend fun getOperationCount(): Int {
        return try {
            databaseOperation.textGenDocumentOperationDao.getOperationCount()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting the size of 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to delete an element in the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation entry which gets deleted
     * */
    suspend fun deleteOperation(textGenDocumentOperation: TextGenDocumentOperation) {
        try {
            databaseOperation.textGenDocumentOperationDao.deleteOperation(textGenDocumentOperation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting operation from 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to delete all elements from the 'textGenDocumentOperation_table' database
     * */
    suspend fun deleteAllOperations() {
        try {
            databaseOperation.textGenDocumentOperationDao.deleteAllOperations()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting all operations from 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Method to get specified element from the 'textGenDocumentOperation_table' database
     * @param id ID of the wanted operation
     * @return TextGenDocumentOperation
     * */
    suspend fun getOperation(id: Long): TextGenDocumentOperation {
        return try {
            databaseOperation.textGenDocumentOperationDao.getOperation(id)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error fetching operation from 'textGenDocumentOperation_table':" +
                        "\n\t$e"
            )
            TextGenDocumentOperation(
                documentID = -1,
                modelName = "",
                dateTime = "",
                tokens = "",
                type = "",
                message = "",
                status = "",
                pageCount = 0,
                currentPage = 0,
                path = "",
                context = ""
            )
        }
    }
}