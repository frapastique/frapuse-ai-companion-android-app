package com.back.frapuse.data.textgen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textGenChatLibrary_table")
data class TextGenChatLibrary(
    @PrimaryKey(autoGenerate = true)
    val ID: Long = 0,
    val conversationID: Long,
    val modelName: String,
    val dateTime: String,
    val tokens: String,
    val type: String,
    val message: String,
    val sentImage: String,
    val sentDocument: String,
    val documentText: String,
    val finalContext: String,
)
