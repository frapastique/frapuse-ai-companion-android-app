package com.back.frapuse.data.imagegen.models

data class ImageGenTextToImageRequest(
    val prompt: String,
    val seed: Long,
    val sampler_name: String,
    val cfg_scale: Double,
    val steps: Int,
    val width: Int,
    val height: Int,
    val negative_prompt: String
)
