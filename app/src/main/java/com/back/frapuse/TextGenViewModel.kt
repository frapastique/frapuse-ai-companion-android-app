package com.back.frapuse

import android.app.Application
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.TextGenRepository
import com.back.frapuse.data.datamodels.textgen.TextGenChatLibrary
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateRequest
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponse
import com.back.frapuse.data.datamodels.textgen.TextGenGenerateResponseText
import com.back.frapuse.data.datamodels.textgen.TextGenModelResponse
import com.back.frapuse.data.datamodels.textgen.TextGenPrompt
import com.back.frapuse.data.datamodels.textgen.TextGenTokenCountBody
import com.back.frapuse.data.local.getTextGenDatabase
import com.back.frapuse.data.remote.TextGenBlockAPI
import kotlinx.coroutines.launch
import java.util.Date

private const val TAG = "TextGenViewModel"

class TextGenViewModel(application: Application) : AndroidViewModel(application) {

    // Application context
    private val app = getApplication<Application>()

    // Database value
    private val database = getTextGenDatabase(application)

    // Initialize repository
    private val repository = TextGenRepository(TextGenBlockAPI, database)

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

    // Holder of token count
    private val _tokenResponseHolder = MutableLiveData<TextGenTokenCountBody>()
    val tokenResponseHolder: LiveData<TextGenTokenCountBody>
        get() = _tokenResponseHolder

    // Tokens count of given text
    private val _tokenCount = MutableLiveData<String>()
    val tokenCount: LiveData<String>
        get() = _tokenCount

    /* _______ Prompts _________________________________________________________________ */

    // Instructions prompt, tells the AI who it is and how to behave
    private val _instructionsPrompt = MutableLiveData<String>()
    val instructionsPrompt: LiveData<String>
        get() = _instructionsPrompt

    // Previous chat messages from human, gets inserted between instructions and next prompt
    private val _previousPromptHuman = MutableLiveData<String>()
    val previousPromptHuman: LiveData<String>
        get() = _previousPromptHuman

    // Previous chat messages from AI, gets inserted between instructions and next prompt
    private val _previousPromptAI = MutableLiveData<String>()
    val previousPromptAI: LiveData<String>
        get() = _previousPromptAI

    // Next prompt, gets inserted after final prompt and past nextPrompts
    private val _nextPrompt = MutableLiveData<String>()
    val nextPrompt: LiveData<String>
        get() = _nextPrompt

    // Prompt for text generation
    private val _prompt = MutableLiveData<TextGenPrompt>()
    val prompt: LiveData<TextGenPrompt>
        get() = _prompt

    /* _______ Api Status ______________________________________________________________ */

    // Api status
    private val _apiStatus = MutableLiveData<AppStatus>()
    val apiStatus: LiveData<AppStatus>
        get() = _apiStatus

    /* _______ Values Local ____________________________________________________________ */

    // Chat library
    private val _chatLibrary = MutableLiveData<List<TextGenChatLibrary>>()
    val chatLibrary: LiveData<List<TextGenChatLibrary>>
        get() = _chatLibrary


    init {
        setInstructionsPrompt("The following is a conversation with an AI research assistant. The assistant tone is technical and scientific.")
        setPreviousChatHuman("Human:Hello, who are you?")
        setPreviousChatAI("AI:Greetings! I am an AI research assistant. How can I help you today?")
        viewModelScope.launch {
            if (repository.getChatCount() == 0) {
                populateDB()
            }
            _chatLibrary.value = repository.getAllChats()
            checkTokensCount()
        }
    }


    /* _______ Generation Parameters ___________________________________________________ */

    fun setInstructionsPrompt(prompt: String) {
        _instructionsPrompt.value = prompt
    }

    fun setPreviousChatHuman(prompt: String) {
        _previousPromptHuman.value = prompt
    }

    fun setPreviousChatAI(prompt: String) {
        _previousPromptAI.value = prompt
    }

    fun setNextPrompt(prompt: String) {
        _nextPrompt.value = "Human: $prompt"
        viewModelScope.launch {
            val tokens = repository.getTokenCount(TextGenPrompt(prompt)).results.first().tokens
            repository.insertChat(
                TextGenChatLibrary(
                    dateTime = getDateTime(),
                    tokens = tokens,
                    name = "Human",
                    profilePicture = "",
                    message = prompt,
                    sentImage = ""
                )
            )
            _chatLibrary.value = repository.getAllChats()
            createFinalPrompt()
        }
    }

