package com.back.frapuse.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.datamodels.Options
import com.back.frapuse.data.datamodels.SDModel
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.delay

const val TAG = "ImageGenerationRepository"

class ImageGenerationRepository(private val api: TextToImageAPI) {

    private var _models = MutableLiveData<List<SDModel>>()
    val models: LiveData<List<SDModel>>
        get() = _models

    private var _options = MutableLiveData<Options>()
    val options: LiveData<Options>
        get() = _options

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
            Log.e(TAG, "Models count: \n\t${_models.value!!.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading models from API: \n\t$e")
        }
    }

    suspend fun getOptions() {
        try {
            _options.value = api.retrofitService.getOptions()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading options from API: \n\t$e")
        }
    }

    suspend fun startTextToImage(textToImageRequest: TextToImageRequest) {
        try {
            _image.value = api.retrofitService.startTextToImage(textToImageRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: \n\t$e")
        }
    }

    suspend fun getProgress() {
        try {
            _progress.value = api.retrofitService.getProgress().progress
        } catch (e: Exception) {
            Log.e(TAG, "Error loading progress from API: \n\t$e")
        }
    }

    suspend fun setOptions(options: Options) {
        try {
            api.retrofitService.setOptions(options)
            delay(100)
            getModels()
        } catch (e: Exception) {
            Log.e(TAG, "Error setting options: \n\t$e")
        }
    }
}