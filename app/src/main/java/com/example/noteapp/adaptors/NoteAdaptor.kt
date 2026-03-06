package com.example.noteapp.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.room.Note

class NoteAdaptor : RecyclerView.Adapter<NoteAdaptor.NoteViewHolder>() {
    private var notesList: MutableList<Note> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        val note = notesList[position]
        holder.textViewTitle.text = note.title
        holder.textViewDescription.text = note.description
        holder.textViewPriority.text = note.priority.toString()
    }

    override fun getItemCount(): Int = notesList.size

    fun setNotes(notes: MutableList<Note>) {
        this.notesList = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle = view.findViewById<TextView>(R.id.text_view_title)
        val textViewDescription = view.findViewById<TextView>(R.id.text_view_description)
        val textViewPriority = view.findViewById<TextView>(R.id.text_view_priority)
    }
}
