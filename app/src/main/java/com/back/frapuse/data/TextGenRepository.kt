package com.back.frapuse.data

import android.util.Log
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponseText
import com.back.frapuse.data.datamodels.textgen.TextGenModelResponse
import com.back.frapuse.data.datamodels.textgen.TextGenPrompt
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountBody
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountResponse
import com.back.frapuse.data.remote.TextGenBlockAPI
import com.back.frapuse.data.remote.TextGenStreamAPI

private const val TAG = "TextGenRepository"

class TextGenRepository(private val apiBlock: TextGenBlockAPI, private val apiStream: TextGenStreamAPI) {

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

    suspend fun generateStreamText(parameters: TextGenGenerateRequest): TextGenGenerateResponse {
        return try {
            apiStream.retrofitService.generateStreamText(parameters)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving text response TextGen stream API: \n\t $e")
            TextGenGenerateResponse(listOf(TextGenGenerateResponseText("Error")))
        }
    }
}