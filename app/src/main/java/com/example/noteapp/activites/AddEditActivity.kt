package com.example.noteapp.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteapp.R
import com.example.noteapp.utils.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class AddEditActivity : AppCompatActivity() {
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var numberPickerPriority: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        numberPickerPriority = findViewById(R.id.number_picker_priority)
        numberPickerPriority.minValue = 1
        numberPickerPriority.maxValue = 10

        if (intent.hasExtra(Constants.EXTRA_ID)) {
            title = "Edit Note"
            editTextTitle.setText(intent.getStringExtra(Constants.EXTRA_TITLE))
            editTextDescription.setText(intent.getStringExtra(Constants.EXTRA_DESCRIPTION))
            numberPickerPriority.value = intent.getIntExtra(Constants.EXTRA_PRIORITY, 1)
        } else {
            title = "Add Note"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        val priority = numberPickerPriority.value

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent().apply {
            putExtra(Constants.EXTRA_TITLE, title)
            putExtra(Constants.EXTRA_DESCRIPTION, description)
            putExtra(Constants.EXTRA_PRIORITY, priority)
            val id = intent.getIntExtra(Constants.EXTRA_ID, -1)
            if (id != -1) {
                putExtra(Constants.EXTRA_ID, id)
            }
        }

        val resultCode = if (intent.hasExtra(Constants.EXTRA_ID)) {
            Constants.EDIT_REQUEST_CODE
        } else {
            Constants.REQUEST_CODE
        }

        setResult(resultCode, data)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu_item -> {
                saveNote()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
