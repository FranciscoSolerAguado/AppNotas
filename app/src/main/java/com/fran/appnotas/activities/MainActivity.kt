package com.fran.appnotas.activities

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R
import com.fran.appnotas.adapter.CategoriaAdapterMain
import com.fran.appnotas.adapter.NotaAdapter
import com.fran.appnotas.databinding.ActivityMainBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Categoria
import com.fran.appnotas.model.Nota
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DBHelper

    // Adapters
    private lateinit var adapterNotas: NotaAdapter
    private lateinit var adapterCategoriasMain: CategoriaAdapterMain

    // Views
    private lateinit var recyclerViewNotas: RecyclerView
    private lateinit var recyclerViewCategorias: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var categoryIcon: ImageView

    // Action Mode
    private var actionMode: ActionMode? = null

    // Variable para controlar el filtro actual
    private var categoriaSeleccionada: Categoria? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        db = DBHelper(this)

        setupAdapters()
        setupRecyclerViews()

        loadCategories()
        loadNotes() // Carga inicial

        setupClickListeners()
        setSupportActionBar(binding.toolbar)
    }

    private fun setupAdapters() {
        adapterNotas = NotaAdapter(mutableListOf(),
            onItemClick = { nota -> onNoteClick(nota.id) },
            onStartSelectionMode = { startOrUpdateActionMode() }
        )

        adapterCategoriasMain = CategoriaAdapterMain(mutableListOf()) { categoria ->
            // Lógica de Filtrado al hacer click en una categoría
            if (categoriaSeleccionada?.id == categoria.id) {
                // Si pulsamos la que ya estaba seleccionada -> Quitamos filtro
                categoriaSeleccionada = null
                Toast.makeText(this, "Mostrando todas", Toast.LENGTH_SHORT).show()
            } else {
                // Si pulsamos una nueva -> Aplicamos filtro
                categoriaSeleccionada = categoria
                Toast.makeText(this, "Filtro: ${categoria.name}", Toast.LENGTH_SHORT).show()
            }

            // 1. Actualizamos visualmente el adaptador de categorías
            adapterCategoriasMain.setSelectedCategoria(categoriaSeleccionada)

            // 2. Recargamos las notas con el nuevo criterio
            loadNotes()
        }
    }

    private fun setupRecyclerViews() {
        recyclerViewCategorias = binding.recyclerViewCategorias
        recyclerViewCategorias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategorias.adapter = adapterCategoriasMain

        recyclerViewNotas = binding.recyclerViewNotas
        recyclerViewNotas.layoutManager = LinearLayoutManager(this)
        recyclerViewNotas.adapter = adapterNotas
    }

    private fun setupClickListeners() {
        categoryIcon = binding.ivFolderIcon
        categoryIcon.setOnClickListener {
            val i = Intent(this, NuevaCategoriaActivity::class.java)
            startActivity(i)
        }

        floatingActionButton = binding.fabAddBtn
        floatingActionButton.setOnClickListener {
            val i = Intent(this, NotaActivity::class.java)
            startActivity(i)
        }
    }

    private fun loadNotes() {
        // Decidimos qué cargar basándonos en si hay filtro o no
        val lista = if (categoriaSeleccionada == null) {
            db.obtenerNotas() // Carga normal (todas)
        } else {
            db.obtenerNotasPorCategoria(categoriaSeleccionada!!.id) // Carga filtrada
        }

        adapterNotas.actualizarLista(lista)

        // Opcional: Mostrar mensaje si no hay notas en esa categoría
        if (lista.isEmpty() && categoriaSeleccionada != null) {
            Toast.makeText(this, "No hay notas en esta categoría", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCategories() {
        val categorias = db.obtenerCategorias()
        adapterCategoriasMain.actualizarLista(categorias)
        // Aseguramos que se mantenga la selección visual si rotamos pantalla o volvemos
        adapterCategoriasMain.setSelectedCategoria(categoriaSeleccionada)
    }

    private fun onNoteClick(id: Int) {
        val i = Intent(this, NotaActivity::class.java)
        i.putExtra("id", id)
        startActivity(i)
    }

    // --- Action Mode --- //

    private fun startOrUpdateActionMode() {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallback)
        }
        val selectedCount = adapterNotas.selectedItems.size
        if (selectedCount == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$selectedCount seleccionada(s)"
            actionMode?.invalidate()
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.contextual_notes_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    deleteSelectedNotes()
                    mode.finish()
                    true
                }
                R.id.action_move -> {
                    showMoveToCategoryDialog()
                    mode.finish()
                    true
                }
                R.id.action_pin -> {
                    Toast.makeText(this@MainActivity, "Fijar notas (no implementado)", Toast.LENGTH_SHORT).show()
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapterNotas.clearSelection()
            actionMode = null
        }
    }

    private fun deleteSelectedNotes() {
        val selected = adapterNotas.getSelectedNotes()
        selected.forEach { nota ->
            db.eliminarNota(nota.id)
        }
        Toast.makeText(this, "${selected.size} nota(s) eliminada(s)", Toast.LENGTH_SHORT).show()
        loadNotes()
    }

    private fun showMoveToCategoryDialog() {
        val categorias = db.obtenerCategorias()
        val categoriaNombres = categorias.map { it.name }.toTypedArray()
        val selectedNotes = adapterNotas.getSelectedNotes()

        AlertDialog.Builder(this)
            .setTitle("Mover a categoría")
            .setItems(categoriaNombres) { _, which ->
                val categoriaSeleccionada = categorias[which]
                selectedNotes.forEach { nota ->
                    nota.category = categoriaSeleccionada
                    db.actualizarNota(nota)
                }
                Toast.makeText(this, "${selectedNotes.size} nota(s) movida(s) a '${categoriaSeleccionada.name}'", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Recargamos categorías y notas (manteniendo el filtro si existía)
        loadCategories()
        loadNotes()
    }
}
