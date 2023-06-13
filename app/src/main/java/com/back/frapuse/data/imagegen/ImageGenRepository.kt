package com.back.frapuse.data.imagegen

import android.util.Log
import com.back.frapuse.data.imagegen.models.ImageGenImageBase64
import com.back.frapuse.data.imagegen.models.ImageGenImageInfo
import com.back.frapuse.data.imagegen.models.ImageGenImageMetadata
import com.back.frapuse.data.imagegen.models.ImageGenOptions
import com.back.frapuse.data.imagegen.models.ImageGenProgress
import com.back.frapuse.data.imagegen.models.ImageGenSDModel
import com.back.frapuse.data.imagegen.models.ImageGenSampler
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageResponse
import com.back.frapuse.data.imagegen.models.ImageGenTextToImageRequest
import com.back.frapuse.data.imagegen.local.ImageGenDatabase
import com.back.frapuse.data.imagegen.remote.ImageGenAPI

private const val TAG = "ImageGenRepository"

class ImageGenRepository(private val api: ImageGenAPI, private val database: ImageGenDatabase) {

    /* _______ Methods Remote __________________________________________________________ */

    suspend fun getModels(): List<ImageGenSDModel> {
        return try {
            api.retrofitService.getModels()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading models from API:" +
                        "\n\t$e"
            )
            listOf()
        }
    }

    suspend fun getOptions(): ImageGenOptions {
        return try {
            api.retrofitService.getOptions()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading options from API:" +
                        "\n\t$e"
            )
            ImageGenOptions(
                sd_model_checkpoint = ""
            )
        }
    }

    suspend fun startTextToImage(textToImageRequest: ImageGenTextToImageRequest): ImageGenTextToImageResponse {
        return try {
            api.retrofitService.startTextToImage(textToImageRequest)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading Data from API:" +
                        "\n\t$e"
            )
            ImageGenTextToImageResponse(
                listOf()
            )
        }
    }

    suspend fun getProgress(): ImageGenProgress {
        return try {
            api.retrofitService.getProgress()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading progress from API:" +
                        "\n\t$e"
            )
            ImageGenProgress(
                progress = 0.0,
                current_image = null
            )
        }
    }

    suspend fun setOptions(options: ImageGenOptions) {
        try {
            api.retrofitService.setOptions(options)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error setting options:" +
                        "\n\t$e"
            )
        }
    }

    suspend fun getImageInfo(imageBase64: ImageGenImageBase64): ImageGenImageInfo {
        return try {
            api.retrofitService.getImageMetaData(imageBase64)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading image info from API:" +
                        "\n\t$e"
            )
            ImageGenImageInfo(
                info = ""
            )
        }
    }

    suspend fun getSamplers(): List<ImageGenSampler> {
        return try {
            api.retrofitService.getSamplers()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error loading samplers from API:" +
                        "\n\t$e"
            )
            listOf()
        }
    }

    /* _______ Methods Local ___________________________________________________________ */

    /**
     * Insert new image metadata
     * @param ImageMetadata Image metadata which gets inserted
     * */
    suspend fun insertImage(ImageMetadata: ImageGenImageMetadata) {
        try {
            database.imageGenDao.insertImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error inserting image in 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Delivers all images from the 'imageGenMetadata_table' database
     * @return List -> ImageMetadata
     * */
    suspend fun getAllImages(): List<ImageGenImageMetadata> {
        return try {
            database.imageGenDao.getAllImages()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting all images from 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
            listOf()
        }
    }

    /**
     * Update metadata of existing image
     * @param ImageMetadata Image metadata which gets updated
     * */
    suspend fun updateImage(ImageMetadata: ImageGenImageMetadata) {
        try {
            database.imageGenDao.updateImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error updating image in 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Get size of image database
     * @return Int
     * */
    suspend fun getImageCount(): Int {
        return try {
            database.imageGenDao.getImageCount()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error getting the size from 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Delete a selected image metadata from database
     * @param ImageMetadata Image metadata which gets deleted
     * */
    suspend fun deleteImage(ImageMetadata: ImageGenImageMetadata) {
        try {
            database.imageGenDao.deleteImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting image from 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Delete all entries from database
     * */
    suspend fun deleteAllImages() {
        try {
            database.imageGenDao.deleteAllImages()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error deleting all images from 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
        }
    }

    /**
     * Get selected image from database
     * @param imageID ID of the wanted image
     * @return ImageMetadata
     * */
    suspend fun getImageMetadata(imageID: Long): ImageGenImageMetadata {
        return try {
            database.imageGenDao.getImageMetadata(imageID)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error fetching image from 'imageGenMetadata_table':" +
                        "\n\t$e"
            )
            ImageGenImageMetadata(
                seed = 0,
                positivePrompt = "",
                negativePrompt = "",
                image = "",
                steps = 0,
                size = "",
                width = 0,
                height = 0,
                sampler = "",
                CFGScale = 0.0,
                model = "",
                info = ""
            )
        }
    }
}