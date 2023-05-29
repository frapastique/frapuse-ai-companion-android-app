package com.back.frapuse.data.textgen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textGenChatLibrary_table")
data class TextGenChatLibrary(
    @PrimaryKey(autoGenerate = true)
    val ID: Long = 0,
    val conversationID: Long,
    var modelName: String = "",
    val dateTime: String,
    var tokens: String = "",
    val type: String,
    var status: Boolean = false,
    val message: String,
    var sentImage: String = "",
    var sentDocument: String = "",
    var documentText: String = "",
    var finalContext: String = "",
)
