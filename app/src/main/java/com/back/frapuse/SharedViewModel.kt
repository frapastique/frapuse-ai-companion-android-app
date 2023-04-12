package com.back.frapuse

import android.graphics.Bitmap
import android.media.Image
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.AppRepository
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val repository = AppRepository(TextToImageAPI)

    val imageBase64 = repository.image

    private var _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    fun loadPrompt(prompt: String) {
        viewModelScope.launch {
            repository.getPrompt(prompt, 5, 256, 256)
        }
    }

    fun decodeImage(imageBase64: TextToImage) {
        TODO("Convert Base64 image to image")
    }
}