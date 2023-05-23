package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryResponseDocuments(
    val id: String,
    val content: String,
    val content_type: String,
    val meta: List<TextGenHaystackMeta>,
    val score: Double
)
