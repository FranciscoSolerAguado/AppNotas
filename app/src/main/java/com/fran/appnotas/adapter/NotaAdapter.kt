package com.fran.appnotas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.databinding.ItemNotaBinding
import com.fran.appnotas.model.Nota

class NotaAdapter(
    private var notas: MutableList<Nota>,
    private val onItemClick: (Nota) -> Unit,
    private val onStartSelectionMode: () -> Unit
) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    private var isSelectionMode = false
    val selectedItems = mutableSetOf<Nota>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val binding = ItemNotaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        holder.bind(nota, isSelected = selectedItems.contains(nota))
    }

    override fun getItemCount(): Int = notas.size

    fun actualizarLista(nuevasNotas: MutableList<Nota>) {
        notas = nuevasNotas
        notifyDataSetChanged()
    }

    fun toggleSelection(nota: Nota) {
        if (selectedItems.contains(nota)) {
            selectedItems.remove(nota)
        } else {
            selectedItems.add(nota)
        }
        notifyItemChanged(notas.indexOf(nota))
    }

    fun clearSelection() {
        isSelectionMode = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedNotes(): List<Nota> = selectedItems.toList()

    inner class NotaViewHolder(private val binding: ItemNotaBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val nota = notas[position]
                    if (isSelectionMode) {
                        toggleSelection(nota)
                        if (selectedItems.isEmpty()) {
                            isSelectionMode = false
                            // Notificar a la actividad para que finalice el ActionMode
                        }
                        onStartSelectionMode() // Notifica para actualizar el título del ActionMode
                    } else {
                        onItemClick(nota)
                    }
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (!isSelectionMode) {
                        isSelectionMode = true
                        toggleSelection(notas[position])
                        onStartSelectionMode() // Notifica para iniciar el ActionMode
                    }
                    true
                } else {
                    false
                }
            }
        }

        fun bind(nota: Nota, isSelected: Boolean) {
            binding.tvTitle.text = nota.title
            binding.tvContent.text = nota.content
            binding.tvDate.text = nota.date

            if (isSelected) {
                binding.selectionOverlay.visibility = View.VISIBLE
                binding.selectionIcon.visibility = View.VISIBLE
            } else {
                binding.selectionOverlay.visibility = View.GONE
                binding.selectionIcon.visibility = View.GONE
            }
        }
    }
}
