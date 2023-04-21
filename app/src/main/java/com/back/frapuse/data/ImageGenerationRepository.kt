package com.back.frapuse.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.datamodels.ImageBase64
import com.back.frapuse.data.datamodels.ImageInfo
import com.back.frapuse.data.datamodels.Options
import com.back.frapuse.data.datamodels.SDModel
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.local.ImageGenDatabase
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.delay

private const val TAG = "ImageGenerationRepository"

class ImageGenerationRepository(private val api: TextToImageAPI, private val database: ImageGenDatabase) {

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

    private var _imageInfo = MutableLiveData<ImageInfo>()
    val imageInfo: LiveData<ImageInfo>
        get() = _imageInfo

    /* ____________________________________ Methods ____________________________________ */

    suspend fun getModels() {
        try {
            _models.value = api.retrofitService.getModels()
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
            try {
                val currentImage = api.retrofitService.getProgress().current_image
                if (!currentImage.isNullOrEmpty()) {
                    _currentImage.value = currentImage.toString()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress image from API: \n\t$e")
            }
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

    suspend fun getImageInfo(imageBase64: ImageBase64) {
        try {
            _imageInfo.value = api.retrofitService.getImageMetaData(imageBase64)
        } catch (e: Exception) {
            Log.e(TAG, "Error image info from API: \n\t$e")
        }
    }
}