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
import com.example.noteapp.databinding.ActivityMainBinding
import com.example.noteapp.room.Note
import com.example.noteapp.room.NoteViewModel
import com.example.noteapp.utils.Constants
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NoteAdaptor.OnClickListener {
    private lateinit var noteViewModel: NoteViewModel
    private val notesAdapter = NoteAdaptor(this)

    private lateinit var getResults: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupViewModel()
        setupActivityResultLauncher()

        binding.addNoteButton.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            getResults.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val removedNote = notesAdapter.getNoteAt(viewHolder.adapterPosition)
                noteViewModel.deleteNote(removedNote)
                Snackbar.make(binding.recyclerView, R.string.note_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        noteViewModel.addNote(removedNote)
                    }.show()
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupViewModel() {
        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[NoteViewModel::class.java]

        noteViewModel.allNotes.observe(this) { notes ->
            notesAdapter.submitList(notes)
        }
    }

    private fun setupActivityResultLauncher() {
        getResults = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data ?: return@registerForActivityResult
            
            when (result.resultCode) {
                Constants.REQUEST_CODE -> {
                    val note = extractNoteFromIntent(data)
                    noteViewModel.addNote(note)
                    Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show()
                }
                Constants.EDIT_REQUEST_CODE -> {
                    val id = data.getIntExtra(Constants.EXTRA_ID, -1)
                    if (id != -1) {
                        val note = extractNoteFromIntent(data).apply { this.id = id }
                        noteViewModel.updateNote(note)
                        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun extractNoteFromIntent(data: Intent): Note {
        val title = data.getStringExtra(Constants.EXTRA_TITLE) ?: ""
        val description = data.getStringExtra(Constants.EXTRA_DESCRIPTION) ?: ""
        val priority = data.getIntExtra(Constants.EXTRA_PRIORITY, 1)
        return Note(title, description, priority)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_notes_menu -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this, R.string.all_notes_deleted, Toast.LENGTH_SHORT).show()
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
