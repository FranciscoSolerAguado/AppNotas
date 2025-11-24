package com.fran.appnotas.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.adapter.CategoriaAdapter
import com.fran.appnotas.adapter.NotaAdapter
import com.fran.appnotas.databinding.ActivityMainBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapterNotas: NotaAdapter
    private lateinit var adapterCategorias: CategoriaAdapter
    private lateinit var db: DBHelper
    private lateinit var recyclerViewNotas: RecyclerView
    private lateinit var recyclerViewCategorias: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var categoryIcon: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        db = DBHelper(this)
        
        // Inicializar adaptadores
        adapterNotas = NotaAdapter(mutableListOf()) { nota ->
            onNoteClick(nota.id)
        }
        adapterCategorias = CategoriaAdapter(mutableListOf()) { categoria, action ->
            // TODO: Implementar el filtrado de notas por categoría
        }

        // Configurar RecyclerView de Categorías
        recyclerViewCategorias = binding.recyclerViewCategorias
        recyclerViewCategorias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategorias.adapter = adapterCategorias

        // Configurar RecyclerView de Notas
        recyclerViewNotas = binding.recyclerViewNotas
        recyclerViewNotas.adapter = adapterNotas
        recyclerViewNotas.layoutManager = LinearLayoutManager(this)

        // Cargar datos
        loadCategories()
        loadNotes()

        categoryIcon = binding.categoryIcon
        onCreateCategoryClick()

        floatingActionButton = binding.fabAddBtn
        onCreateNoteClick()

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
    }

    private fun obtainNotesDB(): MutableList<Nota> {
        return db.obtenerNotas()
    }

    private fun loadNotes() {
        val lista = obtainNotesDB()
        adapterNotas.actualizarLista(lista)
    }

    private fun loadCategories() {
        val categorias = db.obtenerCategorias()
        adapterCategorias.actualizarLista(categorias)
    }

    private fun onCreateCategoryClick() {
        binding.categoryIcon.setOnClickListener {
            val i = Intent(this, NuevaCategoriaActivity::class.java)
            startActivity(i)
        }
    }


    private fun onCreateNoteClick() {
        binding.fabAddBtn.setOnClickListener {
            val i = Intent(this, NotaActivity::class.java)
            startActivity(i)
        }
    }

    private fun onNoteClick(id: Int) {
        val i = Intent(this, NotaActivity::class.java)
        i.putExtra("id", id)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
        loadNotes()
    }

}
