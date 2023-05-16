package com.back.frapuse.data.textgen.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.back.frapuse.data.textgen.models.TextGenDocumentOperation

@Dao
interface TextGenDocumentOperationDao {
    /**
     * Method to insert an element into the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation information which gets inserted
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(textGenDocumentOperation: TextGenDocumentOperation)

    /**
     * Method to load all elements from the 'textGenDocumentOperation_table' database
     * @return List -> TextGenDocumentOperation
     * */
    @Query("SELECT * FROM textGenDocumentOperation_table")
    suspend fun getAllOperations(): List<TextGenDocumentOperation>

    /**
     * Method to update an element in the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation entry which gets updated
     * */
    @Update
    suspend fun updateOperation(textGenDocumentOperation: TextGenDocumentOperation)

    /**
     * Method to get the count of elements from the 'textGenDocumentOperation_table' database
     * @return Int
     * */
    @Query("SELECT COUNT(*) FROM textGenDocumentOperation_table")
    suspend fun getOperationCount(): Int

    /**
     * Method to delete an element in the 'textGenDocumentOperation_table' database
     * @param textGenDocumentOperation Operation entry which gets deleted
     * */
    @Delete
    suspend fun deleteOperation(textGenDocumentOperation: TextGenDocumentOperation)

    /**
     * Method to delete all elements from the 'textGenDocumentOperation_table' database
     * */
    @Query("DELETE FROM textGenDocumentOperation_table")
    suspend fun deleteAllOperations()

    /**
     * Method to get specified element from the 'textGenDocumentOperation_table' database
     * @param id ID of the wanted operation
     * @return TextGenDocumentOperation
     * */
    @Query("SELECT * FROM textGenDocumentOperation_table WHERE id = :id")
    suspend fun getOperation(id: Long): TextGenDocumentOperation
}