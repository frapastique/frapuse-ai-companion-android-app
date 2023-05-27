package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryResponseAnswers(
    val answer: String,
    val type: String,
    val score: Double,
    val context: String,
    val offsets_in_document: List<TextGenHaystackOffset>,
    val offsets_in_context: List<TextGenHaystackOffset>,
    val document_ids: List<String>,
    val meta: TextGenHaystackQueryAnswerMeta
)
