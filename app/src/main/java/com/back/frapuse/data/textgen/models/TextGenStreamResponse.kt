package com.back.frapuse.data.textgen.models

data class TextGenStreamResponse(
    val event: String,
    val message_num: Long,
    var text: String = ""
)
