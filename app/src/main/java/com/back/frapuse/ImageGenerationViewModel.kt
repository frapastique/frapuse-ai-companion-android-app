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
import com.back.frapuse.data.datamodels.ImageInfo
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.data.datamodels.Options
import com.back.frapuse.data.datamodels.Progress
import com.back.frapuse.data.datamodels.SDModel
import com.back.frapuse.data.datamodels.Sampler
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.local.getDatabase
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ImageGenerationViewModel"

enum class AppStatus { LOADING, ERROR, DONE }

class ImageGenerationViewModel(application: Application) : AndroidViewModel(application) {

    // Database value
    private val database = getDatabase(application)

    // Repository value
    private val repository = ImageGenerationRepository(TextToImageAPI, database)

    /* _______ Values Remote ___________________________________________________________ */

    // Current options
    private val _options = MutableLiveData<Options>()
    val options: LiveData<Options>
        get() = _options

    // List of models which are loaded on the server
    private val _models = MutableLiveData<List<SDModel>>()
    val models: LiveData<List<SDModel>>
        get() = _models

    // List of samplers from API
    private val _samplersList = MutableLiveData<List<Sampler>>()
    val samplersList: LiveData<List<Sampler>>
        get() = _samplersList

    // Current progress of image generation
    private var _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress>
        get() = _progress

    // Holds generation information of the final image
    private val _imageInfo = MutableLiveData<ImageInfo>()
    val imageInfo: LiveData<ImageInfo>
        get() = _imageInfo


    /* _______ Api Status ______________________________________________________________ */

    // Concurrent API status of options
    private val _apiStatusOptions = MutableLiveData<AppStatus>()
    val apiStatusOptions: LiveData<AppStatus>
        get() = _apiStatusOptions

    // Concurrent API status of text to image request
    private val _apiStatusTextToImg = MutableLiveData<AppStatus>()
    val apiStatusTextToImg: LiveData<AppStatus>
        get() = _apiStatusTextToImg


    /* _______ Values Local ____________________________________________________________ */

    // Holds the library of previously generated images and its metadata
    private var _imageLibrary = MutableLiveData<List<ImageMetadata>>()
    val imageLibrary: LiveData<List<ImageMetadata>>
        get() = _imageLibrary


    /* _______ Generation Parameters ___________________________________________________ */

    // Applied prompt
    private var _prompt = MutableLiveData<String>()
    val prompt: LiveData<String>
        get() = _prompt

    // Applied negative prompt
    private val _negativePrompt = MutableLiveData<String>()
    val negativePrompt: LiveData<String>
        get() = _negativePrompt

    // Applied configuration scale
    private val _cfgScale = MutableLiveData<Double>()
    val cfgScale: LiveData<Double>
        get() = _cfgScale

    // Applied step count
    private var _steps = MutableLiveData<Int>()
    val steps: LiveData<Int>
        get() = _steps

    // Applied image width
    private var _width = MutableLiveData<Int>()
    val width: LiveData<Int>
        get() = _width

    // Applied image height
    private var _height = MutableLiveData<Int>()
    val height: LiveData<Int>
        get() = _height

    // Applied seed
    private val _seed = MutableLiveData<Long>()
    val seed: LiveData<Long>
        get() = _seed

    // Applied sampler
    private val _sampler = MutableLiveData<Sampler>()
    val sampler: LiveData<Sampler>
        get() = _sampler

    // Formatted text to image request data for api call
    private var _textToImageRequest = MutableLiveData<TextToImageRequest>()
    val textToImageRequest: LiveData<TextToImageRequest>
        get() = _textToImageRequest


    /* _______ Image data _______________________________________________________________ */

    // Final image in Base64 format
    private var _finalImageBase64 = MutableLiveData<TextToImage>()
    val finalImageBase64: LiveData<TextToImage>
        get() = _finalImageBase64

    // Live preview image in Base64 format
    private val _progressImageBase64 = MutableLiveData<String>()
    val progressImageBase64: LiveData<String>
        get() = _progressImageBase64

    // Decoded bitmap image
    private var _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    // Holds the metadata of generated image which gets inserted into database
    private val _imageMetadata = MutableLiveData<ImageMetadata>()
    val imageMetadata: LiveData<ImageMetadata>
        get() = _imageMetadata


    /* _______ Local Status ____________________________________________________________ */

    // Current app status for text to image request data
    private val _appStatusTextToImageRequest = MutableLiveData<AppStatus>()
    val appStatusTextToImageRequest: LiveData<AppStatus>
        get() = _appStatusTextToImageRequest

    // Current status of apply image metadata
    private val _appStatusApplyMetaData = MutableLiveData<AppStatus>()
    val appStatusApplyMetaData: LiveData<AppStatus>
        get() = _appStatusApplyMetaData

    init {
        viewModelScope.launch {
            _imageLibrary.value = repository.getAllImages()
            loadOptions()
            loadSamplers()
            loadModels()
        }
    }

    /* _______ Methods Generation Parameters ___________________________________________ */

