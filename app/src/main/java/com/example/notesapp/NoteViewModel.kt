package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao = NoteDatabase.getDatabase(application).noteDao()

    // Expose the Flow for all notes
    val allNotes: Flow<List<Note>> = noteDao.getAllNotesByLastModified() // Default sorting by lastModified

    // Insert method without suspend, run on IO thread
    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.insert(note)
        }
    }

    // Update method without suspend, run on IO thread
    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.update(note)
        }
    }

    // Delete method without suspend, run on IO thread
    fun delete(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.delete(note)
        }
    }

    // Get a single note by ID, run on a background thread
    suspend fun getNoteById(noteId: Int): Flow<Note?> {
        return withContext(Dispatchers.IO) {
            noteDao.getNoteById(noteId) // Query the note from the DAO in a background thread
        }
    }

    // Function to get notes sorted by date created
    fun getNotesByDateCreated(): Flow<List<Note>> {
        return noteDao.getAllNotesByDateCreated()
    }

    // Function to get notes sorted by note length (body length)
    fun getNotesByLength(): Flow<List<Note>> {
        return noteDao.getAllNotesByLength()
    }

    // Function to get notes sorted alphabetically by title
    fun getNotesAlphabetically(): Flow<List<Note>> {
        return noteDao.getAllNotesAlphabetically()
    }

}

