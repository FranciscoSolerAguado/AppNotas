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

    private var selectedCategoriaId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaMainViewHolder {
        val binding = ItemCategoriaMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaMainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaMainViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria, categoria.id == selectedCategoriaId, onCategoriaClicked)
    }

    override fun getItemCount(): Int = categorias.size

    fun actualizarLista(nuevasCategorias: List<Categoria>) {
        categorias.clear()
        categorias.addAll(nuevasCategorias)
        notifyDataSetChanged()
    }

    fun setSelectedCategoria(categoria: Categoria?) {
        val oldSelectedId = selectedCategoriaId
        selectedCategoriaId = categoria?.id

        // Notifica el cambio al elemento que estaba seleccionado antes (si lo había)
        val oldIndex = categorias.indexOfFirst { it.id == oldSelectedId }
        if (oldIndex != -1) {
            notifyItemChanged(oldIndex)
        }

        // Notifica el cambio al nuevo elemento seleccionado (si lo hay)
        val newIndex = categorias.indexOfFirst { it.id == selectedCategoriaId }
        if (newIndex != -1) {
            notifyItemChanged(newIndex)
        }
    }

    class CategoriaMainViewHolder(private val binding: ItemCategoriaMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria, isSelected: Boolean, onCategoriaClicked: (Categoria) -> Unit) {
            binding.chipCategoria.text = categoria.name // Usando .name como definiste
            binding.chipCategoria.isChecked = isSelected
            binding.chipCategoria.setOnClickListener {
                onCategoriaClicked(categoria)
            }
        }
    }
}
