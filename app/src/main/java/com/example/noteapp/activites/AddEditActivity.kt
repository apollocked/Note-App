package com.example.noteapp.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteapp.R
import com.example.noteapp.databinding.ActivityAddEditBinding
import com.example.noteapp.utils.Constants

class AddEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupPriorityPicker()
        fillDataIfEditing()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun setupPriorityPicker() {
        binding.numberPickerPriority.apply {
            minValue = 1
            maxValue = 10
        }
    }

    private fun fillDataIfEditing() {
        if (intent.hasExtra(Constants.EXTRA_ID)) {
            title = getString(R.string.edit_note)
            binding.editTextTitle.setText(intent.getStringExtra(Constants.EXTRA_TITLE))
            binding.editTextDescription.setText(intent.getStringExtra(Constants.EXTRA_DESCRIPTION))
            binding.numberPickerPriority.value = intent.getIntExtra(Constants.EXTRA_PRIORITY, 1)
        } else {
            title = getString(R.string.add_note)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
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

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val priority = binding.numberPickerPriority.value

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, R.string.please_insert_title_and_description, Toast.LENGTH_SHORT).show()
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
}
