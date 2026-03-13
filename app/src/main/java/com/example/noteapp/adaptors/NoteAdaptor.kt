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

class NoteAdaptor(private val listener: OnClickListener) :
    ListAdapter<Note, NoteAdaptor.NoteViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.id == newNote.id
            }

            override fun areContentsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote == newNote
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getNoteAt(position: Int): Note = getItem(position)

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
        private val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
        private val textViewPriority: TextView = view.findViewById(R.id.text_view_priority)
        private val priorityIndicator: View = view.findViewById(R.id.priority_indicator)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClickItem(getItem(position))
                }
            }
        }

        fun bind(note: Note) {
            textViewTitle.text = note.title
            textViewDescription.text = note.description
            
            val (priorityText, colorRes) = when (note.priority) {
                in 1..3 -> "Low" to R.color.priority_low
                in 4..7 -> "Medium" to R.color.priority_medium
                else -> "High" to R.color.priority_high
            }

            textViewPriority.text = priorityText
            val color = ContextCompat.getColor(itemView.context, colorRes)
            textViewPriority.setTextColor(color)
            priorityIndicator.setBackgroundColor(color)
        }
    }

    interface OnClickListener {
        fun onClickItem(note: Note)
    }
}
