package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryResponse(
    val query: String,
    val answers: List<TextGenHaystackQueryResponseAnswers>
)
