package com.fran.appnotas.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R
import com.fran.appnotas.adapter.NotaAdapter
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.mutableListOf

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NotaAdapter
    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        db = DBHelper(this)

        adapter = NotaAdapter(mutableListOf()) { nota ->
            abrirNota(nota.id)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarNotas()
    }

    private fun obtenerNotasDB(): MutableList<Nota> {
        return db.obtenerNotas()
    }

    private fun cargarNotas() {
        val lista = obtenerNotasDB()
        adapter.actualizarLista(lista)
    }

    private fun abrirNota(id: Int) {
        val i = Intent(this, notaActivity::class.java)
        i.putExtra("id", id)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        cargarNotas()
    }

}
