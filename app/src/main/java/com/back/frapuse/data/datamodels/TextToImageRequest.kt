package com.back.frapuse.data.datamodels

data class TextToImageRequest(
    val prompt: String,
    val seed: Long = -1,
    val sampler_name: String = "Euler a",
    val cfg_scale: Double,
    val steps: Int,
    val width: Int,
    val height: Int,
    val negative_prompt: String
)
