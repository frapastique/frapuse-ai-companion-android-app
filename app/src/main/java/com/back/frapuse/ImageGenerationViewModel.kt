package com.back.frapuse

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.ImageGenerationRepository
import com.back.frapuse.data.datamodels.ImageBase64
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.data.datamodels.Options
import com.back.frapuse.data.datamodels.SDModel
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.local.getDatabase
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ImageGenerationViewModel"

enum class ApiOptionsStatus { LOADING, ERROR, DONE }

class ImageGenerationViewModel(application: Application) : AndroidViewModel(application) {

    // Database value
    private val database = getDatabase(application)

    // Repository value
    private val repository = ImageGenerationRepository(TextToImageAPI, database)

    // Image in Base64 format
    val imageBase64 = repository.image

    // Live preview image in Base64 format
    val progressImageBase64 = repository.currentImage

    private val _models = MutableLiveData<List<SDModel>>()
    val models: LiveData<List<SDModel>>
        get() = _models

    val options = repository.options

    private var _prompt = MutableLiveData<String>()
    val prompt: LiveData<String>
        get() = _prompt

    private val _negativePrompt = MutableLiveData<String>("")
    val negativePrompt: LiveData<String>
        get() = _negativePrompt

    private val _cfgScale = MutableLiveData<Int>()
    val cfgScale: LiveData<Int>
        get() = _cfgScale

    private var _steps = MutableLiveData<Int>()
    val steps: LiveData<Int>
        get() = _steps

    private var _width = MutableLiveData<Int>()
    val width: LiveData<Int>
        get() = _width

    private var _height = MutableLiveData<Int>()
    val height: LiveData<Int>
        get() = _height

    private var _textToImageRequest = MutableLiveData<TextToImageRequest>()
    val textToImageRequest: LiveData<TextToImageRequest>
        get() = _textToImageRequest

    private var _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    private var _progress = MutableLiveData<Double>()
    val progress: LiveData<Double>
        get() = _progress

    private val _optionsStatus = MutableLiveData<ApiOptionsStatus>()
    val optionsStatus: LiveData<ApiOptionsStatus>
        get() = _optionsStatus

    val imageInfo = repository.imageInfo

    private val _imageMetadata = MutableLiveData<ImageMetadata>()
    val imageMetadata: LiveData<ImageMetadata>
        get() = _imageMetadata

    private val _genDataStatus = MutableLiveData<Boolean>()
    val genDataStatus: LiveData<Boolean>
        get() = _genDataStatus

    /* ____________________________________ Methods Remote _____________________________ */

    fun setPrompt(prompt: String) {
        _prompt.value = prompt
        checkGenData()
    }

    fun setNegativePrompt(newNegativePrompt: String) {
        _negativePrompt.value = newNegativePrompt
        checkGenData()
    }

    fun setSteps(steps: String) {
        if (steps.isNotEmpty()) {
            _steps.value = steps.toInt()
        }
    }

    fun setCfgScale(cfgScale: String) {
        if (cfgScale.isNotEmpty()) {
            _cfgScale.value = cfgScale.toInt()
        }
    }

    fun setWidth(width: String) {
        if (width.isNotEmpty()) {
            _width.value = width.toInt()
        }
    }

    fun setHeight(height: String) {
        if (height.isNotEmpty()) {
            _height.value = height.toInt()
        }
    }

    private fun checkGenData() {
        _genDataStatus.value = _prompt.value!!.isNotBlank()
                && _cfgScale.value!! > 0
                && _steps.value!! > 0
                && _width.value!! > 0
                && _height.value!! > 0
    }

    fun setTextToImageRequest() {
        _textToImageRequest.value = TextToImageRequest(
            _prompt.value!!,
            _cfgScale.value!!,
            _steps.value!!,
            _width.value!!,
            _height.value!!,
            _negativePrompt.value!!
        )
    }

    fun loadTextToImage() {
        viewModelScope.launch {
            repository.startTextToImage(_textToImageRequest.value!!)
        }

        viewModelScope.launch {
            try {
                do {
                    repository.getProgress()
                    try {
                        _progress.value = repository.progress.value
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading progress: \n\t $e")
                    }
                    delay(100)
                } while (_progress.value!! < 0.90)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress loop: \n\t $e")
            }
            delay(200)
            _progress.value = 1.0
        }
    }

    fun decodeImage(imageBase64: String) {
        viewModelScope.launch {
            repository.getImageInfo(ImageBase64(imageBase64))
        }
        val decodedByte = Base64.decode(imageBase64, Base64.DEFAULT)
        _image.value = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

        applyImageMetadata()
    }

    fun loadOptions() {
        _optionsStatus.value = ApiOptionsStatus.LOADING
        viewModelScope.launch {
            repository.getOptions()
            _optionsStatus.value = ApiOptionsStatus.DONE
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            delay(1000)
            repository.getModels()
            try {
                _models.value = repository.models.value!!
            } catch (e: Exception) {
                Log.e(TAG, "Error loading models: \n\t $e")
            }
        }
    }

    fun setModel(modelName: String) {
        _optionsStatus.value = ApiOptionsStatus.LOADING
        val newModel = _models.value!!.find { it.model_name == modelName }
        val newOptions = Options(
            sd_model_checkpoint = newModel!!.title,
        )
        viewModelScope.launch {
            repository.setOptions(newOptions)
            _optionsStatus.value = ApiOptionsStatus.DONE
        }
    }

    /* ____________________________________ Methods Local ______________________________ */

    private fun applyImageMetadata() {
        _imageMetadata.value = ImageMetadata(
            seed = 0,
            positivePrompt = _prompt.value!!,
            negativePrompt = "",
            image = imageBase64.value!!.images.first(),
            steps = _steps.value!!,
            size = "${_width.value}x${_height.value}",
            width = _width.value!!,
            height = _height.value!!,
            sampler = "",
            CFGScale = 0.0,
            model = "",
            modelHash = ""
        )
    }

    fun saveImage() {
        viewModelScope.launch {
            try {
                repository.insertImage(_imageMetadata.value!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image in database: \n\t $e")
            }
        }
    }
}