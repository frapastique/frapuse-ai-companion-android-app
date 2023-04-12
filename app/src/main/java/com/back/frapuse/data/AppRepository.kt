package com.back.frapuse.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.remote.TextToImageAPI

const val TAG = "AppRepository"

class AppRepository(private val api: TextToImageAPI) {

    private var _image = MutableLiveData<TextToImage>()
    val image: LiveData<TextToImage>
        get() = _image

    suspend fun getPrompt(textToImageRequest: TextToImageRequest) {
        try {
            _image.value = api.retrofitService.getPrompt(textToImageRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: $e")
        }
    }
}