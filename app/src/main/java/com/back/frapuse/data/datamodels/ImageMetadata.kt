package com.back.frapuse.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imageGenMetadata_table")
data class ImageMetadata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val seed: Long,
    val positivePrompt: String,
    val negativePrompt: String,
    val image: String,
    val steps: Int,
    val size: String,
    val width: Int,
    val height: Int,
    val sampler: String,
    val CFGScale: Double,
    val model: String,
    val modelHash: String,
    val info: String,
)
