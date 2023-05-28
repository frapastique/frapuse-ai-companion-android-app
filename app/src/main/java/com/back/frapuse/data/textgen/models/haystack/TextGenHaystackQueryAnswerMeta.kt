package com.back.frapuse.data.textgen.models.haystack

data class TextGenHaystackQueryAnswerMeta(
    val _split_id: String,
    val _split_overlap: List<TextGenHaystackOverlap>,
    val name: String
)
