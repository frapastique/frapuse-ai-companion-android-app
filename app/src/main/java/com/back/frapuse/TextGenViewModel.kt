package com.back.frapuse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.TextGenRepository
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponseText
import com.back.frapuse.data.datamodels.textgen.TextGenModelResponse
import com.back.frapuse.data.datamodels.textgen.TextGenPrompt
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountBody
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountResponse
import com.back.frapuse.data.remote.TextGenBlockAPI

private const val TAG = "TextGenViewModel"

class TextGenViewModel(application: Application) : AndroidViewModel(application) {

    // Application context
    private val app = getApplication<Application>()

    // Initialize repository
    private val repository = TextGenRepository(TextGenBlockAPI)

    /* _______ Values Remote ___________________________________________________________ */

    // Current loaded model
    private val _model = MutableLiveData<TextGenModelResponse>()
    val model: LiveData<TextGenModelResponse>
        get() = _model

    // Text generation parameters body
    private val _genRequestBody = MutableLiveData<TextGenGenerateRequest>()
    val genRequestBody: LiveData<TextGenGenerateRequest>
        get() = _genRequestBody

    // Text generation response holds a list of text
    private val _genResponseHolder = MutableLiveData<TextGenGenerateResponse>()
    val genResponseHolder: LiveData<TextGenGenerateResponse>
        get() = _genResponseHolder

    // Text response from genResponseHolder
    private val _genResponseText = MutableLiveData<TextGenGenerateResponseText>()
    val genResponseText: LiveData<TextGenGenerateResponseText>
        get() = _genResponseText

    // Prompt for text generation
    private val _prompt = MutableLiveData<TextGenPrompt>()
    val prompt: LiveData<TextGenPrompt>
        get() = _prompt

    // Holder of token count
    private val _tokenResponseHolder = MutableLiveData<TextGenTokenCountBody>()
    val tokenResponseHolder: LiveData<TextGenTokenCountBody>
        get() = _tokenResponseHolder

    // Tokens count of given text
    private val _tokenCount = MutableLiveData<TextGenTokenCountResponse>()
    val tokenCount: LiveData<TextGenTokenCountResponse>
        get() = _tokenCount
}