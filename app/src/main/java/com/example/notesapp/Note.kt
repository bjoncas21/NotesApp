package com.example.notesapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val dateCreated: Date = Date(),
    val lastModified: Date = Date(),
    val imageUri: String? = null // New field for image URI
)








