package com.back.frapuse.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.back.frapuse.data.datamodels.ImageBase64
import com.back.frapuse.data.datamodels.ImageInfo
import com.back.frapuse.data.datamodels.ImageMetadata
import com.back.frapuse.data.datamodels.Options
import com.back.frapuse.data.datamodels.Progress
import com.back.frapuse.data.datamodels.SDModel
import com.back.frapuse.data.datamodels.Sampler
import com.back.frapuse.data.datamodels.TextToImage
import com.back.frapuse.data.datamodels.TextToImageRequest
import com.back.frapuse.data.local.ImageGenDatabase
import com.back.frapuse.data.remote.TextToImageAPI
import kotlinx.coroutines.delay

private const val TAG = "ImageGenerationRepository"

class ImageGenerationRepository(private val api: TextToImageAPI, private val database: ImageGenDatabase) {

    private var _models = MutableLiveData<List<SDModel>>()
    val models: LiveData<List<SDModel>>
        get() = _models

    private var _options = MutableLiveData<Options>()
    val options: LiveData<Options>
        get() = _options

    private var _image = MutableLiveData<TextToImage>()
    val image: LiveData<TextToImage>
        get() = _image

    private var _currentImage = MutableLiveData<String>()
    val currentImage: LiveData<String>
        get() = _currentImage

    private var _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress>
        get() = _progress

    private var _imageInfo = MutableLiveData<ImageInfo>()
    val imageInfo: LiveData<ImageInfo>
        get() = _imageInfo

    private var _samplers = MutableLiveData<List<Sampler>>()
    val samplers: LiveData<List<Sampler>>
        get() = _samplers

    /* ____________________________________ Methods Remote _____________________________ */

    suspend fun getModels() {
        try {
            _models.value = api.retrofitService.getModels()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading models from API: \n\t $e")
        }
    }

    suspend fun getOptions() {
        try {
            _options.value = api.retrofitService.getOptions()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading options from API: \n\t $e")
        }
    }

    suspend fun startTextToImage(textToImageRequest: TextToImageRequest) {
        try {
            _image.value = api.retrofitService.startTextToImage(textToImageRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Data from API: \n\t $e")
        }
    }

    suspend fun getProgress() {
        try {
            _progress.value = api.retrofitService.getProgress()
            try {
                val currentImage = _progress.value!!.current_image
                if (!currentImage.isNullOrEmpty()) {
                    _currentImage.value = currentImage.toString()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress image from API: \n\t $e")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading progress from API: \n\t $e")
        }
    }

    suspend fun setOptions(options: Options) {
        try {
            api.retrofitService.setOptions(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting options: \n\t $e")
        }
    }

    /*suspend fun getImageInfo(imageBase64: ImageBase64) {
        try {
            _imageInfo.value = api.retrofitService.getImageMetaData(imageBase64)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image info from API: \n\t $e")
        }
    }*/

    suspend fun getSamplers() {
        try {
            _samplers.value = api.retrofitService.getSamplers()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading samplers from API: \n\t $e")
        }
    }

    /* ____________________________________ Methods Local ______________________________ */

    /**
     * Insert new image metadata
     * @param ImageMetadata Image metadata which gets inserted
     * */
    suspend fun insertImage(ImageMetadata: ImageMetadata) {
        try {
            database.imageGenerationDao.insertImage(ImageMetadata)
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
            database.imageGenerationDao.getAllImages()
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
            database.imageGenerationDao.updateImage(ImageMetadata)
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
            database.imageGenerationDao.getImageCount()
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
            database.imageGenerationDao.deleteImage(ImageMetadata)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image from 'imageGenMetadata_table': \n\t $e")
        }
    }

    /**
     * Delete all entries from database
     * */
    suspend fun deleteAllImages() {
        try {
            database.imageGenerationDao.deleteAllImages()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all images from 'imageGenMetadata_table': \n\t $e")
        }
    }
}