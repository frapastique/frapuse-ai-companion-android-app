package com.back.frapuse

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
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
        val decodedByte = Base64.decode(imageBase64.images[0], Base64.DEFAULT)
        _image.value = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }
}