    fun createFinalPrompt() {
        val idTokenMap = mutableMapOf<Long, String>()
        var tokenCountCurrent = 0
        var prevPrompt = ""
        val newChatLibrary = mutableListOf<TextGenChatLibrary>()

        for (message in _chatLibrary.value!!) {
            Log.e(TAG, "Token count:\n\t$tokenCountCurrent")
            tokenCountCurrent += message.tokens.toInt()
            idTokenMap[message.chatID] = message.tokens
            prevPrompt += message.name + ": " + message.message + "\n"
        }

        if (tokenCountCurrent > 1500) {
            Log.e(TAG, "New token count:\n\t$tokenCountCurrent")
            prevPrompt = ""
            do {
                val firstEntry = idTokenMap.entries.first()
                Log.e(TAG, "Working count:\n\t$tokenCountCurrent")
                tokenCountCurrent -= firstEntry.value.toInt()
                idTokenMap.remove(firstEntry.key)
            } while (tokenCountCurrent > 1500)

            Log.e(TAG, "Latest token count:\n\t$tokenCountCurrent")

            viewModelScope.launch {
                for (entry in idTokenMap) {
                    val currentChat = repository.getChat(entry.key)
                    newChatLibrary.add(currentChat)
                }
                for (message in newChatLibrary) {
                    prevPrompt += message.name + ": " + message.message + "\n"
                }
                _prompt.value = TextGenPrompt(
                    prompt = _instructionsPrompt.value!! + "\n" +
                            prevPrompt + "AI:"
                )
                checkTokensCount()
                generateBlock(_prompt.value!!.prompt)
            }
        } else {
            _prompt.value = TextGenPrompt(
                prompt = _instructionsPrompt.value!! + "\n" +
                        prevPrompt + "AI:"
            )
            checkTokensCount()
            generateBlock(_prompt.value!!.prompt)
        }
    }

    private fun checkTokensCount() {
        if (_prompt.value == null) {
            var prevPrompt: String = _instructionsPrompt.value!! + "\n"
            for (message in _chatLibrary.value!!) {
                prevPrompt += message.name + ": " + message.message + "\n"
            }
            viewModelScope.launch {
                _tokenCount.value = repository.getTokenCount(
                    TextGenPrompt(
                        prevPrompt
                    )
                ).results.first().tokens
            }
        } else {
            viewModelScope.launch {
                _tokenCount.value = repository.getTokenCount(_prompt.value!!)
                    .results.first().tokens
            }
        }
    }

    fun generateBlock(prompt: String) {
        _apiStatus.value = AppStatus.LOADING
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
                _genResponseHolder.value = repository.generateBlockText(_genRequestBody.value!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading response holder: \n\t $e")
            }
            try {
                _genResponseText.value = _genResponseHolder.value!!.results.first()
                val tokens = repository.getTokenCount(TextGenPrompt(
                    _genResponseText.value!!.text.drop(1)
                )).results.first().tokens
                repository.insertChat(
                    TextGenChatLibrary(
                        dateTime = getDateTime(),
                        tokens = tokens,
                        name = "AI",
                        profilePicture = "",
                        message = _genResponseText.value!!.text.drop(1),
                        sentImage = ""
                    )
                )
                _chatLibrary.value = repository.getAllChats()
                _tokenCount.value = calculateTokens(_tokenCount.value!!, repository.getTokenCount(
                    TextGenPrompt(
                        "AI:" + _genResponseText.value!!.text
                    )
                ).results.first().tokens)
                _apiStatus.value = AppStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "Error loading response text from holder: \n\t $e")
            }
        }
    }

    fun getAllChats() {
        viewModelScope.launch {
            _chatLibrary.value = repository.getAllChats()
        }
    }

    fun calculateTokens(pre: String, add: String): String {
        val preInt = pre.toInt()
        val addInt = add.toInt()
        val new = preInt + addInt
        return new.toString()
    }

    fun deleteChatLibrary() {
        viewModelScope.launch {
            repository.deleteAllChats()
            _chatLibrary.value = repository.getAllChats()
            populateDB()
            _chatLibrary.value = repository.getAllChats()
        }
    }

    private fun populateDB() {
        viewModelScope.launch {
            var tokens = repository.getTokenCount(
                TextGenPrompt("Human: Hello, who are you?")
            ).results.first().tokens
            repository.insertChat(TextGenChatLibrary(
                dateTime = getDateTime(),
                tokens = tokens,
                name = "Human",
                profilePicture = "",
                message = "Hello, who are you?",
                sentImage = ""
            ))
            tokens = repository.getTokenCount(
                TextGenPrompt("AI: Greetings! I am an AI research assistant. How can I help you today?")
            ).results.first().tokens
            repository.insertChat(TextGenChatLibrary(
                dateTime = getDateTime(),
                tokens = tokens,
                name = "AI",
                profilePicture = "",
                message = "Greetings! I am an AI research assistant. How can I help you today?",
                sentImage = ""
            ))
            _chatLibrary.value = repository.getAllChats()
            checkTokensCount()
        }
    }

    fun getDateTime(): String {
        val sdf = SimpleDateFormat("dd.MM.yy - hh:mm:ss")
        return sdf.format(Date())
    }
}