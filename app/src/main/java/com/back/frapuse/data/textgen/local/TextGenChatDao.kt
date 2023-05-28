package com.back.frapuse.data.textgen.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.back.frapuse.data.textgen.models.llm.TextGenChatLibrary

@Dao
interface TextGenChatDao {
    /**
     * Method to insert an element into the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat information which gets inserted
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(TextGenChatLibrary: TextGenChatLibrary)

    /**
     * Method to load all elements from the 'textGenChatLibrary_table' database
     * @return List -> TextGenChatLibrary
     * */
    @Query("SELECT * FROM textGenChatLibrary_table")
    suspend fun getAllChats(): List<TextGenChatLibrary>

    /**
     * Method to update an element in the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat entry which gets updated
     * */
    @Update
    suspend fun updateChat(TextGenChatLibrary: TextGenChatLibrary)

    /**
     * Method to get the count of elements from the 'textGenChatLibrary_table' database
     * @return Int
     * */
    @Query("SELECT COUNT(*) FROM textGenChatLibrary_table")
    suspend fun getChatCount(): Int

    /**
     * Method to delete an element in the 'textGenChatLibrary_table' database
     * @param TextGenChatLibrary Chat entry which gets deleted
     * */
    @Delete
    suspend fun deleteChat(TextGenChatLibrary: TextGenChatLibrary)

    /**
     * Method to delete all elements from the 'textGenChatLibrary_table' database
     * */
    @Query("DELETE FROM textGenChatLibrary_table")
    suspend fun deleteAllChats()

    /**
     * Method to get specified element from the 'textGenChatLibrary_table' database
     * @param chatID ID of the wanted chat
     * @return TextGenChatLibrary
     * */
    @Query("SELECT * FROM textGenChatLibrary_table WHERE ID = :ID")
    suspend fun getChat(ID: Long): TextGenChatLibrary
}