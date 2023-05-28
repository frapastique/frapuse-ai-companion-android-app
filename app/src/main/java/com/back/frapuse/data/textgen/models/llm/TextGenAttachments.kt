package com.back.frapuse.data.textgen.models.llm

data class TextGenAttachments(
    val id: Long,
    val path: String,
    var pageCount: Int
)
