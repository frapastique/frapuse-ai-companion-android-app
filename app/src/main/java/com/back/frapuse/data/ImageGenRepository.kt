package com.back.frapuse.data

import android.util.Log
import com.back.frapuse.data.datamodels.imagegen.ImageBase64
import com.back.frapuse.data.datamodels.imagegen.ImageInfo
import com.back.frapuse.data.datamodels.imagegen.ImageMetadata
import com.back.frapuse.data.datamodels.imagegen.Options
import com.back.frapuse.data.datamodels.imagegen.Progress
import com.back.frapuse.data.datamodels.imagegen.SDModel
import com.back.frapuse.data.datamodels.imagegen.Sampler
import com.back.frapuse.data.datamodels.imagegen.TextToImage
import com.back.frapuse.data.datamodels.imagegen.TextToImageRequest
import com.back.frapuse.data.local.ImageGenDatabase
import com.back.frapuse.data.remote.TextToImageAPI

private const val TAG = "ImageGenRepository"

class ImageGenRepository(private val api: TextToImageAPI, private val database: ImageGenDatabase) {

    /* ____________________________________ Methods Remote _____________________________ */

    suspend fun getModels(): List<SDModel> {
        return try {
            api.retrofitService.getModels()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading models from API: \n\t $e")
            listOf()
        }
    }

    suspend fun getOptions(): Options {
        return try {
            api.retrofitService.getOptions()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading options from API: \n\t $e")
            Options(
                sd_model_checkpoint = ""
            )
        }
    }

    suspend fun startTextToImage(textToImageRequest: TextToImageRequest): TextToImage {
        return try {
            api.retrofitService.startTextToImage(textToImageRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: \n\t $e")
            TextToImage(
                listOf()
            )
        }
    }

    suspend fun getProgress(): Progress {
        return try {
            api.retrofitService.getProgress()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading progress from API: \n\t $e")
            Progress(
                progress = 0.0,
                current_image = null
            )
        }
    }

    suspend fun setOptions(options: Options) {
        try {
            api.retrofitService.setOptions(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting options: \n\t $e")
        }
    }

    suspend fun getImageInfo(imageBase64: ImageBase64): ImageInfo {
        return try {
            api.retrofitService.getImageMetaData(imageBase64)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image info from API: \n\t $e")
            ImageInfo(
                info = ""
            )
        }
    }

    suspend fun getSamplers(): List<Sampler> {
        return try {
            api.retrofitService.getSamplers()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading samplers from API: \n\t $e")
            listOf()
        }
    }

    /* ____________________________________ Methods Local ______________________________ */

    /**
     * Insert new image metadata
     * @param ImageMetadata Image metadata which gets inserted
     * */
    suspend fun insertImage(ImageMetadata: ImageMetadata) {
        try {
            database.imageGenDao.insertImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting image in 'imageGenMetadata_table': \n\t $e")
        }
    }

    /**
     * Delivers all images from the 'imageGenMetadata_table' database
     * @return List -> ImageMetadata
     * */
    suspend fun getAllImages(): List<ImageMetadata> {
        return try {
            database.imageGenDao.getAllImages()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all images from 'imageGenMetadata_table': \n\t $e")
            listOf()
        }
    }

    /**
     * Update metadata of existing image
     * @param ImageMetadata Image metadata which gets updated
     * */
    suspend fun updateImage(ImageMetadata: ImageMetadata) {
        try {
            database.imageGenDao.updateImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating image in 'imageGenMetadata_table': \n\t $e")
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
            Log.e(TAG, "Error getting the size from 'imageGenMetadata_table': \n\t $e")
        }
    }

    /**
     * Delete a selected image metadata from database
     * @param ImageMetadata Image metadata which gets deleted
     * */
    suspend fun deleteImage(ImageMetadata: ImageMetadata) {
        try {
            database.imageGenDao.deleteImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image from 'imageGenMetadata_table': \n\t $e")
        }
    }

    /**
     * Delete all entries from database
     * */
    suspend fun deleteAllImages() {
        try {
            database.imageGenDao.deleteAllImages()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all images from 'imageGenMetadata_table': \n\t $e")
        }
    }

    /**
     * Get selected image from database
     * @param imageID ID of the wanted image
     * @return ImageMetadata
     * */
    suspend fun getImageMetadata(imageID: Long): ImageMetadata {
        return try {
            database.imageGenDao.getImageMetadata(imageID)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching image from 'imageGenMetadata_table': \n\t $e")
            ImageMetadata(
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