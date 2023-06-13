package com.back.frapuse.ui.imagegen

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.imagegen.ImageGenRepository
import com.back.frapuse.data.imagegen.models.ImageGenImageBase64
import com.back.frapuse.data.imagegen.models.ImageGenImageInfo
import com.back.frapuse.data.imagegen.models.ImageGenImageMetadata
import com.back.frapuse.data.imagegen.models.ImageGenOptions
import com.back.frapuse.data.imagegen.models.ImageGenProgress
import com.back.frapuse.data.imagegen.models.ImageGenSDModel
import com.back.frapuse.data.imagegen.models.ImageGenSampler
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageResponse
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageRequest
import com.back.frapuse.data.imagegen.local.getImageGenDatabase
import com.back.frapuse.data.imagegen.remote.ImageGenAPI
import com.back.frapuse.util.AppStatus
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ImageGenViewModel"

class ImageGenViewModel(application: Application) : AndroidViewModel(application) {

    // Application context
    val app = getApplication<Application>()

    // Database value
    private val database = getImageGenDatabase(application)

    // Repository value
    private val repository = ImageGenRepository(ImageGenAPI, database)

    /* _______ Values Remote ___________________________________________________________ */

    // Current options
    private val _options = MutableLiveData<ImageGenOptions>()
    val options: LiveData<ImageGenOptions>
        get() = _options

    // List of models which are loaded on the server
    private val _models = MutableLiveData<List<ImageGenSDModel>>()
    val models: LiveData<List<ImageGenSDModel>>
        get() = _models

    // List of samplers from API
    private val _samplersList = MutableLiveData<List<ImageGenSampler>>()
    val samplersList: LiveData<List<ImageGenSampler>>
        get() = _samplersList

    // Current progress of image generation
    private var _progress = MutableLiveData<ImageGenProgress>()
    val progress: LiveData<ImageGenProgress>
        get() = _progress

    // Holds generation information of the final image
    private val _imageInfo = MutableLiveData<ImageGenImageInfo>()
    val imageInfo: LiveData<ImageGenImageInfo>
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
    private var _imageLibrary = MutableLiveData<List<ImageGenImageMetadata>>()
    val imageLibrary: LiveData<List<ImageGenImageMetadata>>
        get() = _imageLibrary


    /* _______ Generation Parameters ___________________________________________________ */

    // Applied prompt
    private var _prompt = MutableLiveData<String>()
    val prompt: LiveData<String>
        get() = _prompt

    // Applied negative prompt
    private val _negativePrompt = MutableLiveData(
        "(worst quality, low quality: 1.3), ((disfigured)), " +
                "((bad art)), ((deformed)), ((poorly drawn)), ((extra limbs)), " +
                "weird colors, blurry")
    val negativePrompt: LiveData<String>
        get() = _negativePrompt

    // Applied configuration scale
    private val _cfgScale = MutableLiveData(7.5)
    val cfgScale: LiveData<Double>
        get() = _cfgScale

    // Applied step count
    private var _steps = MutableLiveData(25)
    val steps: LiveData<Int>
        get() = _steps

    // Applied image width
    private var _width = MutableLiveData(512)
    val width: LiveData<Int>
        get() = _width

    // Applied image height
    private var _height = MutableLiveData(512)
    val height: LiveData<Int>
        get() = _height

    // Applied seed
    private val _seed = MutableLiveData<Long>(-1)
    val seed: LiveData<Long>
        get() = _seed

    // Applied sampler
    private val _sampler = MutableLiveData(ImageGenSampler("DPM++ 2S a Karras"))
    val sampler: LiveData<ImageGenSampler>
        get() = _sampler

    // Formatted text to image request data for api call
    private var _textToImageRequest = MutableLiveData<ImageGenTextToImageRequest>()


    /* _______ Image data _______________________________________________________________ */

    // Final image in Base64 format
    private var _finalImageBase64 = MutableLiveData<ImageGenTextToImageResponse>()
    val finalImageBase64: LiveData<ImageGenTextToImageResponse>
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
    private val _imageMetadata = MutableLiveData<ImageGenImageMetadata>()

    // Image saved state
    private val _imageSavedState = MutableLiveData<Boolean>()
    val imageSavedState: LiveData<Boolean>
        get() = _imageSavedState


    /* _______ Local Status ____________________________________________________________ */

    // Current app status for text to image request data
    private val _appStatusSetTextToImageRequest = MutableLiveData<AppStatus>()
    val appStatusSetTextToImageRequest: LiveData<AppStatus>
        get() = _appStatusSetTextToImageRequest

    // Current status of apply image metadata
    private val _appStatusApplyMetaData = MutableLiveData<AppStatus>()

    init {
        viewModelScope.launch {
            _imageLibrary.value = repository.getAllImages()
            loadOptions()
            loadSamplers()
            loadModels()
            _imageSavedState.value = false
        }
    }


    /* _______ Methods Generation Parameters ___________________________________________ */

    // Set current prompt
    fun setPrompt(newPrompt: String) {
        if (newPrompt != _prompt.value) {
            _prompt.value = newPrompt
        }
    }

