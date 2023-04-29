package com.back.frapuse

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.TextGenRepository
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponseText
import com.back.frapuse.data.datamodels.textgen.TextGenModelResponse
import com.back.frapuse.data.datamodels.textgen.TextGenPrompt
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountBody
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountResponse
import com.back.frapuse.data.remote.TextGenBlockAPI
import kotlinx.coroutines.launch

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


    /* _______ Generation Parameters ___________________________________________________ */

    fun test(prompt: String) {
        viewModelScope.launch {
            _genRequestBody.value = TextGenGenerateRequest(
                prompt = prompt,
                max_new_tokes = 250,
                do_sample = true,
                temperature = 1.3,
                top_p = 0.1,
                typical_p = 1.0,
                repetition_penalty = 1.18,
                top_k = 40,
                min_length = 0,
                no_repeat_ngram_size = 0,
                num_beams = 1,
                penalty_alpha = 0.0,
                length_penalty = 1.0,
                early_stopping = false,
                seed = -1,
                add_bos_token = true,
                truncation_length = 2048,
                ban_eos_token = false,
                skip_special_tokens = true,
                stopping_strings = listOf()
            )
            try {
                _genResponseHolder.value = repository.generateText(_genRequestBody.value!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading response holder: \n\t $e")
            }
            try {
                _genResponseText.value = _genResponseHolder.value!!.results.first()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading response text from holder: \n\t $e")
            }
        }
    }
}