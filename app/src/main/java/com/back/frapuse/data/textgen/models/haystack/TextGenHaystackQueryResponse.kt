package com.back.frapuse.data.textgen.models.haystack

data class TextGenHaystackQueryResponse(
    val query: String,
    val answers: List<TextGenHaystackQueryResponseAnswers>,
    val documents: List<TextGenHaystackQueryResponseDocuments>
)
