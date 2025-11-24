package com.fran.appnotas.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fran.appnotas.adapter.CategoriaAdapter
import com.fran.appnotas.databinding.ActivityNuevaCategoriaBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Categoria

class NuevaCategoriaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNuevaCategoriaBinding
    private lateinit var db: DBHelper
    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevaCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this)

        initRecyclerView()
        loadCategorias()

        binding.btnAddCategoria.setOnClickListener {
            val nombreCategoria = binding.etNombreCategoria.text.toString().trim()
            if (nombreCategoria.isNotEmpty()) {
                guardarCategoria(nombreCategoria)
                binding.etNombreCategoria.text.clear()
            } else {
                Toast.makeText(this, "El nombre de la categoría no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = CategoriaAdapter(mutableListOf()) { categoria ->
            // Aquí puedes añadir una acción al hacer clic en una categoría, si lo necesitas.
        }
        binding.rvCategorias.layoutManager = LinearLayoutManager(this)
        binding.rvCategorias.adapter = adapter
    }

    private fun loadCategorias() {
        val categorias = db.obtenerCategorias()
        adapter.actualizarLista(categorias)
    }

    private fun guardarCategoria(nombre: String) {
        val nuevaCategoria = Categoria(0, nombre)
        val id = db.insertarCategoria(nuevaCategoria)
        if (id > -1) {
            Toast.makeText(this, "Categoría guardada", Toast.LENGTH_SHORT).show()
            loadCategorias() // Recargar la lista
        } else {
            Toast.makeText(this, "Error al guardar la categoría. ¿Quizás ya existe?", Toast.LENGTH_LONG).show()
        }
    }
}
