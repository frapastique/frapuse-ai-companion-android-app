package com.back.frapuse.data.datamodels.textgen

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textGenChatLibrary_table")
data class TextGenChatLibrary(
    @PrimaryKey(autoGenerate = true)
    val chatID: Long = 0,
    val name: String,
    val profilePicture: String,
    val message: String,
    val sentImage: String,
    //val textGenGenerateRequest: TextGenGenerateRequest
)
