package com.back.frapuse.data.textgen.models

data class TextGenHaystackQueryResponseDocuments(
    val id: String,
    val content: String,
    val content_type: String,
    val meta: TextGenHaystackQueryDocumentsMeta,
    val id_hash_keys: List<String>,
    val score: Double
)
