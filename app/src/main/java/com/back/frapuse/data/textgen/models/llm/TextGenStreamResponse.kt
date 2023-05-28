package com.back.frapuse.data.textgen.models.llm

data class TextGenStreamResponse(
    val event: String,
    val message_num: Long,
    var text: String = ""
)
