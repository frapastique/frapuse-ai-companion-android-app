package com.back.frapuse.data.textgen.models

data class TextGenHaystackMeta(
    val author: String,
    val topic: List<String>,
    val title: String,
    val type: String,
    val name: String
)
