package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.activites.AddEditActivity
import com.example.noteapp.adaptors.NoteAdaptor
import com.example.noteapp.room.Note
import com.example.noteapp.room.NoteViewModel
import com.example.noteapp.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel
    private val notesAdaptor = NoteAdaptor()
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNoteButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // This adds the height of the status bar/action bar as padding to your layout
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addNoteButton = findViewById(R.id.add_note_button)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = notesAdaptor
        recyclerView.adapter = notesAdaptor
        recyclerView.layoutManager = LinearLayoutManager(this)
noteViewModel = ViewModelProvider(
    this,
    ViewModelProvider.AndroidViewModelFactory(application)
            )[NoteViewModel::class.java]


        noteViewModel.allNotes.observe(this) { list ->
            //here we can add the data to the recycler view
            notesAdaptor.setNotes(list)
        }
        val getResults =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Constants.REQUEST_CODE) {
                    val title = it.data?.getStringExtra(Constants.EXTRA_TITLE)
                    val description = it.data?.getStringExtra(Constants.EXTRA_DESCRIPTION)
                    val priority = it.data?.getIntExtra(Constants.EXTRA_PRIORITY, -1)

                    val note = Note(title!!, description!!, priority!!)
                    noteViewModel.addNote(note)
                    Toast.makeText(
                        this,
                        "Note saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        addNoteButton.setOnClickListener {
            val intent = Intent(
                this@MainActivity, AddEditActivity::class.java
            )
            getResults.launch(intent)
        }

    }
}