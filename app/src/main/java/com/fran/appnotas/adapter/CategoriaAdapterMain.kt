package com.fran.appnotas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.databinding.ItemCategoriaMainBinding
import com.fran.appnotas.model.Categoria

class CategoriaAdapterMain(
    private var categorias: MutableList<Categoria>,
    private val onCategoriaClicked: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapterMain.CategoriaMainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaMainViewHolder {
        val binding = ItemCategoriaMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaMainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaMainViewHolder, position: Int) {
        holder.bind(categorias[position], onCategoriaClicked)
    }

    override fun getItemCount(): Int = categorias.size

    fun actualizarLista(nuevasCategorias: List<Categoria>) {
        categorias.clear()
        categorias.addAll(nuevasCategorias)
        notifyDataSetChanged()
    }

    class CategoriaMainViewHolder(private val binding: ItemCategoriaMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria, onCategoriaClicked: (Categoria) -> Unit) {
            binding.chipCategoria.text = categoria.name
            binding.chipCategoria.setOnClickListener {
                onCategoriaClicked(categoria)
            }
        }
    }
}
