package com.fran.appnotas.activities

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fran.appnotas.R
import com.fran.appnotas.db.DBHelper
import com.fran.appnotas.model.Nota
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class notaActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private var notaId = -1

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota)

        db = DBHelper(this)

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
        val titulo = etTitle.text.toString().trim()
        val contenido = etContent.text.toString().trim()
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        if (titulo.isEmpty() && contenido.isEmpty()) {
            finish()
            return
        }

        val nota = Nota(
            id = notaId,
            title = titulo,
            content = contenido,
            date = fecha,
            color = "#FFFFFF",
            category = "default"
        )

        if (notaId == -1) {
            db.insertarNota(nota)
        } else {
            db.actualizarNota(nota)
        }

        finish()
    }
}
