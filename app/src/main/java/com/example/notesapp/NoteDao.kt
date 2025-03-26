package com.example.notesapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    fun getNoteById(noteId: Int): Flow<Note?>

    @Query("SELECT * FROM notes ORDER BY lastModified DESC")
    fun getAllNotesByLastModified(): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY dateCreated DESC")
    fun getAllNotesByDateCreated(): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY LENGTH(content) DESC")
    fun getAllNotesByLength(): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getAllNotesAlphabetically(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun searchNotesByKeyword(keyword: String): Flow<List<Note>>
}


