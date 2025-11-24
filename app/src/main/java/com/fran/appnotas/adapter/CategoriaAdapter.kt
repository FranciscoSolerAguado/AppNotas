package com.fran.appnotas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.databinding.ItemCategoriaBinding
import com.fran.appnotas.model.Categoria

class CategoriaAdapter(
    private var categorias: MutableList<Categoria>,
    private val onItemClicked: (Categoria, String) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria, onItemClicked)
    }

    override fun getItemCount(): Int = categorias.size

    fun actualizarLista(nuevasCategorias: MutableList<Categoria>) {
        categorias = nuevasCategorias
        notifyDataSetChanged()
    }

    class CategoriaViewHolder(private val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria, onItemClicked: (Categoria, String) -> Unit) {
            binding.tvNombreCategoria.text = categoria.name

            binding.btnEliminarCategoria.setOnClickListener {
                onItemClicked(categoria, "delete")
            }

            itemView.setOnClickListener {
                onItemClicked(categoria, "click")
            }
        }
    }
}
