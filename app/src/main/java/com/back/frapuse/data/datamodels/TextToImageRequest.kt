package com.back.frapuse.data.datamodels

data class TextToImageRequest(
    val prompt: String,
    val cfg_scale: Int,
    val steps: Int,
    val width: Int,
    val height: Int,
    val negative_prompt: String
)