    fun setPrompt(newPrompt: String) {
        if (newPrompt != _prompt.value) {
            Log.e(
                TAG,
                "Prompt status: \n\t SETTING \n\t new: $newPrompt \n\t current: ${_prompt.value}"
            )
            _prompt.value = newPrompt
            Log.e(
                TAG,
                "Prompt status: \n\t SET \n\t new: $newPrompt \n\t current: ${_prompt.value}"
            )
        }
    }

    fun setNegativePrompt(newNegativePrompt: String) {
        if (newNegativePrompt != _negativePrompt.value) {
            Log.e(
                TAG,
                "Negative prompt status: \n\t SETTING \n\t new: $newNegativePrompt \n\t current: ${_negativePrompt.value}"
            )
            _negativePrompt.value = newNegativePrompt
            Log.e(
                TAG,
                "Negative prompt status: \n\t SET \n\t new: $newNegativePrompt \n\t current: ${_negativePrompt.value}"
            )
        }
    }

    fun setSteps(newSteps: Int) {
        Log.e(TAG, "Steps status: \n\t CHECK \n\t new: $newSteps")
        if (newSteps > 0 && newSteps != _steps.value) {
            Log.e(TAG, "Steps status: \n\t VALID \n\t new: $newSteps")
            Log.e(TAG, "Steps status: \n\t SETTING \n\t new: $newSteps \n\t current: ${_steps.value}")
            _steps.value = newSteps
            Log.e(TAG, "Steps status: \n\t SET \n\t new: $newSteps \n\t current: ${_steps.value}")
        } else {
            Log.e(TAG, "Steps status: \n\t INVALID \n\t current: ${_steps.value}")
        }
    }

    fun setCfgScale(newCfgScale: Double) {
        Log.e(TAG, "CFG scale status: \n\t CHECK \n\t new: $newCfgScale")
        if (newCfgScale > 0.0 && newCfgScale != _cfgScale.value) {
            Log.e(TAG, "CFG scale status: \n\t VALID \n\t new: $newCfgScale")
            Log.e(TAG, "CFG scale status: \n\t SETTING \n\t new: $newCfgScale \n\t current: ${_cfgScale.value}")
            _cfgScale.value = newCfgScale
            Log.e(TAG, "CFG scale status: \n\t SET \n\t new: $newCfgScale \n\t current: ${_cfgScale.value}")
        } else {
            Log.e(TAG, "CFG scale status: \n\t INVALID \n\t current: ${_cfgScale.value}")
        }
    }

    fun setWidth(newWidth: Int) {
        Log.e(TAG, "Width status: \n\t CHECK \n\t new: $newWidth")
        if (newWidth > 0 && newWidth != _width.value) {
            Log.e(TAG, "Width status: \n\t VALID \n\t new: $newWidth")
            Log.e(TAG, "Width status: \n\t SETTING \n\t new: $newWidth \n\t current: ${_width.value}")
            _width.value = newWidth
            Log.e(TAG, "Width status: \n\t SET \n\t new: $newWidth \n\t current: ${_width.value}")
        } else {
            Log.e(TAG, "Width status: \n\t INVALID \n\t current: ${_width.value}")
        }
    }

    fun setHeight(newHeight: Int) {
        Log.e(TAG, "Height status: \n\t CHECK \n\t new: $newHeight")
        if (newHeight > 0 && newHeight != _height.value) {
            Log.e(TAG, "Height status: \n\t VALID \n\t new: $newHeight")
            Log.e(TAG, "Height status: \n\t SETTING \n\t new: $newHeight \n\t current: ${_height.value}")
            _height.value = newHeight
            Log.e(TAG, "Height status: \n\t SET \n\t new: $newHeight \n\t current: ${_height.value}")
        } else {
            Log.e(TAG, "Height status: \n\t INVALID \n\t current: ${_height.value}")
        }
    }

    fun setSeed(newSeed: Long) {
        Log.e(TAG, "Seed status: \n\t CHECK \n\t new: $newSeed")
        if (newSeed >= (-1) && newSeed != seed.value) {
            Log.e(TAG, "Seed status: \n\t VALID \n\t new: $newSeed")
            Log.e(TAG, "Seed status: \n\t SETTING \n\t new: $newSeed \n\t current: ${_seed.value}")
            _seed.value = newSeed
            Log.e(TAG, "Seed status: \n\t SET \n\t new: $newSeed \n\t current: ${_seed.value}")
        } else {
            Log.e(TAG, "Seed status: \n\t INVALID \n\t current: ${_seed.value}")
        }
    }

