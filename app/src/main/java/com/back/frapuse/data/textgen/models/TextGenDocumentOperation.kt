package com.back.frapuse.data.textgen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textGenDocumentOperation_table")
data class TextGenDocumentOperation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var haystackReferences: String = "",
    var meta: String = "",
    var dateTime: String = "",
    var pageCount: Int = 0,
    var path: String = "",
)