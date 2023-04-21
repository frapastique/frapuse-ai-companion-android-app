package com.back.frapuse

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.ImageGenerationRepository
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.launch

private const val TAG = "ImageGenerationViewModel"

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

class ImageGenerationViewModel : ViewModel() {

    private val repository = ImageGenerationRepository(TextToImageAPI)

    val imageBase64 = repository.image

    val models = repository.models.value

    val options = repository.options

    private var _prompt = MutableLiveData<String>()
    val prompt: LiveData<String>
        get() = _prompt

    private var _steps = MutableLiveData<Int>()
    val steps: LiveData<Int>
        get() = _steps

    private var _width = MutableLiveData<Int>()
    val width: LiveData<Int>
        get() = _width

    private var _height = MutableLiveData<Int>()
    val height: LiveData<Int>
        get() = _height

    val generationData: LiveData<Quadruple<String, Int, Int, Int>> =
        MediatorLiveData<Quadruple<String, Int, Int, Int>>().apply {
            var setPrompt: String? = null
            var setSteps: Int? = null
            var setWidth: Int? = null
            var setHeight: Int? = null

            addSource(prompt) { promptVal ->
                setPrompt = promptVal
                if (setSteps != null && setWidth != null && setHeight != null) {
                    value = Quadruple(setPrompt!!, setSteps!!, setWidth!!, setHeight!!)
                }
            }

            addSource(steps) { stepsVal ->
                setSteps = stepsVal
                if (setPrompt != null && setWidth != null && setHeight != null) {
                    value = Quadruple(setPrompt!!, setSteps!!, setWidth!!, setHeight!!)
                }
            }

            addSource(width) { widthVal ->
                setWidth = widthVal
                if (setPrompt != null && setSteps != null && setHeight != null) {
                    value = Quadruple(setPrompt!!, setSteps!!, setWidth!!, setHeight!!)
                }
            }

            addSource(height) { heightVal ->
                setHeight = heightVal
                if (setPrompt != null && setSteps != null && setWidth != null) {
                    value = Quadruple(setPrompt!!, setSteps!!, setWidth!!, setHeight!!)
                }
            }

            if (setPrompt != null && setSteps != null && setWidth != null && setHeight != null) {
                value = Quadruple(setPrompt!!, setSteps!!, setWidth!!, setHeight!!)
            }
        }

    private var _textToImageRequest = MutableLiveData<TextToImageRequest>()
    val textToImageRequest: LiveData<TextToImageRequest>
        get() = _textToImageRequest

    private var _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    private var _progress = MutableLiveData<Double>()
    val progress: LiveData<Double>
        get() = _progress

    fun setPrompt(prompt: String) {
        _prompt.value = prompt
    }

    fun setSteps(steps: String) {
        if (steps.isNotEmpty()) {
            _steps.value = steps.toInt()
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

    fun setTextToImageRequest(prompt: String, steps: Int, width: Int, height: Int) {
        _textToImageRequest.value = TextToImageRequest(
            prompt,
            steps,
            width,
            height
        )
    }

    fun loadTextToImage() {
        viewModelScope.launch {
            repository.startTextToImage(textToImageRequest.value!!)
            try {
                do {
                    repository.getProgress()
                    try {
                        _progress.value = repository.progress.value
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading progress: $e")
                    }
                } while (progress.value!! < 1.00)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress loop: $e")
            }
        }
    }

    fun decodeImage(imageBase64: TextToImage) {
        val decodedByte = Base64.decode(imageBase64.images[0], Base64.DEFAULT)
        _image.value = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun loadOptions() {
        viewModelScope.launch {
            repository.getOptions()
        }
    }
}