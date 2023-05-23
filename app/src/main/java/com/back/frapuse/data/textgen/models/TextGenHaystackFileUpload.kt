package com.back.frapuse.data.textgen.models

import java.io.File

data class TextGenHaystackFileUpload(
    val files: File,
    val meta: String,
    val additional_params: String,
    val remove_numeric_tables: Boolean,
    val valid_languages: List<String>,
    val clean_whitespace: Boolean,
    val clean_empty_lines: Boolean,
    val clean_header_footer: Boolean,
    val split_by: String,
    val split_length: Int,
    val split_overlap: Int,
    val split_respect_sentence_boundary: Boolean
)