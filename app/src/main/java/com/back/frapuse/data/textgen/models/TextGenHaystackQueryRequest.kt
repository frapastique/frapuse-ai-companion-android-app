package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryRequest(
    val query: String,
    var params: String = "{}",
    var debug: Boolean = false
)
