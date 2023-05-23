package com.back.frapuse.data.textgen.models

data class TextGenHaystackFilterDocumentsResponse(
    val id: String,
    val content: String,
    val meta: TextGenHaystackMeta,
    val score: Double,
    val embedding: String
)