    fun setTextToImageRequest() {
        _appStatusTextToImageRequest.value = AppStatus.LOADING
        Log.e(TAG, "TextToImageRequest status: \n\t CHECK")
        if (!_prompt.value.isNullOrEmpty()) {
            Log.e(TAG, "TextToImageRequest status: \n\t VALID")
            Log.e(TAG, "TextToImageRequest status: \n\t SETTING")
            _textToImageRequest.value = TextToImageRequest(
                prompt = _prompt.value!!,
                seed = _seed.value!!,
                cfg_scale = _cfgScale.value!!,
                sampler_name = sampler.value!!.name,
                steps = _steps.value!!,
                width = _width.value!!,
                height = _height.value!!,
                negative_prompt = _negativePrompt.value!!
            )
            Log.e(TAG, "TextToImageRequest status: \n\t SET")
            _appStatusTextToImageRequest.value = AppStatus.DONE
        } else {
            Log.e(TAG, "TextToImageRequest status: \n\t INVALID")
            _appStatusTextToImageRequest.value = AppStatus.ERROR
        }
    }

    fun loadTextToImage() {
        viewModelScope.launch {
            _apiStatusTextToImg.value = AppStatus.LOADING
            _finalImageBase64.value = repository.startTextToImage(_textToImageRequest.value!!)
            _apiStatusTextToImg.value = AppStatus.DONE
            cancel()
        }
    }

    fun loadProgress() {
        viewModelScope.launch {
            try {
                while (_apiStatusTextToImg.value == AppStatus.LOADING) {
                    delay(100)
                    try {
                        _progress.value = repository.getProgress()
                        try {
                            if (_progress.value!!.current_image != null) {
                                _progressImageBase64.value = _progress.value!!.current_image!!
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error loading progress image: \n\t $e")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading progress: \n\t $e")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress loop: \n\t $e")
            }
            cancel()
        }
    }

    fun loadOptions() {
        _apiStatusOptions.value = AppStatus.LOADING
        viewModelScope.launch {
            Log.e(TAG, "Option status: \n\t LOADING")
            _options.value = repository.getOptions()
            Log.e(TAG, "Option status: \n\t LOADED")
            _apiStatusOptions.value = AppStatus.DONE
            cancel()
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            delay(1000)
            try {
                Log.e(TAG, "Model status: \n\t LOADING")
                _models.value = repository.getModels()
                Log.e(TAG, "Model status: \n\t LOADED")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading models: \n\t $e")
            }
            cancel()
        }
    }

    fun setModel(modelName: String) {
        _apiStatusOptions.value = AppStatus.LOADING
        val newModel = _models.value!!.find { it.model_name == modelName }
        val newOptions = Options(
            sd_model_checkpoint = newModel!!.title
        )
        viewModelScope.launch {
            repository.setOptions(newOptions)
            _apiStatusOptions.value = AppStatus.DONE
        }
    }

    fun loadSamplers() {
        viewModelScope.launch {
            Log.e(TAG, "Samplers status: \n\t LOADING")
            _samplersList.value = repository.getSamplers()
            Log.e(TAG, "Samplers status: \n\t LOADED")
        }
    }

    fun setSampler(newSamplerName: String) {
        Log.e(TAG, "Sampler status: \n\t SETTING")
        _sampler.value = Sampler(
            name = newSamplerName
        )
        Log.e(TAG, "Sampler status: \n\t SET")
    }

    fun loadImageInfo() {
        viewModelScope.launch {
            _imageInfo.value = repository.getImageInfo(
                ImageBase64(
                    _finalImageBase64.value!!.images.first()
                )
            )
        }
    }

    /* _______ Methods Local ___________________________________________________________ */

    fun decodeImage(imageBase: String) {
        val decodedByte = Base64.decode(imageBase, Base64.DEFAULT)
        _image.value = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun applyImageMetadata() {
        _appStatusApplyMetaData.value = AppStatus.LOADING
        var lastSeed: Long = -1
        if (_seed.value!! == lastSeed) {
            try {
                lastSeed = Regex("Seed: (\\d+)")
                    .find(imageInfo.value!!.info)
                    ?.groupValues!![1].toLong()
            } catch (e: Exception) {
                Log.e(TAG, "Error finding seed: \n\t $e")
            }
        } else {
            lastSeed = _seed.value!!
        }

        viewModelScope.launch {
            try {
                _imageMetadata.value = ImageMetadata(
                    seed = lastSeed,
                    positivePrompt = _prompt.value!!,
                    negativePrompt = _negativePrompt.value!!,
                    image = _finalImageBase64.value!!.images.first(),
                    steps = _steps.value!!,
                    size = "${_width.value}x${_height.value}",
                    width = _width.value!!,
                    height = _height.value!!,
                    sampler = _sampler.value!!.name,
                    CFGScale = _cfgScale.value!!,
                    model = _options.value!!.sd_model_checkpoint,
                    info = imageInfo.value!!.info
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error applying image metadata: \n\t $e")
                _appStatusApplyMetaData.value = AppStatus.ERROR
            }
            cancel()
        }
        _appStatusApplyMetaData.value = AppStatus.DONE
    }

    fun saveImage() {
        viewModelScope.launch {
            try {
                Log.e(TAG, "Save image status: \n\t SAVING")
                repository.insertImage(_imageMetadata.value!!)
                Log.e(TAG, "Save image status: \n\t SAVED")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image in database: \n\t $e")
            }

            _imageLibrary.value = repository.getAllImages()
            cancel()
        }
    }
}