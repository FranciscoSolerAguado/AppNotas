package com.fran.appnotas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R
import com.fran.appnotas.model.Nota


class NotaAdapter(
    private var lista: MutableList<Nota>,
    private val onClick: (Nota) -> Unit
) : RecyclerView.Adapter<NotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = lista[position]
        holder.title.text = nota.title
        holder.content.text = nota.content
        holder.date.text = nota.date

        holder.itemView.setOnClickListener { onClick(nota) }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Nota>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
