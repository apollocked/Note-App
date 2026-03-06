package com.example.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note_table")
     fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY priority ASC")
     fun getAllNotes(): LiveData<MutableList<Note>>


}