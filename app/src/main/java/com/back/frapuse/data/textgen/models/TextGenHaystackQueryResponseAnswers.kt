package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryResponseAnswers(
    val answer: String,
    val type: String,
    val score: Double,
    val context: String,
    val offsets_in_document: List<String>,
    val offsets_in_context: List<String>,
    val document_ids: String,
    val meta: List<TextGenHaystackMeta>,
    val documents: List<TextGenHaystackQueryResponseDocuments>
)
