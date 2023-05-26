package com.back.frapuse.data.textgen.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextGenHaystackFilterDocumentsRequest(
    var filters: String? = null
)
