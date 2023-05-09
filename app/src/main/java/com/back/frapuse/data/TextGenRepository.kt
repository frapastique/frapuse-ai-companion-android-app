package com.back.frapuse.data

import android.util.Log
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponseText
import com.back.frapuse.data.datamodels.textgen.TextGenModelResponse
import com.back.frapuse.data.datamodels.textgen.TextGenPrompt
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountBody
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountResponse
import com.back.frapuse.data.local.TextGenChatLibraryDatabase
import com.back.frapuse.data.remote.TextGenBlockAPI

private const val TAG = "TextGenRepository"

class TextGenRepository(private val apiBlock: TextGenBlockAPI, private val database: TextGenChatLibraryDatabase) {

    /* ____________________________________ Methods Remote _____________________________ */

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
    suspend fun getChat(chatID: Long): TextGenChatLibrary {
        return try {
            database.textGenChatDao.getChat(chatID)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching chat from 'textGenChatLibrary_table': \n\t $e")
            TextGenChatLibrary(
                dateTime = "",
                tokens = "",
                name = "",
                profilePicture = "",
                message = "",
                sentImage = "",
                sentDocument = "",
                documentText = ""
            )
        }
    }
}