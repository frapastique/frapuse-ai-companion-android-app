package com.back.frapuse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.back.frapuse.data.datamodels.ImageMetadata

@Dao
interface ImageGenerationDao {
    /**
     * Method to insert an element into the 'imageGenMetadata_table' database
     * @param ImageMetadata Image metadata which gets inserted
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(ImageMetadata: ImageMetadata)

    /**
     * Method to load all elements from the 'imageGenMetadata_table' database
     * @return List -> ImageMetadata
     * */
    @Query("SELECT * FROM imageGenMetadata_table")
    suspend fun getAllImages(): List<ImageMetadata>

    /**
     * Method to update an element in the 'imageGenMetadata_table' database
     * @param ImageMetadata Image metadata which gets updated
     * */
    @Update
    suspend fun updateImage(ImageMetadata: ImageMetadata)

    /**
     * Method to get the count of images from the 'imageGenMetadata_table' database
     * @return Int
     * */
    @Query("SELECT COUNT(*) FROM imageGenMetadata_table")
    suspend fun getImageCount(): Int

    /**
     * Method to delete an element in the 'imageGenMetadata_table' database
     * @param ImageMetadata Image metadata which gets deleted
     * */
    @Delete
    suspend fun deleteImage(ImageMetadata: ImageMetadata)

    /**
     * Method to delete all elements from the 'imageGenMetadata_table' database
     * */
    @Query("DELETE FROM imageGenMetadata_table")
    suspend fun deleteAllImages()

    /**
     * Method to get specified element from the 'imageGenMetadata_table' database
     * @param imageID ID of the wanted image
     * @return ImageMetadata
     * */
    @Query("SELECT * FROM imageGenMetadata_table WHERE id = :imageID")
    suspend fun getImageMetadata(imageID: Long): ImageMetadata
}