package com.example.noteapp.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.adaptors.NoteAdaptor
import com.example.noteapp.room.Note
import com.example.noteapp.room.NoteViewModel
import com.example.noteapp.utils.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NoteAdaptor.OnClickListener {
    private lateinit var noteViewModel: NoteViewModel
    private val notesAdaptor = NoteAdaptor(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var getResults: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addNoteButton = findViewById(R.id.add_note_button)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = notesAdaptor
        recyclerView.layoutManager = LinearLayoutManager(this)
        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[NoteViewModel::class.java]


        noteViewModel.allNotes.observe(this) {
            notesAdaptor.submitList(it)
        }
        getResults =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Constants.REQUEST_CODE) {
                    val title = it.data?.getStringExtra(Constants.EXTRA_TITLE)
                    val description = it.data?.getStringExtra(Constants.EXTRA_DESCRIPTION)
                    val priority = it.data?.getIntExtra(Constants.EXTRA_PRIORITY, -1)

                    val note = Note(title!!, description!!, priority!!)
                    noteViewModel.addNote(note)
                    Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()

                } else if (it.resultCode == Constants.EDIT_REQUEST_CODE) {
                    val title = it.data?.getStringExtra(Constants.EXTRA_TITLE)
                    val description = it.data?.getStringExtra(Constants.EXTRA_DESCRIPTION)
                    val priority = it.data?.getIntExtra(Constants.EXTRA_PRIORITY, -1)
                    val id = it.data?.getIntExtra(Constants.EXTRA_ID, -1)
                    val note = Note(title!!, description!!, priority!!)
                    note.id = id!!
                    noteViewModel.updateNote(note)
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                }

            }
        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            getResults.launch(intent)
        }
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val removedNote = notesAdaptor.getNoteAt(viewHolder.adapterPosition)
                noteViewModel.deleteNote(removedNote)
                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        noteViewModel.addNote(removedNote)
                    }.show()
            }
        }).attachToRecyclerView(recyclerView)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_notes_menu -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClickItem(note: Note) {
        val intent = Intent(this, AddEditActivity::class.java).apply {
            putExtra(Constants.EXTRA_TITLE, note.title)
            putExtra(Constants.EXTRA_DESCRIPTION, note.description)
            putExtra(Constants.EXTRA_PRIORITY, note.priority)
            putExtra(Constants.EXTRA_ID, note.id)
        }
        getResults.launch(intent)
    }
}
