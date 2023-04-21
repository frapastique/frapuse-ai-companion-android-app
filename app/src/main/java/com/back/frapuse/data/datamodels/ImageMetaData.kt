package com.back.frapuse.data.datamodels

data class ImageMetaData(
    val id: Long,
    val positivePrompt: String,
    val negativePrompt: String,
    val image: ImageBase64,
    val steps: Int,
    val size: String,
    val width: Int,
    val height: Int,
    val sampler: String,
    val CFGScale: Double,
    val seed: Long,
    val model: String,
    val modelHash: String
)
