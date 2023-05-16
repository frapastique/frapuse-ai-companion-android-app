package com.back.frapuse.data.textgen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textGenDocumentOperation_table")
data class TextGenDocumentOperation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentID: Long,
    val modelName: String,
    val dateTime: String,
    val tokens: String,
    val type: String,
    val message: String,
    var status: String,
    val pageCount: Int,
    val currentPage: Int,
    val path: String,
    var context: String
)