package com.back.frapuse.data.textgen.models.haystack

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextGenHaystackMeta(
    val author: String,
    val summary: String,
    val topic: List<String>,
    val title: String,
    val type: String,
    val name: String
)
