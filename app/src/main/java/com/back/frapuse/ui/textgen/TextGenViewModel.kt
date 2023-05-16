package com.back.frapuse.ui.textgen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.back.frapuse.data.textgen.TextGenRepository
import com.back.frapuse.data.textgen.models.TextGenChatLibrary
import com.back.frapuse.data.textgen.models.TextGenGenerateRequest
import com.back.frapuse.data.textgen.models.TextGenGenerateResponse
import com.back.frapuse.data.textgen.models.TextGenGenerateResponseText
import com.back.frapuse.data.textgen.models.TextGenModelResponse
import com.back.frapuse.data.textgen.models.TextGenPrompt
import com.back.frapuse.data.textgen.local.getTextGenDatabase
import com.back.frapuse.data.textgen.remote.TextGenBlockAPI
import com.back.frapuse.util.AppStatus
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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

    // Text generation response holds a list of text
    private val _genResponseHolder = MutableLiveData<TextGenGenerateResponse>()

    // Text response from genResponseHolder
    private val _genResponseText = MutableLiveData<TextGenGenerateResponseText>()

    // Tokens count of given text
    private val _tokenCount = MutableLiveData<String>()
    val tokenCount: LiveData<String>
        get() = _tokenCount

    // LiveData stream response object
    val streamResponseMessage = repository.streamResponseMessage

    /* _______ Prompts _________________________________________________________________ */

    // Instructions prompt, tells the AI who it is and how to behave
    private val _instructionsContext = MutableLiveData<String>()
    private val _instructionContextTokenCount = MutableLiveData<String>()

    // Previous chat messages from human, gets inserted between instructions and next prompt
    private val _humanContext = MutableLiveData<String>()

    // Previous chat messages from AI, gets inserted between instructions and next prompt
    private val _aiContext = MutableLiveData<String>()

    // Prompt for text generation
    private val _prompt = MutableLiveData<TextGenPrompt>()
    val prompt: LiveData<TextGenPrompt>
        get() = _prompt

    // Count of tokens
    private var _count = MutableLiveData<String>()
    val count: LiveData<String>
        get() = _count

    // Extracted text with OCR from bitmap
    private var _textOut = MutableLiveData<String>()

    /* _______ Api Status ______________________________________________________________ */

    // Api status
    private val _apiStatus = MutableLiveData<AppStatus>()
    val apiStatus: LiveData<AppStatus>
        get() = _apiStatus

    // Status of creating the final prompt
    private val _createPromptStatus = MutableLiveData<AppStatus>()
    val createPromptStatus: LiveData<AppStatus>
        get() = _createPromptStatus

    /* _______ Values Local ____________________________________________________________ */

    // Chat library
    private val _chatLibrary = MutableLiveData<List<TextGenChatLibrary>>()
    val chatLibrary: LiveData<List<TextGenChatLibrary>>
        get() = _chatLibrary

    // Current chat
    private val _currentChatMessage = MutableLiveData<TextGenChatLibrary>()
    val currentChatMessage: LiveData<TextGenChatLibrary>
        get() = _currentChatMessage

    // LiveData variable to hold the pdf file path
    private val _pdfPath = MutableLiveData<String>()
    val pdfPath: LiveData<String>
        get() = _pdfPath

    // PDF as bitmap
    private var _pdfBitmap = MutableLiveData<Bitmap>()

    // create a contract for picking a PDF file
    private val pickPdfContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = input
            }
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
        }
    }

    // create a launcher variable to hold the ActivityResultLauncher
    private var pickPdfLauncher: ActivityResultLauncher<String>? = null

    init {
        viewModelScope.launch {
            getModel()
            _chatLibrary.value = repository.getAllChats()
            if (repository.getChatCount() == 0) {
                setInstructionsContext(
                    "A chat between a curious user and an artificial intelligence" +
                            " assistant. The assistant gives helpful, detailed, and " +
                            "polite answers to the user's questions."
                )
            } else {
                _instructionsContext.value = _chatLibrary.value!!.first().message
                _instructionContextTokenCount.value = _chatLibrary.value!!.first().tokens
                createFinalPrompt()
            }
            checkTokensCount()
        }
    }


    /* _______ Generation Parameters ___________________________________________________ */

    private fun setInstructionsContext(instruction: String) {
        _instructionsContext.value = "$instruction "
        _prompt.value = TextGenPrompt(
            prompt = _instructionsContext.value!!
        )
        viewModelScope.launch {
            _instructionContextTokenCount.value = repository.getTokenCount(
                TextGenPrompt(
                    _instructionsContext.value!!
                )
            )
                .results.first().tokens

            repository.insertChat(
                TextGenChatLibrary(
                    conversationID = 0,
                    dateTime = getDateTime(),
                    modelName = "",
                    tokens = _instructionContextTokenCount.value!!,
                    type = "Instructions",
                    message = instruction,
                    sentImage = "",
                    sentDocument = "",
                    documentText = "",
                    finalContext = _instructionsContext.value!!
                )
            )

            _chatLibrary.value = repository.getAllChats()
        }
    }

    fun setHumanContext(message: String, filePath: String) {
        _apiStatus.value = AppStatus.LOADING
        _createPromptStatus.value = AppStatus.LOADING

        if (filePath.isEmpty()) {
            _humanContext.value = "USER: $message "
        } else {
            _humanContext.value = "USER: ${message}Context: ${_textOut.value.toString()} "
        }

        viewModelScope.launch {
            val tokens = repository.getTokenCount(
                TextGenPrompt(
                    _humanContext.value!!
                )
            )
                .results.first().tokens

            repository.insertChat(
                TextGenChatLibrary(
                    conversationID = 0,
                    dateTime = getDateTime(),
                    modelName = "",
                    tokens = tokens,
                    type = "Human",
                    message = message,
                    sentImage = "",
                    sentDocument = filePath,
                    documentText = _textOut.value.toString(),
                    finalContext = _humanContext.value!!
                )
            )

            _chatLibrary.value = repository.getAllChats()
            createFinalPrompt()
            _createPromptStatus.value = AppStatus.DONE
        }
    }

    fun saveAttachment(filePath: String) {
        viewModelScope.launch {
            try {
                repository.insertChat(
                    TextGenChatLibrary(
                        conversationID = 0,
                        dateTime = getDateTime(),
                        modelName = "",
                        tokens = _count.value!!,
                        type = "Human Attachment",
                        message = "",
                        sentImage = "",
                        sentDocument = filePath,
                        documentText = _textOut.value.toString(),
                        finalContext = ""
                    )
                )
                _textOut.value = ""
            } catch (e: Exception) {
                Log.e(TAG, "Error saving Attachment:\n\t$e")
            }

        }
    }

    private fun setAIContext(prompt: String) {
        _aiContext.value = prompt
    }

    // Final prompt creator
    private fun createFinalPrompt() {
        var prevPrompt = _instructionsContext.value!!
        var tokenCountCurrent = _instructionContextTokenCount.value!!.toInt()
        val currentChatLibrary = _chatLibrary.value!!.toMutableList()
            .filter { it.type == "Human" || it.type == "AI" }

        // Take name, message and if file is provided also the extracted text and construct the prompt
        for (message in currentChatLibrary) {
            tokenCountCurrent += message.tokens.toInt()
            if (tokenCountCurrent > 1700) {
                Log.e(TAG, "Current token count:\n\t$tokenCountCurrent")
                do {
                    tokenCountCurrent -= currentChatLibrary.first().tokens.toInt()
                    currentChatLibrary.drop(1)
                    Log.e(TAG, "New token count:\n\t$tokenCountCurrent")
                } while (tokenCountCurrent > 1700)
            }
        }

        for (message in currentChatLibrary)  {
            prevPrompt += message.finalContext
        }

        _prompt.value = TextGenPrompt(
            prompt = prevPrompt + "ASSISTANT:"
        )
        checkTokensCount()
    }

    // Get the name of loaded AI model from API
    private fun getModel() {
        viewModelScope.launch {
            _model.value = repository.getModel()
        }
    }

    // Check token count
    private fun checkTokensCount() {
        when (_prompt.value) {
            null -> _tokenCount.value = "Tokens"
            else -> {
                viewModelScope.launch {
                    _tokenCount.value = repository.getTokenCount(_prompt.value!!)
                        .results.first().tokens
                }
            }
        }
    }

    fun generateStream() {
        _apiStatus.value = AppStatus.LOADING
        _createPromptStatus.value = AppStatus.WAITING

        viewModelScope.launch {
            repository.insertChat(
                TextGenChatLibrary(
                    conversationID = 0,
                    dateTime = getDateTime(),
                    modelName = _model.value!!.result,
                    tokens = "1",
                    type = "AI",
                    message = "",
                    sentImage = "",
                    sentDocument = "",
                    documentText = "",
                    finalContext = ""
                )
            )
            _chatLibrary.value = repository.getAllChats()

            _genRequestBody.value = TextGenGenerateRequest(
                prompt = _prompt.value!!.prompt,
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

            repository.sendMessageToWebSocket(_genRequestBody.value!!)
        }
    }

    fun resetStream() {
        repository.resetStreamResponseMessage()
        _apiStatus.value = AppStatus.DONE
    }

    fun updateChat(messageID: Long, message: String) {
        Log.e(TAG, "Latest response:\n\t$message")
        _aiContext.value = "ASSISTANT: ${message.drop(1)} "
        viewModelScope.launch {
            val tokens = repository.getTokenCount(
                TextGenPrompt(message.drop(1))
            ).results.first().tokens
            repository.updateChat(
                TextGenChatLibrary(
                    ID = messageID,
                    conversationID = 0,
                    dateTime = getDateTime(),
                    modelName = _model.value!!.result,
                    tokens = tokens,
                    type = "AI",
                    message = message.drop(1),
                    sentImage = "",
                    sentDocument = "",
                    documentText = "",
                    finalContext = _aiContext.value!!
                )
            )
            _chatLibrary.value = repository.getAllChats()
            _tokenCount.value = (_tokenCount.value!!.toInt() + tokens.toInt()).toString()
        }
    }

    // Method to send final prompt to server and generate block response. After receiving response
    // clean and place response into chat library
    fun generateBlock() {
        _apiStatus.value = AppStatus.LOADING
        _createPromptStatus.value = AppStatus.WAITING
        viewModelScope.launch {
            _genRequestBody.value = TextGenGenerateRequest(
                prompt = _prompt.value!!.prompt,
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
                val tokens = repository.getTokenCount(
                    TextGenPrompt(
                    _genResponseText.value!!.text.drop(1)
                )
                ).results.first().tokens
                repository.insertChat(
                    TextGenChatLibrary(
                        conversationID = 0,
                        dateTime = getDateTime(),
                        modelName = _model.value!!.result,
                        tokens = tokens,
                        type = "AI",
                        message = _genResponseText.value!!.text.drop(1),
                        sentImage = "",
                        sentDocument = "",
                        documentText = "",
                        finalContext = ""
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

    // Fill the chat library from database
    fun getAllChats() {
        viewModelScope.launch {
            _chatLibrary.value = repository.getAllChats()
        }
    }

    // Method to calculate token count dedicated to createFinalPrompt method
    private fun calculateTokens(pre: String, add: String): String {
        val preInt = pre.toInt()
        val addInt = add.toInt()
        val new = preInt + addInt
        return new.toString()
    }

    // Method to get current message from chat library
    fun setCurrentChatMessage(messageID: Long) {
        viewModelScope.launch {
            _currentChatMessage.value = repository.getChat(messageID)
        }
    }

    // Method to clear chat library and populate with base entries
    fun deleteChatLibrary() {
        viewModelScope.launch {
            repository.deleteAllChats()
            setInstructionsContext(
                "A chat between a curious user and an artificial intelligence" +
                        " assistant. The assistant gives helpful, detailed, and " +
                        "polite answers to the user's questions."
            )
            _chatLibrary.value = repository.getAllChats()
        }
    }

    // Get the date and time of given moment
    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(): String {
        val sdf = SimpleDateFormat("dd.MM.yy - hh:mm:ss")
        return sdf.format(Date())
    }

    // Method to register the pdf contract and get the launcher
    fun registerPickPdfContract(registry: ActivityResultRegistry) {
        pickPdfLauncher = registry.register("pickPdf", pickPdfContract) { uri ->
            // handle the URI of the selected file
            if (uri != null) {
                // call the function to create a local PDF file and pass the URI and context
                val filePath = createLocalPdfFile(uri, app.applicationContext)
                // update the LiveData variable with the file path
                _pdfPath.value = filePath
            }
        }
    }

    // Method to launch the file picker
    fun launchPickPdf() {
        pickPdfLauncher?.launch("application/pdf")
    }

    // Method to create a local PDF file from a URI and return its path
    @SuppressLint("Range")
    private fun createLocalPdfFile(uri: Uri, context: Context): String {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (fileName == null) {
            fileName = uri.path
            val cut = fileName?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    fileName = fileName?.substring(cut + 1)
                }
            }
        }

        // get the app-specific internal storage directory
        val dir = context.filesDir
        // create a subdirectory for PDF files
        val pdfDir = File(dir, "pdf")
        pdfDir.mkdirs()
        // create a file with a unique name
        val file = File(pdfDir, fileName.toString())
        // copy the content of the URI to the file
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        // close the streams
        inputStream?.close()
        outputStream.close()
        // get and return the file path
        return file.path
    }

    // Clear out file path
    fun resetPdfPath() {
        _pdfPath.value = ""
    }

    // Delete all saved PDFs from directory
    fun deleteAllPdf(context: Context) {
        try {
            val dir = context.filesDir
            val pdfDir = File(dir, "pdf")
            val files = pdfDir.listFiles()

            for (file in files!!) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting files:\n\t$e")
        }
    }

    // Process extracted text block, build a string and set textOut value
    private fun processTextBlock(result: Text) {
        val textBlocks = result.textBlocks
        if (textBlocks.size == 0) {
            _textOut.value = "No text found"
        }
        val stringBuilder = StringBuilder()
        for (block in textBlocks) {
            stringBuilder.append("\n\n")
            val lines = block.lines
            for (line in lines) {
                val elements = line.elements
                for (element in elements) {
                    val elementText = element.text
                    stringBuilder.append("$elementText ")
                }
            }
        }
        _textOut.value = stringBuilder.toString()
    }

    // Method to get token count dedicated for extracted text
    fun getTokenCount(text: String) {
        viewModelScope.launch {
            _count.value = repository.getTokenCount(TextGenPrompt(prompt = text)).results.first().tokens
        }
    }

    // Method to extract the text from a bitmap. Process the text block and get token count on success
    fun extractText() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(_pdfBitmap.value!!, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                processTextBlock(visionText)
                getTokenCount(_textOut.value!!)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error extracting text:\n\t$e")
            }
    }

    // Set the current pdf page as bitmap
    fun setPdfBitmap(image: Bitmap) {
        _pdfBitmap.value = image
    }
}