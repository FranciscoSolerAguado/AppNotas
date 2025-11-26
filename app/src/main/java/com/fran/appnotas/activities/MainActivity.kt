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
        loadNotes()

        setupClickListeners()
        setSupportActionBar(binding.toolbar)
    }

    private fun setupAdapters() {
        adapterNotas = NotaAdapter(mutableListOf(),
            onItemClick = { nota -> onNoteClick(nota.id) },
            onStartSelectionMode = { startOrUpdateActionMode() }
        )

        adapterCategoriasMain = CategoriaAdapterMain(mutableListOf()) { categoria ->
            // TODO: Implementar el filtrado de notas por categoría
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
        val lista = db.obtenerNotas()
        adapterNotas.actualizarLista(lista)
    }

    private fun loadCategories() {
        val categorias = db.obtenerCategorias()
        adapterCategoriasMain.actualizarLista(categorias)
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
            actionMode?.invalidate() // Refreshes the menu
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.contextual_notes_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is changed
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
        loadCategories()
        loadNotes()
    }
}
