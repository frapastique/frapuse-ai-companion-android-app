package com.back.frapuse.data.textgen.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.back.frapuse.data.textgen.models.TextGenChatLibrary

/**
 * TextGenDatabase as RoomDatabase for storing image metadata
 * */
@Database(entities = [TextGenChatLibrary::class], version = 1)
abstract class TextGenChatLibraryDatabase : RoomDatabase() {

    /**
     * Abstract value which stores the interface from TextGenDao
     * */
    abstract val textGenChatDao: TextGenChatDao
}

/**
 * INSTANCE stores the instance of TextGenChatLibraryDatabase in order to be able to work with it
 * */
private lateinit var INSTANCE: TextGenChatLibraryDatabase

/**
 * Function to initialise 'textGenChatLibrary_table' database
 * @param context Context of application
 * @return INSTANCE as TextGenDatabase
 * */
fun getTextGenDatabase(context: Context): TextGenChatLibraryDatabase {
    synchronized(TextGenChatLibraryDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TextGenChatLibraryDatabase::class.java,
                "textGenChatLibrary_table"
            )
                .build()
        }
    }
    return INSTANCE
}