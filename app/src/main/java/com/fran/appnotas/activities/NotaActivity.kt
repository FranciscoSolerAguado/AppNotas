package com.fran.appnotas.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fran.appnotas.databinding.ActivityNotaBinding
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotaBinding
    private lateinit var db: DBHelper
    private var nota: Nota? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DBHelper(this)

        setSupportActionBar(binding.toolbarNota)
        binding.toolbarNota.setNavigationOnClickListener { finish() }

        val notaId = intent.getIntExtra("id", -1)
        if (notaId != -1) {
            // Cargar nota existente
            nota = db.obtenerNotaPorId(notaId)
            nota?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
            }
        } else {
            // Es una nota nueva: el último argumento es null porque no tiene categoría
            nota = Nota(id = 0, title = "", content = "", date = null, color = null, category = null)
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
            currentNota.date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
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