    // Set current negative prompt
    fun setNegativePrompt(newNegativePrompt: String) {
        if (newNegativePrompt != _negativePrompt.value) {
            _negativePrompt.value = newNegativePrompt
        }
    }

    // Set current steps
    fun setSteps(newSteps: Int) {
        if (newSteps > 0 && newSteps != _steps.value) {
            _steps.value = newSteps
        }
    }

    // Set current CFG scale
    fun setCfgScale(newCfgScale: Double) {
        if (newCfgScale > 0.0 && newCfgScale != _cfgScale.value) {
            _cfgScale.value = newCfgScale
        }
    }

    // Set current width
    fun setWidth(newWidth: Int) {
        if (newWidth > 0 && newWidth != _width.value) {
            _width.value = newWidth
        }
    }

    // Set current height
    fun setHeight(newHeight: Int) {
        if (newHeight > 0 && newHeight != _height.value) {
            _height.value = newHeight
        }
    }

    // Set current seed
    fun setSeed(newSeed: Long) {
        if (newSeed >= (-1) && newSeed != seed.value) {
            _seed.value = newSeed
        }
    }

    // Set current Sampler
    fun setSampler(newSamplerName: String) {
        _sampler.value = ImageGenSampler(
            name = newSamplerName
        )
    }

    // Set current model, set options and update options value
    fun setModel(modelName: String) {
        val setModelMessage = "$modelName model successfully loaded."
        _apiStatusOptions.value = AppStatus.LOADING
        val newModel = _models.value!!.find { it.model_name == modelName }
        val newOptions = ImageGenOptions(
            sd_model_checkpoint = newModel!!.title
        )
        viewModelScope.launch {
            repository.setOptions(newOptions)
            _options.value = repository.getOptions()
            _models.value = repository.getModels()
            _apiStatusOptions.value = AppStatus.DONE
            Toast.makeText(app.applicationContext, setModelMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set and fill text to image request and update status
    fun setTextToImageRequest() {
        _appStatusSetTextToImageRequest.value = AppStatus.LOADING
        Log.e(
            TAG,
            "TextToImageRequest status:" +
                    "\n\tCHECK"
        )
        if (!_prompt.value.isNullOrEmpty()) {
            Log.e(
                TAG,
                "TextToImageRequest status:" +
                        "\n\tVALID"
            )
            Log.e(
                TAG,
                "TextToImageRequest status:" +
                        "\n\tSETTING"
            )
            _textToImageRequest.value = ImageGenTextToImageRequest(
                prompt = _prompt.value!!,
                seed = _seed.value!!,
                cfg_scale = _cfgScale.value!!,
                sampler_name = sampler.value!!.name,
                steps = _steps.value!!,
                width = _width.value!!,
                height = _height.value!!,
                negative_prompt = _negativePrompt.value!!
            )
            Log.e(
                TAG,
                "TextToImageRequest status:" +
                        "\n\tSET"
            )
            _appStatusSetTextToImageRequest.value = AppStatus.DONE
        } else {
            Log.e(
                TAG,
                "TextToImageRequest status:" +
                        "\n\tINVALID"
            )
            _appStatusSetTextToImageRequest.value = AppStatus.ERROR
        }
    }


    /* _______ Load Methods Remote _____________________________________________________ */

    // Load current options from server
    private fun loadOptions() {
        _apiStatusOptions.value = AppStatus.LOADING
        viewModelScope.launch {
            Log.e(
                TAG,
                "Option status:" +
                        "\n\tLOADING"
            )
            _options.value = repository.getOptions()
            Log.e(
                TAG,
                "Option status:" +
                        "\n\tLOADED"
            )
            _apiStatusOptions.value = AppStatus.DONE
            cancel()
        }
    }

    // Load all available models from server
    private fun loadModels() {
        viewModelScope.launch {
            delay(1000)
            try {
                Log.e(
                    TAG,
                    "Model status:" +
                            "\n\tLOADING"
                )
                _models.value = repository.getModels()
                Log.e(
                    TAG,
                    "Model status:" +
                            "\n\tLOADED"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error loading models:" +
                            "\n\t$e"
                )
            }
            cancel()
        }
    }

    // Load all available samplers from server
    private fun loadSamplers() {
        viewModelScope.launch {
            Log.e(
                TAG,
                "Samplers status:" +
                        "\n\tLOADING"
            )
            _samplersList.value = repository.getSamplers()
            Log.e(
                TAG,
                "Samplers status:" +
                        "\n\tLOADED"
            )
        }
    }

    // Start text to image request
    fun startTextToImageRequest() {
        _apiStatusTextToImg.value = AppStatus.LOADING
        viewModelScope.launch {
            _finalImageBase64.value = repository.startTextToImage(_textToImageRequest.value!!)
            _appStatusSetTextToImageRequest.value = AppStatus.WAITING
            _apiStatusTextToImg.value = AppStatus.DONE
            cancel()
            setImageSaved(true)
            loadImageInfo()
        }
    }

    // Load the progress of current text to image request
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
                            Log.e(
                                TAG,
                                "Error loading progress image:" +
                                        "\n\t$e"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "Error loading progress:" +
                                    "\n\t$e"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error loading progress loop:" +
                            "\n\t$e"
                )
            }
            _progress.value = ImageGenProgress(
                progress = 0.0,
                current_image = null
            )
            try {
                _progressImageBase64.value = _finalImageBase64.value!!.images.first()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error setting progress image:" +
                            "\n\t$e"
                )
            }
            cancel()
        }
    }

    // Load image info from final image
    private fun loadImageInfo() {
        viewModelScope.launch {
            _imageInfo.value = repository.getImageInfo(
                ImageGenImageBase64(
                    _finalImageBase64.value!!.images[0]
                )
            )
            applyImageMetadata()
            cancel()
        }
    }

    /* _______ Methods Local ___________________________________________________________ */

    // Decode Base64 image and set image value with bitmap image
    fun decodeImage(imageBase: String): Bitmap {
        val decodedByte = Base64.decode(imageBase, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    // Set image value to current image
    fun setImage(image: Bitmap) {
        _image.value = image
    }

    // Apply image metadata and update status
    private fun applyImageMetadata() {
        _appStatusApplyMetaData.value = AppStatus.LOADING
        var lastSeed: Long = -1
        if (_seed.value!! == lastSeed) {
            try {
                lastSeed = Regex("Seed: (\\d+)")
                    .find(_imageInfo.value!!.info)
                    ?.groupValues!![1].toLong()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error finding seed:" +
                            "\n\t$e"
                )
            }
        } else {
            lastSeed = _seed.value!!
        }

        try {
            _imageMetadata.value = ImageGenImageMetadata(
                seed = lastSeed,
                positivePrompt = _prompt.value!!,
                negativePrompt = _negativePrompt.value!!,
                image = _finalImageBase64.value!!.images[0],
                steps = _steps.value!!,
                size = "${_width.value}x${_height.value}",
                width = _width.value!!,
                height = _height.value!!,
                sampler = _sampler.value!!.name,
                CFGScale = _cfgScale.value!!,
                model = _options.value!!.sd_model_checkpoint,
                info = _imageInfo.value!!.info
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error applying image metadata:" +
                        "\n\t$e"
            )
            _appStatusApplyMetaData.value = AppStatus.ERROR
        }
        _appStatusApplyMetaData.value = AppStatus.DONE
    }

    // Save image metadata into database
    fun saveImage() {
        val saveMessage = "Image successfully added to the library!"
        viewModelScope.launch {
            try {
                Log.e(
                    TAG,
                    "Save image status:" +
                            "\n\tSAVING"
                )
                repository.insertImage(_imageMetadata.value!!)
                Log.e(
                    TAG,
                    "Save image status:" +
                            "\n\tSAVED"
                )
                setImageSaved(false)
                Toast.makeText(app.applicationContext, saveMessage, Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error saving image in database:" +
                            "\n\t$e"
                )
            }
            _imageLibrary.value = repository.getAllImages()
            cancel()
        }
    }

    // Delete selected image metadata from database
    fun deleteImage(imageID: Long) {
        val deleteMessage = "Image successfully deleted!"
        viewModelScope.launch {
            try {
                val imageToDelete = repository.getImageMetadata(imageID)
                try {
                    repository.deleteImage(imageToDelete)
                    Toast.makeText(app.applicationContext, deleteMessage, Toast.LENGTH_SHORT)
                        .show()
                    getAllImages()
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error deleting image from database:" +
                                "\n\t$e"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error getting image from database:" +
                            "\n\t$e"
                )
            }
        }
    }

    // Set image metadata for current image
    fun setImageMetadata(imageID: Long) {
        viewModelScope.launch {
            try {
                _imageMetadata.value = repository.getImageMetadata(imageID)
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error getting image from database:" +
                            "\n\t$e"
                )
            }
        }
    }

    // Check if image is already saved and set status accordingly
    private fun setImageSaved(state: Boolean) {
        _imageSavedState.value = state
    }

    // Set the size of ImageView dynamically
    fun setImageViewParams(imageView: ImageView) {
        if (_height.value!! > _width.value!!) {
            imageView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            imageView.layoutParams.height = 0
        } else {
            imageView.layoutParams.width = 0
            imageView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun getAllImages() {
        viewModelScope.launch {
            _imageLibrary.value = repository.getAllImages()
        }
    }


    /* _______ TextGen Image Generation ________________________________________________ */

    fun textGenRequest(prompt: String) {
        setPrompt(prompt)

        viewModelScope.launch {
            _finalImageBase64.value = repository.startTextToImage(
                ImageGenTextToImageRequest(
                    prompt = prompt,
                    seed = _seed.value!!,
                    sampler_name = _sampler.value!!.name,
                    cfg_scale = _cfgScale.value!!,
                    steps = _steps.value!!,
                    width = _width.value!!,
                    height = _height.value!!,
                    negative_prompt = _negativePrompt.value!!
                )
            )
        }
    }
}