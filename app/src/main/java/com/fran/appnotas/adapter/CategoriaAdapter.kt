package com.fran.appnotas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R
import com.fran.appnotas.model.Categoria

class CategoriaAdapter(
    private var lista: MutableList<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = lista[position]
        holder.title.text = categoria.name

        holder.itemView.setOnClickListener { onClick(categoria) }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}