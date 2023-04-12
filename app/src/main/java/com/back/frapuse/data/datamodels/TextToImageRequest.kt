package com.back.frapuse.data.datamodels

data class TextToImageRequest(
    val prompt: String,
    val steps: Int,
    val width: Int,
    val height: Int
)
