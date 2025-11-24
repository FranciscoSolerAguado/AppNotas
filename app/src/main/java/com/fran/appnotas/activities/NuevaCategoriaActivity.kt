package com.fran.appnotas.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initRecyclerView()
        loadCategorias()

        binding.fabAddCategoria.setOnClickListener {
            mostrarDialogoNuevaCategoria()
        }
    }

    private fun initRecyclerView() {
        adapter = CategoriaAdapter(mutableListOf()) { categoria, action ->
            if (action == "delete") {
                mostrarDialogoConfirmacion(categoria)
            }
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

    private fun eliminarCategoria(categoria: Categoria) {
        db.eliminarCategoria(categoria.id)
        Toast.makeText(this, "Categoría eliminada", Toast.LENGTH_SHORT).show()
        loadCategorias()
    }

    private fun mostrarDialogoNuevaCategoria() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nueva Categoría")

        val input = EditText(this)
        input.hint = "Nombre de la categoría"
        builder.setView(input)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombreCategoria = input.text.toString().trim()
            if (nombreCategoria.isNotEmpty()) {
                guardarCategoria(nombreCategoria)
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun mostrarDialogoConfirmacion(categoria: Categoria) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro de que quieres eliminar la categoría '${categoria.name}'? Las notas asociadas no se borrarán.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCategoria(categoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
