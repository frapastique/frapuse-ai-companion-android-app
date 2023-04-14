package com.back.frapuse.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.datamodels.SDModels
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.remote.TextToImageAPI

const val TAG = "ImageGenerationRepository"

class ImageGenerationRepository(private val api: TextToImageAPI) {

    private var _models = MutableLiveData<List<SDModels>>()
    val models: LiveData<List<SDModels>>
        get() = _models

    private var _image = MutableLiveData<TextToImage>()
    val image: LiveData<TextToImage>
        get() = _image

    private var _currentImage = MutableLiveData<String>()
    val currentImage: LiveData<String>
        get() = _currentImage

    private var _progress = MutableLiveData<Double>()
    val progress: LiveData<Double>
        get() = _progress

    suspend fun getModels() {
        try {
            _models.value = api.retrofitService.getModels()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: $e")
        }
    }

    suspend fun startTextToImage(textToImageRequest: TextToImageRequest) {
        try {
            _image.value = api.retrofitService.startTextToImage(textToImageRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: $e")
        }
    }

    suspend fun getProgress() {
        try {
            _progress.value = api.retrofitService.getProgress().progress
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: $e")
        }
    }
}