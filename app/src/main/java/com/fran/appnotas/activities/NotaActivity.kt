package com.fran.appnotas.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.fran.appnotas.R
import com.fran.appnotas.databinding.ActivityNotaBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import com.google.android.material.appbar.MaterialToolbar

class NotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotaBinding

    private var nota: Nota? = null

    private lateinit var db: DBHelper
    private var notaId = -1

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DBHelper(this)

        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            // Cargar nota existente
            nota = db.obtenerNotaPorId(id)
            nota?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
            }
        } else {
            // Es una nota nueva
            nota = Nota(0, "", "", "", "", "")
        }

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarNota)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }
        toolbar.inflateMenu(R.menu.menu_nota)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save -> {
                    guardarNota()
                    true
                }

                else -> false
            }
        }

        notaId = intent.getIntExtra("id", -1)

        if (notaId != -1)
            cargarNota(notaId)
    }

    private fun cargarNota(id: Int) {
        val nota = db.obtenerNotaPorId(id)
        nota?.let {
            etTitle.setText(it.title)
            etContent.setText(it.content)
        }
    }

    private fun guardarNota() {
        val titulo = binding.etTitle.text.toString().trim()
        val contenido = binding.etContent.text.toString().trim()

        // Si la nota actual no es nula
        nota?.let { currentNota ->
            val esNueva = currentNota.id == 0
            val estaVacia = titulo.isEmpty() && contenido.isEmpty()

            if (esNueva && estaVacia) {
                // No guardar si es nueva y está vacía
                return
            }

            if (!esNueva && estaVacia) {
                // Eliminar si es una nota existente que se ha vaciado
                db.eliminarNota(currentNota.id)
                return
            }

            // Actualizar datos de la nota
            currentNota.title = titulo
            currentNota.content = contenido
            currentNota.date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            // Aquí puedes añadir lógica para el color y la categoría si la tienes

            if (esNueva) {
                // Insertar nueva nota y actualizar su ID
                val nuevoId = db.insertarNota(currentNota)
                currentNota.id = nuevoId.toInt()
            } else {
                // Actualizar nota existente
                db.actualizarNota(currentNota)
            }
        }
    }
    override fun onPause() {
        super.onPause()
        guardarNota()
    }
}
