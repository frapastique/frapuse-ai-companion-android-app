package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryDocumentsMeta(
    val _split_id: String,
    val _split_overlap: List<TextGenHaystackOverlap>,
    val name: String
)
