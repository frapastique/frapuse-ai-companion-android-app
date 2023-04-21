package com.back.frapuse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.back.frapuse.data.datamodels.ImageMetadata

/**
 * ImageGenerationDatabase as RoomDatabase for storing contacts
 * */
@Database(entities = [ImageMetadata::class], version = 1)
abstract class ImageGenerationDatabase : RoomDatabase() {

    /**
     * Abstract value which stores the interface from ImageGenerationDao
     * */
    abstract val imageGenerationDao: ImageGenerationDao
}

/**
 * dbInstance stores the instance of ImageGenerationDatabase in order to be able to work with it
 * */
private lateinit var INSTANCE: ImageGenerationDatabase

/**
 * Function to initialise 'imageGenMetadata_table' database
 * @param context Context of application
 * @return INSTANCE as ImageGenerationDatabase
 * */
fun getDatabase(context: Context): ImageGenerationDatabase {
    synchronized(ImageGenerationDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ImageGenerationDatabase::class.java,
                "imageGenMetadata_table"
            ).build()
        }
    }
    return INSTANCE
}