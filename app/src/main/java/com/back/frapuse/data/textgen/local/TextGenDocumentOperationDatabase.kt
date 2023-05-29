package com.back.frapuse.data.textgen.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.back.frapuse.data.textgen.models.TextGenDocumentOperation

/**
 * TextGenDatabase as RoomDatabase for storing image metadata
 * */
@Database(entities = [TextGenDocumentOperation::class], version = 1)
abstract class TextGenDocumentOperationDatabase : RoomDatabase() {

    /**
     * Abstract value which stores the interface from TextGenDao
     * */
    abstract val textGenDocumentOperationDao: TextGenDocumentOperationDao
}

/**
 * INSTANCE stores the instance of TextGenChatLibraryDatabase in order to be able to work with it
 * */
private lateinit var INSTANCE: TextGenDocumentOperationDatabase

/**
 * Function to initialise 'textGenChatLibrary_table' database
 * @param context Context of application
 * @return INSTANCE as TextGenDatabase
 * */
fun getTextGenDocumentOperationDatabase(context: Context): TextGenDocumentOperationDatabase {
    synchronized(TextGenDocumentOperationDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TextGenDocumentOperationDatabase::class.java,
                "textGenDocumentOperation_table"
            )
                .build()
        }
    }
    return INSTANCE
}