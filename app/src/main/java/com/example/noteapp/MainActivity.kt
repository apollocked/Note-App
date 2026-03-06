package com.example.noteapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.room.NoteViewModel
import com.example.noteapp.adaptors.NoteAdaptor
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val notesAdaptor = NoteAdaptor()
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var addNoteButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        addNoteButton = findViewById(R.id.add_note_button)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = notesAdaptor
        recyclerView.adapter=notesAdaptor
        recyclerView.layoutManager = LinearLayoutManager(this)


        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        noteViewModel.allNotes.observe(this){list ->
            //here we can add the data to the recycler view
            notesAdaptor.setNotes(list)
        }

    }
}