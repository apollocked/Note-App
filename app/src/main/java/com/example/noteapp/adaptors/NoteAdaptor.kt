package com.example.noteapp.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.room.Note

class NoteAdaptor(val listener: OnClickListener) :
    ListAdapter<Note, NoteAdaptor.NoteViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.id == newNote.id
            }

            override fun areContentsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.title == newNote.title &&
                        oldNote.description == newNote.description &&
                        oldNote.priority == newNote.priority
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.textViewTitle.text = note.title
        holder.textViewDescription.text = note.description
        holder.textViewPriority.text = note.priority.toString()

        val priorityColor = when (note.priority) {
            in 1..3 -> R.color.priority_low
            in 4..7 -> R.color.priority_medium
            else -> R.color.priority_high
        }
        holder.priorityIndicator.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, priorityColor)
        )
    }

    fun getNoteAt(position: Int): Note {
        return getItem(position)
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
        val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
        val textViewPriority: TextView = view.findViewById(R.id.text_view_priority)
        val priorityIndicator: View = view.findViewById(R.id.priority_indicator)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClickItem(getItem(position))
                }
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(note: Note)
    }
}
