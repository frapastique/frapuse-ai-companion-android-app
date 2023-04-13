package com.back.frapuse

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.AppRepository
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val repository = AppRepository(TextToImageAPI)

    val imageBase64 = repository.image

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

    private var _textToImageRequest = MutableLiveData<TextToImageRequest>()
    val textToImageRequest: LiveData<TextToImageRequest>
        get() = _textToImageRequest

    private var _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    fun setPrompt(prompt: String) {
        _prompt.value = prompt
        checkData()
    }

    fun setSteps(steps: String) {
        _steps.value = steps.toInt()
        checkData()
    }

    fun setWidth(width: String) {
        _width.value = width.toInt()
        checkData()
    }

    fun setHeight(height: String) {
        _height.value = height.toInt()
        checkData()
    }

    private fun checkData() {
        if (prompt.value?.isNotBlank() == true && steps.value!! > 0 && width.value!! >= 248 && height.value!! >= 248) {
            setTextToImageRequest()
        }
    }

    private fun setTextToImageRequest() {
        _textToImageRequest.value = TextToImageRequest(
            prompt.value!!,
            steps.value!!,
            width.value!!,
            height.value!!
        )
    }

    fun loadTextToImage() {
        viewModelScope.launch {
            repository.getPrompt(textToImageRequest.value!!)
        }
    }

    fun decodeImage(imageBase64: TextToImage) {
        val decodedByte = Base64.decode(imageBase64.images[0], Base64.DEFAULT)
        _image.value = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }
}