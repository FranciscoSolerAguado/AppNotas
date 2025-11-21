package com.fran.appnotas.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R
import com.fran.appnotas.adapter.NotaAdapter
import com.fran.appnotas.databinding.ActivityMainBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: NotaAdapter
    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    private fun initComponents() {
        db = DBHelper(this)
        adapter = NotaAdapter(mutableListOf()) { nota ->
            onNoteClick(nota.id)
        }
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadNotes()

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
        adapter.actualizarLista(lista)
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
        loadNotes()
    }

}
