package com.back.frapuse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.back.frapuse.data.datamodels.ImageMetadata

/**
 * ImageGenDatabase as RoomDatabase for storing contacts
 * */
@Database(entities = [ImageMetadata::class], version = 1)
abstract class ImageGenDatabase : RoomDatabase() {

    /**
     * Abstract value which stores the interface from ImageGenerationDao
     * */
    abstract val imageGenerationDao: ImageGenerationDao
}

/**
 * dbInstance stores the instance of ImageGenDatabase in order to be able to work with it
 * */
private lateinit var INSTANCE: ImageGenDatabase

/**
 * Function to initialise 'imageGenMetadata_table' database
 * @param context Context of application
 * @return INSTANCE as ImageGenDatabase
 * */
fun getDatabase(context: Context): ImageGenDatabase {
    synchronized(ImageGenDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ImageGenDatabase::class.java,
                "imageGenMetadata_table"
            )
                .build()
        }
    }
    return INSTANCE
}